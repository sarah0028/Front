package com.example.entofu.learn

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.PackageManagerCompat
import com.example.entofu.papago.PapagoEntity
import com.example.entofu.papago.PapagoServiceCreator
import retrofit2.Call
import retrofit2.Response

object Translation {
    fun makeToastOfTranslation(context : Context, str:String) {
        val papagoService = PapagoServiceCreator().create()
        val call = papagoService.requestTranslation(text=str)
        call.enqueue(object : retrofit2.Callback<PapagoEntity> {
            @SuppressLint("RestrictedApi")
            override fun onResponse(call: Call<PapagoEntity>, response: Response<PapagoEntity>){
                if(response.isSuccessful) {// 성공
                    // Log.d(LOG_TAG, "Successful!")

                    val result = response.body()
                    //text.setText(result?.message?.result?.translatedText!!)
//                    CustomToast.createToast(context, result?.message?.result?.translatedText!!)?.show()

                    Toast.makeText(context,result?.message?.result?.translatedText!!, Toast.LENGTH_SHORT).show()
                }
                else { // 서버에 연결은 됐으나 결과 받기 실패
                    Log.e(PackageManagerCompat.LOG_TAG, "fail!")
                    Log.e(PackageManagerCompat.LOG_TAG, "error code : " + response.code())
                    Log.e(PackageManagerCompat.LOG_TAG, "error message : " + response.message())
                }
            }
            // 서버 연결 실패
            @SuppressLint("RestrictedApi")
            override fun onFailure(call: Call<PapagoEntity>, t: Throwable) {
                Log.d(PackageManagerCompat.LOG_TAG, "onFailure!")
                t.printStackTrace()
            }
        })
    }
    fun fullTranslation(tv : TextView, str:String) {

        val sentenceList = str.split(".")
        // var fullstr : String = ""

        val papagoService = PapagoServiceCreator().create()
        val call = papagoService.requestTranslation(text=str)
        call.enqueue(object : retrofit2.Callback<PapagoEntity> {
            @SuppressLint("RestrictedApi")

            override fun onResponse(call: Call<PapagoEntity>, response: Response<PapagoEntity>){
                if(response.isSuccessful) {// 성공
                    // Log.d(LOG_TAG, "Successful!")

                    val result = response.body()
                    val sentenceResult = result?.message?.result?.translatedText!!.split(".")
                    val stringBuilder = StringBuilder()

                    sentenceList.zip(sentenceResult).forEach {pair ->
                        stringBuilder.append(pair.component1()).appendLine()
                        stringBuilder.append(pair.component2()).appendLine()
                    }
                    tv.text = stringBuilder

                }
                else { // 서버에 연결은 됐으나 결과 받기 실패
                    Log.e(PackageManagerCompat.LOG_TAG, "fail!")
                    Log.e(PackageManagerCompat.LOG_TAG, "error code : " + response.code())
                    Log.e(PackageManagerCompat.LOG_TAG, "error message : " + response.message())
                }
            }
            // 서버 연결 실패
            @SuppressLint("RestrictedApi")
            override fun onFailure(call: Call<PapagoEntity>, t: Throwable) {
                Log.d(PackageManagerCompat.LOG_TAG, "onFailure!")
                t.printStackTrace()
            }
        })
    }
}