package com.project.dockin.data.api

import android.content.Context
import com.project.dockin.data.api.AiApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object Network {

    // 기존: 스프링 서버(8081)용 Retrofit (토큰 인터셉터 붙은 것)
    fun retrofit(context: Context): Retrofit {
        val client = OkHttpClient.Builder()
            // 여기에 Authorization 헤더 넣는 인터셉터 등 이미 있을 것
            .build()

        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8081/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    // 새로 추가: FastAPI(8000)용 Retrofit (인증 없이 사용)
    private val aiRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/")   // FastAPI 포트 8000
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    fun aiApi(): AiApi = aiRetrofit.create(AiApi::class.java)
}