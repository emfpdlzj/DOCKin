package com.project.dockin.ui.worklog

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.project.dockin.R
import com.project.dockin.data.api.AiApi
import com.project.dockin.data.api.Network
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.InputStream
import android.graphics.Color
import android.view.View
import android.widget.TextView

class WorkLogActivity : AppCompatActivity() {

    private val scope = MainScope()
    private lateinit var aiApi: AiApi

    private lateinit var edtContent: EditText
    private lateinit var edtTranslated: EditText
    private lateinit var btnStt: Button
    private lateinit var btnTranslate: Button
    private lateinit var spSrcLang: Spinner
    private lateinit var spTgtLang: Spinner

    private val PICK_AUDIO = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worklog)

        // 1) FastAPI용 AiApi 사용
        aiApi = Network.aiApi()

        val tabWrite = findViewById<TextView>(R.id.tabWrite)
        val tabTranslate = findViewById<TextView>(R.id.tabTranslate)
        val layoutWrite = findViewById<View>(R.id.layoutWrite)
        val layoutTranslate = findViewById<View>(R.id.layoutTranslate)

        edtContent = findViewById(R.id.edtContent)
        edtTranslated = findViewById(R.id.edtTranslated)
        btnStt = findViewById(R.id.btnStt)
        btnTranslate = findViewById(R.id.btnTranslate)

        // xml에서 Spinner id는 spSrcLang / spTgtLang 으로 맞춰둠
        spSrcLang = findViewById(R.id.spSrcLang)
        spTgtLang = findViewById(R.id.spTgtLang)

        // 탭 전환 로직 그대로
        fun showWriteTab() { ... }
        fun showTranslateTab() { ... }
        tabWrite.setOnClickListener { showWriteTab() }
        tabTranslate.setOnClickListener { showTranslateTab() }
        showWriteTab()

        // STT 버튼 -> 오디오 파일 선택
        btnStt.setOnClickListener {
            startAudioPicker()
        }

        // 번역 버튼 -> FastAPI /api/translate 호출
        btnTranslate.setOnClickListener {
            val text = edtContent.text.toString()
            if (text.isBlank()) {
                Toast.makeText(this, "번역할 내용을 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val srcCode = spinnerToLangCode(spSrcLang.selectedItem?.toString())
            val tgtCode = spinnerToLangCode(spTgtLang.selectedItem?.toString())

            scope.launch {
                runCatching {
                    val body = AiApi.TranslateRequest(
                        text = text,
                        source = srcCode,
                        target = tgtCode
                    )
                    aiApi.translate(body)
                }.onSuccess { res ->
                    edtTranslated.setText(res.translated)
                }.onFailure { e ->
                    Toast.makeText(
                        this@WorkLogActivity,
                        "번역 실패: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // 오디오 선택
    private fun startAudioPicker() { ... }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { ... }

    // STT: FastAPI /api/worklogs/stt
    private fun uploadForStt(uri: Uri) {
        scope.launch {
            runCatching {
                val inputStream: InputStream? = contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes() ?: ByteArray(0)

                val fileName = getFileName(uri) ?: "audio.wav"
                val body = RequestBody.create("audio/*".toMediaTypeOrNull(), bytes)
                val part = MultipartBody.Part.createFormData("file", fileName, body)

                aiApi.requestStt(part)
            }.onSuccess { res ->
                val old = edtContent.text.toString()
                val merged = if (old.isBlank()) res.text else "$old\n${res.text}"
                edtContent.setText(merged)
            }.onFailure {
                Toast.makeText(
                    this@WorkLogActivity,
                    "STT 실패: ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // spinner 라벨 -> 언어코드
    private fun spinnerToLangCode(label: String?): String { ... }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}}