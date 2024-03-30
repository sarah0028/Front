package com.example.entofu.apiInterface

import com.example.entofu.papago.Configs
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

class CreateQuizService {
    val quizService: QuizService = Retrofit.Builder()
        .baseUrl(Configs.serverIp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(QuizService::class.java)
}
interface QuizService{

    @GET("/quiz/ko/{memberIdx}")  // 문제 : 영어 뜻 고르기
    fun getVocabQuizKoRequest(@Path("memberIdx") memberIdx:Long) : Call<GetVocabQuizKoResponse>

    @POST("/quiz/ko/{memberIdx}")
    fun getKoResponse(@Path("memberIdx") memberIdx:Long, @Body body : JsonObject): Call<ResponseBody>

    @GET("/quiz/en/{memberIdx}")  // 영어문제 스펠링 쓰기
    fun getVocabQuizEnRequest(@Path("memberIdx") memberIdx:Long) : Call<ResponseBody>

    @POST("/quiz/en/{memberIdx}")  // 스펠링 보내면 채점해서 리턴
    fun getEnResponse(@Path("memberIdx") memberIdx: Long,@Body body : JsonObject): Call<ResponseBody>
}

class GetVocabQuizKoResponse(
    var english : String,
    var korean : List<String>
)