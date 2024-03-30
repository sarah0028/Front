package com.example.entofu.papago

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PapagoServiceCreator {

    //이 클래스의 역할은 retrofit 빌더를 이용해 Sercice객체를 생성
    private val BASE_URL = "https://openapi.naver.com/v1/papago/" // JSON 출력

    fun create() : PapagoService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PapagoService::class.java)
    }
}