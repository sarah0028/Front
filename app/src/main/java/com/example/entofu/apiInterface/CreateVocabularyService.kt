package com.example.entofu.apiInterface

import com.example.entofu.papago.Configs
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

class CreateVocabularyService {
    val vocabularyService: VocabularyService = Retrofit.Builder()
        .baseUrl(Configs.serverIp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(VocabularyService::class.java)
}

interface VocabularyService{
    @POST("/vocabulary/{memberIdx}")
    fun postVocabularyRequest(@Path("memberIdx") memberIdx:Long, @Body body : JsonObject) : Call<ResponseBody>

    @GET("/vocabulary")
    fun getMemberVocabulary(@Query("memberIdx") memberIdx : Long) : Call<VocabularyDto>

    @GET("/vocabulary")
    fun getMemberVocabularyFromKor(@Query("memberIdx") memberIdx:Long, @Query("korean") korean: String)

    @DELETE("/vocabulary/delete/{idx}")
    fun deleteVocabularyOne(@Path("idx") idx: Long) : Call<ResponseBody>

}

class VocabularyDto{
    var data : ArrayList<VocabularyOne>? = null

    inner class VocabularyOne(
        var vocabularyIdx: Long,
        var memberIdx : Long,
        var english : String,
        var korean: String,
        var part : String
    )
}