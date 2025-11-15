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

    private lateinit var etContent: EditText
    private lateinit var etTranslated: EditText
    private lateinit var btnStt: Button
    private lateinit var btnTranslate: Button
    private lateinit var spSourceLang: Spinner
    private lateinit var spTargetLang: Spinner

    private val PICK_AUDIO = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worklog)

        aiApi = Network.aiApi()

        val tabWrite = findViewById<TextView>(R.id.tabWrite)
        val tabTranslate = findViewById<TextView>(R.id.tabTranslate)
        val layoutWrite = findViewById<View>(R.id.layoutWrite)
        val layoutTranslate = findViewById<View>(R.id.layoutTranslate)

        etContent = findViewById(R.id.etContent)
        etTranslated = findViewById(R.id.etTranslated)
        btnStt = findViewById(R.id.btnStt)
        btnTranslate = findViewById(R.id.btnTranslate)

        spSourceLang = findViewById(R.id.spSourceLang)
        spTargetLang = findViewById(R.id.spTargetLang)

        fun showWriteTab() {
            layoutWrite.visibility = View.VISIBLE
            layoutTranslate.visibility = View.GONE
            tabWrite.setBackgroundColor(Color.WHITE)
            tabTranslate.setBackgroundColor(Color.LTGRAY)
        }

        fun showTranslateTab() {
            layoutWrite.visibility = View.GONE
            layoutTranslate.visibility = View.VISIBLE
            tabWrite.setBackgroundColor(Color.LTGRAY)
            tabTranslate.setBackgroundColor(Color.WHITE)

            val tvSourceText = findViewById<TextView>(R.id.tvSourceText)
            tvSourceText.text = etContent.text.toString()
        }

        tabWrite.setOnClickListener { showWriteTab() }
        tabTranslate.setOnClickListener { showTranslateTab() }
        showWriteTab()

        btnStt.setOnClickListener {
            startAudioPicker()
        }

        btnTranslate.setOnClickListener {
            val text = etContent.text.toString()
            if (text.isBlank()) {
                Toast.makeText(this, "번역할 내용을 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val srcCode = spinnerToLangCode(spSourceLang.selectedItem?.toString())
            val tgtCode = spinnerToLangCode(spTargetLang.selectedItem?.toString())

            scope.launch {
                runCatching {
                    val body = AiApi.TranslateRequest(
                        text = text,
                        source = srcCode,
                        target = tgtCode
                    )
                    aiApi.translate(body)
                }.onSuccess { res ->
                    etTranslated.setText(res.translated)
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

    private fun startAudioPicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "audio/*"
        }
        startActivityForResult(intent, PICK_AUDIO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_AUDIO && resultCode == RESULT_OK) {
            data?.data?.let { uploadForStt(it) }
        }
    }

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
                val old = etContent.text.toString()
                val merged = if (old.isBlank()) res.text else "$old\n${res.text}"
                etContent.setText(merged)
            }.onFailure {
                Toast.makeText(
                    this@WorkLogActivity,
                    "STT 실패: ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun getFileName(uri: Uri): String? {
        var name: String? = null
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val idx = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (it.moveToFirst() && idx >= 0) {
                name = it.getString(idx)
            }
        }
        return name
    }

    private fun spinnerToLangCode(label: String?): String =
        when (label) {
            "한국어", "Korean" -> "ko"
            "영어", "English" -> "en"
            "베트남어", "Tiếng Việt" -> "vi"
            "태국어", "ไทย" -> "th"
            "중국어", "中文" -> "zh"
            else -> "ko"
        }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}