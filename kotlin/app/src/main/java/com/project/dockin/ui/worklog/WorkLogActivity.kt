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

        val tabWrite = findViewById<TextView>(R.id.tabWrite)
        val tabTranslate = findViewById<TextView>(R.id.tabTranslate)
        val layoutWrite = findViewById<View>(R.id.layoutWrite)
        val layoutTranslate = findViewById<View>(R.id.layoutTranslate)

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
        }

        tabWrite.setOnClickListener { showWriteTab() }
        tabTranslate.setOnClickListener { showTranslateTab() }

        // 시작은 "작성" 탭
        showWriteTab()

        // TODO: rvSameAreaWorklogs 어댑터 붙이고,
        //       btnRequestTranslate 클릭 시 FastAPI 번역 호출만 나중에 연결하면 됨.
    }

    // STT 결과 처리
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
                val lang = RequestBody.create("text/plain".toMediaTypeOrNull(), "ko")

                aiApi.requestStt(part, lang)
            }.onSuccess { res ->
                val old = edtContent.text.toString()
                val merged = if (old.isBlank()) res.text else "$old\n${res.text}"
                edtContent.setText(merged)
            }.onFailure {
                Toast.makeText(this@WorkLogActivity, "STT 실패: ${it.message}", Toast.LENGTH_SHORT).show()
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

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

}