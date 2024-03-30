package com.example.entofu.apiInterface

import com.example.entofu.papago.Configs
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

class CreateLoginService {
    val loginService: LoginService = Retrofit.Builder()
        .baseUrl(Configs.serverIp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(LoginService::class.java)
}
class CreateSignUpService{
    val signUpService: SignUpService = Retrofit.Builder()
        .baseUrl(Configs.serverIp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(SignUpService::class.java)
}
class CreateMyPageService{
    val myPageService: MyPageService = Retrofit.Builder()
        .baseUrl(Configs.serverIp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(MyPageService::class.java)
}
interface LoginService {
    @POST("/login")
    fun postMemberReq(@Body body: JsonObject) : Call<LoginRes>

    @GET("/member/{idx}")
    fun getMember(@Path("idx") idx : Long) : Call<Member>

    @PATCH("/member/{idx}")  //비밀번호 수정
    fun updateMemberRequest(@Path("idx") idx : Long, @Body body:JsonObject): Call<UpdateMemberRes>

    @PATCH("/member/delete/{idx}")
    fun deleteMemberRequest(@Path("idx") idx : Long) : Call<ResponseBody>
}


interface SignUpService {
    @POST("/sign-up")
    fun postMemberRequest(@Body body : JsonObject) : Call<LoginRes>
}

interface MyPageService{
    @GET("/mypage/{memberIdx}")
    fun getMyPage(@Path("memberIdx") memberIdx:Long) : Call<MyPageDTO>
}
