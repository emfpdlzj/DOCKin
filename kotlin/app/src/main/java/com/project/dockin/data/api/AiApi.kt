package com.project.dockin.data.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface AiApi {

    // STT 변환 요청  POST /api/worklogs/stt
    @Multipart
    @POST("/api/worklogs/stt")
    suspend fun requestStt(
        @Part file: MultipartBody.Part,
        @Part("lang") lang: RequestBody? = null
    ): SttResponse

    data class SttResponse(
        val text: String
    )

    // 텍스트 번역 요청  POST /api/translate
    @POST("/api/translate")
    suspend fun translate(
        @Body req: TranslateRequest
    ): TranslateResponse

    data class TranslateRequest(
        val text: String,
        val sourceLang: String,
        val targetLang: String
    )

    data class TranslateResponse(
        val translatedText: String
    )
}