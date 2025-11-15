package com.project.dockin.data.api

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object Network {

    // 공통 Moshi 인스턴스 (Kotlin 지원)
    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    /**
     * Spring Boot 서버 (포트 8081)용 Retrofit
     */
    fun retrofit(context: Context): Retrofit {
        val client = OkHttpClient.Builder()
            // TODO: AuthHeaderInterceptor 붙이면 여기
            .build()

        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8081/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    /**
     * AR 메모 등 Spring API용 ArApi
     */
    fun arApi(context: Context): ArApi {
        return retrofit(context).create(ArApi::class.java)
    }

    /**
     * FastAPI 서버 (포트 8000)용 Retrofit – AI 기능
     */
    private val aiRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    fun aiApi(): AiApi = aiRetrofit.create(AiApi::class.java)
}