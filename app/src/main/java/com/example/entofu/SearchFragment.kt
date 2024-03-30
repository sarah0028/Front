package com.example.entofu

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.entofu.adapter.RecyclerViewAdapter
import com.example.entofu.adapter.VocabularyAdapter
import com.example.entofu.apiInterface.CreateScriptService
import com.example.entofu.apiInterface.CreateVocabularyService
import com.example.entofu.dataItem.ScriptItem
import com.example.entofu.dataItem.Word
import com.example.entofu.databinding.FragmentSearchBinding
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import kotlin.concurrent.thread

class SearchFragment : Fragment() {
    private lateinit var binding : FragmentSearchBinding

    private lateinit var vocabularyRecyclerView : RecyclerView
    private lateinit var vocabularyListAdapter : VocabularyAdapter
    private lateinit var vocabularyList : ArrayList<Word>


    private lateinit var scriptListAdapter : RecyclerViewAdapter
    private lateinit var scriptRecyclerView : RecyclerView
    private lateinit var scriptList : ArrayList<ScriptItem>
    private lateinit var mainAct : MainActivity


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding= FragmentSearchBinding.inflate(inflater)

        firstInit()
        initView()

        if(mainAct.user.isLogin()) {
            loginScriptAdd()
            getMemberVocabulary()
            loginSetAdapter()
        }
        else commonScriptAdd()

        return binding.root
    }

    private fun initView(){
        if(mainAct.user.isLogin()) {
            binding.vocaLinear.visibility = View.GONE
            binding.vocaSearchRV.visibility = View.GONE
            binding.scriptLinear.visibility = View.INVISIBLE
            binding.scriptSearchRV.visibility = View.INVISIBLE
        }
        else{
            binding.vocaLinear.visibility = View.GONE
            binding.vocaSearchRV.visibility = View.GONE
            binding.scriptLinear.visibility = View.INVISIBLE
            binding.scriptSearchRV.visibility = View.INVISIBLE
        }

    }

    private fun firstInit() {
        mainAct = activity as MainActivity
        vocabularyRecyclerView = binding.vocaSearchRV
        vocabularyList = ArrayList()

        scriptRecyclerView=binding.scriptSearchRV
        scriptList = ArrayList()
    }
    private fun addWord(word : String?, meaning: String?,idx: Long?) {
        val item = Word()
        item.setWord(word)
        item.setMeaning(meaning)
        item.setWordIdx(idx)
        vocabularyList.add(item)
    }
    private fun getMemberVocabulary(){
        thread {
            val response = CreateVocabularyService().vocabularyService.getMemberVocabulary(memberIdx = mainAct.user.getIdx()!!).execute()

            CoroutineScope(Dispatchers.Main).launch {
                if (response.isSuccessful) {
                    for(word in response.body()?.data!!){
                        addWord(word.english,word.korean,word.vocabularyIdx)
                    }
                    Log.e("getMemberVocabulary", "success"+response.body().toString())

                } else {
                    Log.e("getMemberVocabulary", "response-fail!")
                    Log.e("getMemberVocabulary", "error code : " + response.code())
                    Log.e("getMemberVocabulary", "error message : " + response.message())
                }
            }
        }
    }
    private fun loginSetAdapter(){

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean = false
            override fun onQueryTextChange(s: String): Boolean {
                if(s.isEmpty()){
                    binding.scriptLinear.visibility = View.INVISIBLE
                    binding.vocaLinear.visibility = View.GONE
                    binding.vocaSearchRV.visibility = View.GONE
                    binding.scriptSearchRV.visibility = View.INVISIBLE
                }
                else{
                    binding.scriptLinear.visibility = View.VISIBLE
                    binding.vocaLinear.visibility = View.VISIBLE
                    binding.vocaSearchRV.visibility = View.VISIBLE
                    binding.scriptSearchRV.visibility = View.VISIBLE
                }
                vocabularyListAdapter.itemFilter.filter(s)
                scriptListAdapter.itemFilter.filter((s))
                return false
            }
        })
        vocabularyListAdapter = VocabularyAdapter(vocabularyList)

        vocabularyRecyclerView.adapter = vocabularyListAdapter
        vocabularyRecyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        vocabularyListAdapter.setItemLongClickListener(object : VocabularyAdapter.OnItemLongClickListener {
            override fun onLongClick(v: View, position: Int): Boolean {

                Snackbar.make(v,"Are you sure you want to delete that word?", Snackbar.LENGTH_LONG).setAction("OK") {

                    CreateVocabularyService().vocabularyService.deleteVocabularyOne(idx = vocabularyList[position].getWordIdx()!!).enqueue(object : retrofit2.Callback<ResponseBody> {
                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            if(response.isSuccessful){

                                vocabularyListAdapter.deleteOne(position)
                                Log.e("deleteVocabularyOne", "success" + response.body()?.string())
                            }
                            else{
                                Log.e("deleteVocabularyOne", "response-fail! :"+response.errorBody()?.string())
                                Log.e("deleteVocabularyOne", "error code : " + response.code())
                                Log.e("deleteVocabularyOne", "error message : " + response.message())
                            }
                        }
                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            t.printStackTrace()
                            Log.e("deleteVocabularyOne", " fail "+ t.message)
                        }
                    })
                }.show()
                return true
            }
        })


        scriptListAdapter = RecyclerViewAdapter(scriptList)

        scriptRecyclerView.adapter = scriptListAdapter
        scriptRecyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        scriptListAdapter.setItemClickListener(object: RecyclerViewAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {

                mainAct.script.setScriptIdx(scriptList[position].getScriptIdx())
                mainAct.script.setStatus(scriptList[position].getStatus())
                mainAct.script.setTitle("${scriptList[position].getTitle()}")
                mainAct.script.setContent("${scriptList[position].getContent()}")
                mainAct.script.setImgUrl(scriptList[position].getImgUrl()!!)
                mainAct.loadFragment(VocabularyFragment())
            }
        })
        scriptListAdapter.setItemLongClickListener(object : RecyclerViewAdapter.OnItemLongClickListener{
            override fun onLongClick(v: View, position: Int) : Boolean {
                if (!mainAct.user.isLogin()) return false
                val deleteService = if (mainAct.user.getUserId()!! == "${R.string.admin_name}") CreateScriptService().scriptService.adminDeleteCommonScript(idx =  scriptList[position].getScriptIdx()!!)
                else CreateScriptService().scriptService.deleteMemberScript(memberIdx = mainAct.user.getIdx()!!,scriptIdx =  scriptList[position].getScriptIdx()!!)


                if (scriptList[position].getStatus() == "MEMBER"||scriptList[position].getStatus() == "SCRAP"){

                    val sb = Snackbar.make(v,"Are you sure you want to delete that script?", Snackbar.LENGTH_LONG).setAction("OK") {

                        Log.e("deleteScriptCheck","memberIdx :  ${mainAct.user.getIdx()} scriptIdx : ${scriptList[position].getScriptIdx()!!}")
                        deleteService.enqueue(object : retrofit2.Callback<ResponseBody> {
                            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                                if(response.isSuccessful){
                                    if(scriptList[position].getStatus() == "MEMBER") scriptListAdapter.deleteOne(position)
                                    else (context as FragmentActivity).supportFragmentManager.beginTransaction().attach(HomeFragment()).commit()
                                    Log.e("deleteScriptCheck", "success" + response.body()?.string())
                                }
                                else{
                                    Log.e("deleteScriptCheck", "response-fail! :"+response.errorBody()?.string())
                                    Log.e("deleteScriptCheck", "error code : " + response.code())
                                    Log.e("deleteScriptCheck", "error message : " + response.message())
                                }
                            }
                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                t.printStackTrace()
                                Log.e("deleteScriptCheck", " fail "+ t.message)
                            }
                        })
                    }
                    sb.animationMode = BaseTransientBottomBar.ANIMATION_MODE_SLIDE
                    sb.show()
                }else return false
                return true
            }
        })
    }
    private fun notLoginSetAdapter(){
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean = false
            override fun onQueryTextChange(s: String): Boolean {

                if(s.isEmpty()){
                    binding.scriptLinear.visibility = View.INVISIBLE
                    binding.scriptSearchRV.visibility = View.INVISIBLE
                }
                else{
                    binding.scriptLinear.visibility = View.VISIBLE
                    binding.scriptSearchRV.visibility = View.VISIBLE
                }
                scriptListAdapter.itemFilter.filter((s))
                return false
            }
        })
        scriptListAdapter = RecyclerViewAdapter(scriptList)

        scriptRecyclerView.adapter = scriptListAdapter
        scriptRecyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        scriptListAdapter.setItemClickListener(object: RecyclerViewAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {

                mainAct.script.setScriptIdx(scriptList[position].getScriptIdx())
                mainAct.script.setStatus(scriptList[position].getStatus())
                mainAct.script.setTitle("${scriptList[position].getTitle()}")
                mainAct.script.setContent("${scriptList[position].getContent()}")
                mainAct.script.setImgUrl(scriptList[position].getImgUrl()!!)
                mainAct.loadFragment(VocabularyFragment())
            }
        })

    }
    private fun scriptOne(status : String?, imgUrl: String?, mainText: String?, content:String?,scriptIdx:Long?) {
        val item = ScriptItem()
        item.setStatus(status)
        if (imgUrl==null) item.setImgUrl(resources.getString(R.string.app_image_url)) else  item.setImgUrl(imgUrl)
        item.setTitle(mainText)
        item.setContent(content)
        item.setScriptIdx(scriptIdx)
//        item.setIsCommon(isCommon)
        scriptList.add(item)
    }
    private fun commonScriptAdd(){
        thread {
            val response = CreateScriptService().scriptService.getCommonScript().execute()
            CoroutineScope(Dispatchers.Main).launch {
                if (response.isSuccessful) {
                    for (scriptOne in response.body()!!.data!!){
                        scriptOne("Common",scriptOne.image,
                            scriptOne.title,scriptOne.content,scriptOne.idx)
                    }
                    notLoginSetAdapter()
                } else {
                    Log.e("commonScriptCheck", "response-fail!")
                    Log.e("commonScriptCheck", "error code : " + response.code())
                    Log.e("commonScriptCheck", "error message : " + response.message())
                }
            }
        }
    }
    private fun memberCommonScriptAdd(){
        thread {
            val response = CreateScriptService().scriptService.getMemberCommonScript(memberIdx = mainAct.user.getIdx()!!).execute()
            CoroutineScope(Dispatchers.Main).launch {
                if (response.isSuccessful) {
                    for (scriptOne in response.body()!!.data!!){
                        scriptOne("COMMON",scriptOne.image,
                            scriptOne.title,scriptOne.content,scriptOne.idx)
                    }
                } else {
                    Log.e("MemberCommonScriptCheck", "response-fail!")
                    Log.e("MemberCommonScriptCheck", "error code : " + response.code())
                    Log.e("MemberCommonScriptCheck", "error message : " + response.message())
                }
            }
        }
    }
    private fun loginScriptAdd(){
        thread {
            val response = CreateScriptService().scriptService.getScrapScript(memberIdx = mainAct.user.getIdx()!!).execute()
            CoroutineScope(Dispatchers.Main).launch {
                if (response.isSuccessful) {
                    for (scriptOne in response.body()!!){
                        scriptOne("SCRAP", scriptOne.img, scriptOne.title,scriptOne.content,scriptOne.scriptIdx)
                    }
                    memberScriptAdd()
                } else {
                    Log.e("ScrapScriptCheck", "response-fail!")
                    Log.e("ScrapScriptCheck", "error code : " + response.code())
                    Log.e("ScrapScriptCheck", "error message : " + response.message())
                }
            }
        }

    }
    private fun memberScriptAdd(){
        thread {
            val response = CreateScriptService().scriptService.getMemberScript(memberIdx = mainAct.user.getIdx()!!).execute()
            CoroutineScope(Dispatchers.Main).launch {
                if (response.isSuccessful) {
                    for (scriptOne in response.body()!!.data!!){
                        scriptOne("MEMBER", resources.getString(R.string.app_image_url),
                            scriptOne.title,scriptOne.content,scriptOne.idx)
                    }
                    memberCommonScriptAdd()
                } else {
                    Log.e("MemberScriptCheck", "response-fail!")
                    Log.e("MemberScriptCheck", "error code : " + response.code())
                    Log.e("MemberScriptCheck", "error message : " + response.message())
                }
            }
        }
    }
}