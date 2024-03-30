package com.example.entofu.quiz

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.entofu.MainActivity
import com.example.entofu.R
import com.example.entofu.apiInterface.CreateQuizService
import com.example.entofu.databinding.FragmentQuiz1Binding
import com.example.entofu.databinding.FragmentQuiz3Binding
import com.example.entofu.newScript.NewScriptRecord.record
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.ArrayList
import kotlin.concurrent.thread

class Quiz3Fragment : Fragment() {

    private lateinit var binding : FragmentQuiz3Binding
    private lateinit var speechRecognizer : SpeechRecognizer
    private lateinit var mainAct : MainActivity

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding= FragmentQuiz3Binding.inflate(inflater)
        mainAct = activity as MainActivity

        getVocabQuizEnRequest()

        binding.voiceimage.setOnClickListener {
            if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
                checkPermission()
            }else{ record() }
        }

        return binding.root
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.RECORD_AUDIO), 3)
        }
    }
    private fun getVocabQuizEnRequest(){
        thread{
            val response = CreateQuizService().quizService.getVocabQuizEnRequest(memberIdx= mainAct.user.getIdx()!!).execute()
            CoroutineScope(Dispatchers.Main).launch {
                if (response.isSuccessful) {

                    binding.voiceWord.text = response.body()!!.string()
                    binding.voiceText.text = ""
                    Log.e("getVocabQuizEnRequest", "success :"+ response.body()!!.string())

                } else {
                    Log.e("getVocabQuizEnRequest", "response-fail!")
                    Log.e("getVocabQuizEnRequest", "error code : " + response.code())
                    Log.e("getVocabQuizEnRequest", "error message : " + response.message())

                }
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
    }
    @Deprecated("Deprecated in Java")
    @SuppressLint("ClickableViewAccessibility")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // check condition
        if (requestCode == 3 && grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            record()
        }
        else Snackbar.make(requireActivity().findViewById(android.R.id.content),"Permission Denied", Snackbar.LENGTH_LONG).show()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun record() {
        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"eng-ENG")

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle) {}
            override fun onBeginningOfSpeech() {
                binding.voiceText.text = "Listening..."
                binding.voiceimage.setColorFilter(Color.parseColor("#EA2C2C"))
            }

            override fun onRmsChanged(v: Float) {}
            override fun onBufferReceived(bytes: ByteArray) {}
            override fun onEndOfSpeech() {}
            override fun onError(i: Int) {
                speechRecognizer.cancel()
                binding.voiceimage.setColorFilter(Color.parseColor("#000000"))

                if (i == SpeechRecognizer.ERROR_NO_MATCH) Snackbar.make(requireActivity().findViewById(android.R.id.content),"Record again", Snackbar.LENGTH_LONG).show()
            }
            override fun onResults(bundle: Bundle) {
                binding.voiceimage.setColorFilter(Color.parseColor("#000000"))
                val data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                binding.voiceText.text=data!![0]
                getVocabQuizEnResponse(data[0])
            }

            @SuppressLint("SetTextI18n")
            override fun onPartialResults(bundle: Bundle) {
                val data: ArrayList<String> = bundle.getStringArrayList(
                    SpeechRecognizer.RESULTS_RECOGNITION
                ) as ArrayList<String>
                val unstableData: ArrayList<String> =
                    bundle.getStringArrayList("android.speech.extra.UNSTABLE_TEXT") as ArrayList<String>
                binding.voiceText.text = data[0] + unstableData[0]
            }
            override fun onEvent(i: Int, bundle: Bundle) {}
        })
        binding.voiceimage.setOnTouchListener { _, m ->
            if (m.action == MotionEvent.ACTION_UP){
                speechRecognizer.stopListening();
            }
            if (m.action == MotionEvent.ACTION_DOWN){
                speechRecognizer.startListening(speechRecognizerIntent)
            }
            false
        }
    }
    private fun getVocabQuizEnResponse(answer: String){
        Log.e("getVocabQuizEnResponse", "answer :$answer")
        val json = JSONObject("""{"question":"${binding.voiceWord.text}", "answer":"$answer"}""")
        thread{
            val response = CreateQuizService().quizService.getEnResponse(memberIdx = mainAct.user.getIdx()!!,body =  JsonParser.parseString(json.toString()) as JsonObject).execute()
            CoroutineScope(Dispatchers.Main).launch {
                if (response.isSuccessful) {
                    Snackbar.make(requireActivity().findViewById(android.R.id.content),response.body()!!.string(), Snackbar.LENGTH_LONG).show()
                    nextVoiceQuiz()
                    Log.e("getVocabQuizEnResponse", "success ! :"+ response.body()!!.string())

                } else {
                    Log.e("getVocabQuizEnResponse", "response-fail!")
                    Log.e("getVocabQuizEnResponse", "error code : " + response.code())
                    Log.e("getVocabQuizEnResponse", "error message : " + response.message())
                }
            }
        }
    }
    private fun nextVoiceQuiz(){
        getVocabQuizEnRequest()
        binding.voiceText.text = "Record your pronunciation"
    }
}