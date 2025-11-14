package com.project.dockin.ui.worklog

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.dockin.R
import com.project.dockin.data.api.AiApi
import com.project.dockin.data.api.Network
import com.project.dockin.data.api.WorkLogApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.InputStream
import android.media.MediaRecorder
import com.project.dockin.ui.common.BaseActivity

class WorkLogActivity : AppCompatActivity() {

    private val scope = MainScope()
    private lateinit var aiApi: AiApi

    // 탭 관련
    private lateinit var tabWrite: TextView
    private lateinit var tabTranslate: TextView
    private lateinit var layoutWrite: View
    private lateinit var layoutTranslate: View

    // 작성 탭
    private lateinit var etTitle: EditText
    private lateinit var etContent: EditText
    private lateinit var btnStt: Button
    private lateinit var btnSave: Button
    private lateinit var rvSameArea: RecyclerView
    private lateinit var sameAreaAdapter: WorkLogAdapter

    // 번역 탭
    private lateinit var tvSourceText: TextView
    private lateinit var spSourceLang: Spinner
    private lateinit var spTargetLang: Spinner
    private lateinit var btnTranslate: Button
    private lateinit var etTranslated: EditText

    // STT 관련
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording: Boolean = false
    private var recordFile: File? = null

    private val REQ_RECORD_AUDIO = 2001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worklog)

        aiApi = Network.retrofit(this).create(AiApi::class.java)

        // 공통 뷰
        tabWrite = findViewById(R.id.tabWrite)
        tabTranslate = findViewById(R.id.tabTranslate)
        layoutWrite = findViewById(R.id.layoutWrite)
        layoutTranslate = findViewById(R.id.layoutTranslate)

        // 작성 탭 뷰
        etTitle = findViewById(R.id.etTitle)
        etContent = findViewById(R.id.etContent)
        btnStt = findViewById(R.id.btnStt)
        btnSave = findViewById(R.id.btnSave)
        rvSameArea = findViewById(R.id.rvSameAreaWorklogs)

        // 번역 탭 뷰
        tvSourceText = findViewById(R.id.tvSourceText)
        spSourceLang = findViewById(R.id.spSourceLang)
        spTargetLang = findViewById(R.id.spTargetLang)
        btnTranslate = findViewById(R.id.btnTranslate)
        etTranslated = findViewById(R.id.etTranslated)

        initTabs()
        initSameAreaList()
        initLanguageSpinners()
        initButtons()
    }

    private fun initTabs() {
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
            if (tvSourceText.text.isNullOrBlank()) {
                tvSourceText.text = etContent.text.toString()
            }
        }

        tabWrite.setOnClickListener { showWriteTab() }
        tabTranslate.setOnClickListener { showTranslateTab() }

        showWriteTab()
    }

    private fun initSameAreaList() {
        sameAreaAdapter = WorkLogAdapter().apply {
            onItemClick = { item ->
                tvSourceText.text = item.log_text

                layoutWrite.visibility = View.GONE
                layoutTranslate.visibility = View.VISIBLE
                tabWrite.setBackgroundColor(Color.LTGRAY)
                tabTranslate.setBackgroundColor(Color.WHITE)
            }
            onItemDelete = null
        }

        rvSameArea.layoutManager = LinearLayoutManager(this)
        rvSameArea.adapter = sameAreaAdapter

        val workLogApi = Network.retrofit(this).create(WorkLogApi::class.java)
        scope.launch {
            runCatching { workLogApi.list() }
                .onSuccess { list ->
                    sameAreaAdapter.submitList(list)
                }
                .onFailure { e ->
                    Toast.makeText(
                        this@WorkLogActivity,
                        "작업일지 목록 불러오기 실패: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun initLanguageSpinners() {
        val langs = listOf("ko", "en", "vi", "th", "zh")
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            langs
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spSourceLang.adapter = adapter
        spTargetLang.adapter = adapter

        val srcIndex = langs.indexOf("ko").takeIf { it >= 0 } ?: 0
        val tgtIndex = langs.indexOf("en").takeIf { it >= 0 } ?: 0
        spSourceLang.setSelection(srcIndex)
        spTargetLang.setSelection(tgtIndex)
    }

    private fun initButtons() {
        // STT: 한 번 누르면 녹음 시작, 다시 누르면 종료 후 STT 요청
        btnStt.setOnClickListener {
            if (!isRecording) {
                startRecordingWithPermissionCheck()
            } else {
                stopRecordingAndSend()
            }
        }

        // 번역 버튼
        btnTranslate.setOnClickListener {
            val defaultMsg = "작성 탭에서 작업일지 내용을 입력하면 여기 원문이 보입니다."
            val fromSelected = tvSourceText.text?.toString() ?: ""
            val fromEditor = etContent.text.toString().trim()

            val srcText = when {
                fromSelected.isNotBlank() && fromSelected != defaultMsg -> fromSelected
                fromEditor.isNotBlank() -> fromEditor
                else -> {
                    Toast.makeText(
                        this,
                        "번역할 작업일지 내용을 선택하거나 입력하세요.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
            }

            val srcLang = spSourceLang.selectedItem?.toString() ?: "ko"
            val tgtLang = spTargetLang.selectedItem?.toString() ?: "en"

            scope.launch {
                runCatching {
                    aiApi.translate(
                        AiApi.TranslateRequest(
                            text = srcText,
                            sourceLang = srcLang,
                            targetLang = tgtLang
                        )
                    )
                }.onSuccess { res ->
                    etTranslated.setText(res.translatedText)
                }.onFailure { e ->
                    Toast.makeText(
                        this@WorkLogActivity,
                        "번역 실패: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        btnSave.setOnClickListener {
            Toast.makeText(
                this,
                "작업일지 서버 저장은 다음 단계에서 연결할 수 있습니다.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun startRecordingWithPermissionCheck() {
        val granted = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (!granted) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.RECORD_AUDIO),
                REQ_RECORD_AUDIO
            )
            return
        }
        startRecording()
    }

    private fun startRecording() {
        val file = File(cacheDir, "stt_${System.currentTimeMillis()}.m4a")
        recordFile = file

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(file.absolutePath)
            prepare()
            start()
        }

        isRecording = true
        btnStt.text = "음성 입력 종료"
        Toast.makeText(this, "녹음을 시작합니다. 말한 후 다시 버튼을 눌러 종료하세요.", Toast.LENGTH_SHORT).show()
    }

    private fun stopRecordingAndSend() {
        try {
            mediaRecorder?.apply {
                stop()
                reset()
                release()
            }
        } catch (_: Exception) {
        } finally {
            mediaRecorder = null
            isRecording = false
        }

        btnStt.text = "음성 입력 시작"

        val file = recordFile
        if (file == null || !file.exists()) {
            Toast.makeText(this, "녹음 파일이 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        scope.launch {
            runCatching {
                val bytes = file.readBytes()
                val body = RequestBody.create("audio/*".toMediaTypeOrNull(), bytes)
                val part = MultipartBody.Part.createFormData("file", file.name, body)
                val lang = RequestBody.create("text/plain".toMediaTypeOrNull(), "ko")

                aiApi.requestStt(part, lang)
            }.onSuccess { res ->
                val old = etContent.text.toString()
                val merged = if (old.isBlank()) res.text else "$old\n${res.text}"
                etContent.setText(merged)
            }.onFailure { e ->
                Toast.makeText(
                    this@WorkLogActivity,
                    "STT 실패: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_RECORD_AUDIO) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecording()
            } else {
                Toast.makeText(this, "마이크 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isRecording) {
            try {
                mediaRecorder?.stop()
            } catch (_: Exception) {
            }
            mediaRecorder?.release()
            mediaRecorder = null
            isRecording = false
        }
        scope.cancel()
    }


}