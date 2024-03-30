package com.example.entofu.newScript

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import java.util.ArrayList

object NewScriptRecord {
//    lateinit var speechRecognizer : SpeechRecognizer

    @SuppressLint("ClickableViewAccessibility")
    fun record(view:View,context : Context, editText: EditText , imageView: ImageView) {
        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"eng-ENG")

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle) {}
            override fun onBeginningOfSpeech() {
                editText.hint = "Listening..."
                imageView.setColorFilter(Color.parseColor("#EA2C2C"))
            }

            override fun onRmsChanged(v: Float) {}
            override fun onBufferReceived(bytes: ByteArray) {}
            override fun onEndOfSpeech() {}
            override fun onError(i: Int) {
                speechRecognizer.cancel()
                imageView.setColorFilter(Color.parseColor("#2F86E1"))

                if (i == SpeechRecognizer.ERROR_NO_MATCH) {
                    val snackBar = Snackbar.make(view, "Record again", Snackbar.LENGTH_SHORT)
                    snackBar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_SLIDE
                    snackBar.show()
                }
            }
            override fun onResults(bundle: Bundle) {
                imageView.setColorFilter(Color.parseColor("#2F86E1"))
                val data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                editText.setText(data!![0])
            }

            @SuppressLint("SetTextI18n")
            override fun onPartialResults(bundle: Bundle) {
                val data: ArrayList<String> = bundle.getStringArrayList(
                    SpeechRecognizer.RESULTS_RECOGNITION
                ) as ArrayList<String>
                val unstableData: ArrayList<String> = bundle.getStringArrayList("android.speech.extra.UNSTABLE_TEXT") as ArrayList<String>
                editText.setText(data[0] + unstableData[0])
            }
            override fun onEvent(i: Int, bundle: Bundle) {}
        })


        imageView.setOnTouchListener {_,m ->
            if (m.action == MotionEvent.ACTION_UP){
                speechRecognizer.stopListening()
            }
            if (m.action == MotionEvent.ACTION_DOWN){
                speechRecognizer.startListening(speechRecognizerIntent)
            }
            false
        }

    }


}