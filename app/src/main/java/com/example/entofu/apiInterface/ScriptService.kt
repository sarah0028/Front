package com.example.entofu.apiInterface

import com.example.entofu.papago.Configs
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.File

class CreateScriptService {
    val scriptService: ScriptService = Retrofit.Builder()
        .baseUrl(Configs.serverIp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ScriptService::class.java)
}

interface ScriptService {
    @GET("/common")   //공통기사 조회
    fun getCommonScript(): Call<Script>

    @GET("/common")   //멤버공통기사 조회
    fun getMemberCommonScript(@Query("memberIdx") memberIdx: Long): Call<Script>

    @GET("/scripts/member/{memberIdx}")  //멤버 스크립트 조회
    fun getMemberScript(@Path("memberIdx") memberIdx: Long): Call<Script>

    @GET("/scraps/member")  //멤버별 스크랩 조회
    fun getScrapScript(@Query("memberIdx") memberIdx: Long): Call<ArrayList<ScrapScript>>

    @POST("/text/{memberIdx}")  //회원 기사 등록
    fun postMemberScriptRequest(
        @Path("memberIdx") memberIdx: Long,
        @Body body: JsonObject
    ): Call<PostMemberScriptRes>

    @POST("/scraps/common")  // 공통기사 스크랩
    fun scrapCommonScript(@Body body: JsonObject): Call<ResponseBody>

    @POST("/scraps/{memberIdx}")  //멤버 스크립트 스크랩
    fun postMemberScrapReq(
        @Path("memberIdx") memberIdx: Long,
        @Body body: JsonObject
    ): Call<ResponseBody>

    @DELETE("/scraps/delete/{memberIdx}/{scriptIdx}")  // 스크랩 삭제
    fun deleteScrap(
        @Path("memberIdx") memberIdx: Long,
        @Path("scriptIdx") scriptIdx: Long
    ): Call<ResponseBody>

//    @PATCH("/scripts/delete/{idx}")  //스크립트 삭제
//    fun deleteScript(@Path("idx") idx : Long) : Call<ResponseBody>

    @DELETE("/scripts/delete/{memberIdx}/{scriptIdx}")  // 멤버 스크립트 삭제
    fun deleteMemberScript(
        @Path("memberIdx") memberIdx: Long,
        @Path("scriptIdx") scriptIdx: Long
    ): Call<ResponseBody>

    @DELETE("/admin/scripts/delete/{idx}")  // admin 공통 스크립트 삭제
    fun adminDeleteCommonScript(@Path("idx") idx: Long): Call<ResponseBody>

    @Multipart
    @POST("/pdf/{memberIdx}")
    fun postPdfRequest(@Path("memberIdx") memberIdx: Long, @Part files: MultipartBody.Part): Call<ResponseBody>

    @Multipart
    @POST("/txt/{memberIdx}")
    fun postTxtRequest(@Path("memberIdx") memberIdx: Long, @Part files: MultipartBody.Part): Call<ResponseBody>
}