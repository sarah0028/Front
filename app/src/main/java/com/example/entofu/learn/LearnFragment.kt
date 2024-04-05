package com.example.entofu.learn

import android.annotation.SuppressLint
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.SpannableString
import android.util.Log
import android.view.GestureDetector
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import com.bumptech.glide.Glide
import com.example.entofu.HomeFragment
import com.example.entofu.MainActivity
import com.example.entofu.R
import com.example.entofu.apiInterface.CreateScriptService
import com.example.entofu.apiInterface.CreateVocabularyService
import com.example.entofu.databinding.FragmentLearnBinding
import com.example.entofu.learn.Translation.fullTranslation
import com.example.entofu.loginService.MyPageFragment
import com.example.entofu.updateHome
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.util.Locale
import kotlin.concurrent.thread
import kotlin.math.abs

class LearnFragment : Fragment() {

    private lateinit var binding : FragmentLearnBinding
    private lateinit var mainAct : MainActivity

    private var clicked = false
    private var speak = false
    private var transButtonClick = false
    private var tts : TextToSpeech? = null
    private lateinit var gestureDetector : GestureDetector

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding= FragmentLearnBinding.inflate(inflater)
        mainAct = activity as MainActivity

        Glide.with(binding.root).load(mainAct.script.getImgUrl()).into(binding.scriptImg)
        binding.title.text= mainAct.script.getTitle()
        binding.content.text = mainAct.script.getContent()

        tts = TextToSpeech(activity, TextToSpeech.OnInitListener {
            if (it == TextToSpeech.SUCCESS) {
                val result = tts!!.setLanguage(Locale.ENGLISH)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS","해당언어는 지원되지 않습니다.")
                    return@OnInitListener
                }
            }
        })


        val toolbar = mainAct.activityMainBinding!!.toolBar
        mainAct.setSupportActionBar(toolbar)
        setHasOptionsMenu(true)
        mainAct.supportActionBar?.title=""
        mainAct.supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val spannable = SpannableString(binding.content.text.toString())
        gestureDetector = GestureDetector(activity, object : GestureDetector.OnGestureListener {
            var x = 0F
            var y = 0F
            var offset= 0
            var offset_up = 0
            val SWIPE_THRESHOLD = 50
            val SWIPE_VELOCITY_THRESHOLD = 25

            override fun onDown(m: MotionEvent): Boolean {
                x = m.x + binding.content.scrollX
                y = m.y + binding.content.scrollY
                offset = binding.content.getOffsetForPosition(x, y)
                return true
            }
            override fun onShowPress(e: MotionEvent) {}
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                // 클릭 오류 단어 제외
                try {
                    val char = binding.content.text[offset]
                    return if (char == ',' || char == '-' || char == ' ' || char == ';' || char == '(' || char == ')' || char == '.' || char == '\'' || char.isDigit())
                        false
                    else {
                        Translation.makeToastOfTranslation(requireContext(), getOffsetToWord(binding.content, offset, offset))
                        speakOut(getOffsetToWord(binding.content, offset, offset))
                        true
                    }
                } catch (e: StringIndexOutOfBoundsException) {
                    makeSnackBar("please click on the alphabet")
                    return false
                }

            }
            override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean = false
            override fun onLongPress(e: MotionEvent) {  // 단어장에 단어 추가
                if (mainAct.user.isLogin()) postVocabularyRequest(getOffsetToWord(binding.content,offset,offset))
                else makeSnackBar("Non-members cannot use the vocabulary.")
            }
            override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                var result = false
                val diffY = e2.y - e1!!.y
                val diffX = e2.x - e1.x
                offset_up = binding.content.getOffsetForPosition(e2.x, e2.y)


                if (abs(diffX) > abs(diffY))
                    if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) Highlighter.highlight(binding.content,spannable,offset,offset_up)   // swipe Right
                        else Highlighter.highlight(binding.content,spannable,offset_up,offset)            // swipe Left
                        result = true
                    }
                return result
            }

        })

        binding.content.setOnTouchListener{_,event->
            gestureDetector.onTouchEvent(event)
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_learn,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home ->{
                mainAct.loadFragment(HomeFragment())
            }
            R.id.toolbar_my_info ->{
                if(mainAct.user.isLogin()) mainAct.loadFragment(MyPageFragment())
                else mainAct.makeDialogNonMember(requireContext())
            }
            R.id.toolbar_speaker ->{
                if(speak){
                    tts!!.stop()
                    speak = !speak
                }
                else {
                    tts!!.setSpeechRate(1.6F)
                    tts!!.setPitch(1F)
                    speak = !speak
                    val strList = mainAct.script.getContent()!!.split('.')
                    for (i in strList.indices) tts!!.speak(strList[i], TextToSpeech.QUEUE_ADD, null,"")

                }
            }
            R.id.toolbar_bookmark ->{
                if (mainAct.user.isLogin()){
                    when(mainAct.script.getStatus()){
                        "SCRAP"->{
                            AlertDialog.Builder(requireContext())
                                .setTitle("Check")
                                .setMessage("Are you sure you want to cancel the scrap?")
                                .setPositiveButton("Ok"){_,_ ->
                                    deleteScrap()
                                    mainAct.activityMainBinding?.toolBar?.menu?.get(2)?.setIcon(R.drawable.bookmark_none)
                                    updateHome =true
                                }
                                .setNegativeButton("No",null)
                                .show()
                        }
                        "MEMBER"->{
                            memberScrap()
                            mainAct.activityMainBinding?.toolBar?.menu?.get(2)?.setIcon(R.drawable.bookmark_added)
                            mainAct.script.setStatus("SCRAP")
//                        refresh=true

                        }
                        "COMMON"->{
                            commonScrap()
                            mainAct.activityMainBinding?.toolBar?.menu?.get(2)?.setIcon(R.drawable.bookmark_added)
                            mainAct.script.setStatus("SCRAP")
                        }
                    }
                }
                else {
                    AlertDialog.Builder(requireContext())
                        .setTitle("NOTION")
                        .setMessage("Non-members can not use scrap.\n Please login in")
                        .setPositiveButton("Ok",null)
                        .show()
                }
            }
            R.id.toolbar_trans ->{
                if (!transButtonClick) {
                    fullTranslation(binding.content,binding.content.text.toString())
                    binding.content.setOnTouchListener(null)
                    transButtonClick = !transButtonClick
                }
                else {
                    binding.content.text = mainAct.script.getContent()
                    binding.content.setOnTouchListener{_,event->
                        gestureDetector.onTouchEvent(event)
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun getOffsetToWord(tv: TextView, offset:Int, end:Int) : String {
        var calOffset = offset
        var calEnd = end
        while (tv.text[calEnd] in 'a'..'z' || tv.text[calEnd] in 'A'..'Z') calEnd += 1
        while (tv.text[calOffset] in 'a'..'z' || tv.text[calOffset] in 'A'..'Z') calOffset -= 1
        return tv.text.toString().substring(calOffset + 1, calEnd)
    }

    private fun speakOut(targetStr : String) {
        tts!!.setPitch(1F) // 음성 톤 높이 지정
        tts!!.setSpeechRate(1.0F) // 음성 속도 지정
        tts!!.speak(targetStr, TextToSpeech.QUEUE_FLUSH, null,"")
    }
    override fun onDestroy() {
        super.onDestroy()
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
            tts = null
        }
    }
    private fun memberScrap(){
        Log.e("scrapCheck", "scriptIdx : ${mainAct.script.getScriptIdx()}")

        thread {
            val json = JSONObject("""{"scriptIdx":"${mainAct.script.getScriptIdx()}"}""")
            val response = CreateScriptService().scriptService.postMemberScrapReq(memberIdx = mainAct.user.getIdx()!!,
                body = JsonParser.parseString(json.toString()) as JsonObject
            ).execute()

            CoroutineScope(Dispatchers.Main).launch {
                if (response.isSuccessful) {
                    updateHome = true
                    makeSnackBar("Scraped successfully")
                    Log.e("scrapCheck", "success"+response.body()?.string())

                } else {
                    Log.e("scrapCheck", "response-fail!")
                    Log.e("scrapCheck", "error code : " + response.code())
                    Log.e("scrapCheck", "error message : " + response.message())
                }
            }
        }
    }
    private fun commonScrap(){
        Log.e("CommonScrapCheck", "scriptidx : ${mainAct.script.getScriptIdx()}")

        thread {
            val json = JSONObject("""{"scriptIdx":"${mainAct.script.getScriptIdx()}","memberIdx":"${mainAct.user.getIdx()}"}""")
            val response = CreateScriptService().scriptService.scrapCommonScript(
                body = JsonParser.parseString(json.toString()) as JsonObject
            ).execute()

            CoroutineScope(Dispatchers.Main).launch {
                if (response.isSuccessful) {
                    updateHome = true
                    makeSnackBar("Scraped successfully")
                    Log.e("CommonScrapCheck", "success"+response.body()?.string())

                } else {
                    Log.e("CommonScrapCheck", "response-fail!")
                    Log.e("CommonScrapCheck", "error code : " + response.code())
                    Log.e("CommonScrapCheck", "error message : " + response.message())
                }
            }
        }
    }
    private fun deleteScrap(){
        Log.e("deleteScrapCheck", "scriptidx : ${mainAct.script.getScriptIdx()}")

        thread {
            val response = CreateScriptService().scriptService.deleteScrap(
                memberIdx = mainAct.user.getIdx()!!,scriptIdx= mainAct.script.getScriptIdx()!!).execute()

            CoroutineScope(Dispatchers.Main).launch {
                if (response.isSuccessful) {
                    updateHome = true
                    Log.e("deleteScrapCheck", "success"+response.body()?.string())

                } else {
                    Log.e("deleteScrapCheck", "response-fail!")
                    Log.e("deleteScrapCheck", "error code : " + response.code())
                    Log.e("deleteScrapCheck", "error message : " + response.message())
                }
            }
        }
    }

    private fun postVocabularyRequest(word:String){

        val json = JSONObject("""{"english":"$word"}""")
        makeSnackBar("Word saved successfully")

        CreateVocabularyService().vocabularyService.postVocabularyRequest(memberIdx = mainAct.user.getIdx()!!,body = JsonParser.parseString(json.toString()) as JsonObject).enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.isSuccessful){
                    Log.e("postVocabularyRequest", "success" + response.body()?.string())
                }
                else{
                    Log.e("postVocabularyRequest", "response-fail! :"+response.errorBody()?.string())
                    Log.e("postVocabularyRequest", "error code : " + response.code())
                    Log.e("postVocabularyRequest", "error message : " + response.message())
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
                Log.e("postVocabularyRequest", " fail "+ t.message)
            }
        })
    }
    fun makeSnackBar(string: String){
        Snackbar.make(requireActivity().findViewById(android.R.id.content),string, Snackbar.LENGTH_LONG).show()
    }
}