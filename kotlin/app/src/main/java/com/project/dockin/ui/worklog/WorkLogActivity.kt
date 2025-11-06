package com.project.dockin.ui.worklog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.project.dockin.R
import com.project.dockin.data.db.AppDb
import com.project.dockin.data.db.WorkLogLocal
import com.project.dockin.data.sync.SyncWorker
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class WorkLogActivity : AppCompatActivity() {
    private val scope = MainScope()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worklog)

        val etTitle = findViewById<EditText>(R.id.etTitle)
        val etContent = findViewById<EditText>(R.id.etContent)

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            val title = etTitle.text.toString().ifBlank { "(제목 없음)" }
            val content = etContent.text.toString()
            scope.launch {
                val id = AppDb.get(this@WorkLogActivity).workLogDao()
                    .upsert(WorkLogLocal(title = title, content = content))
                Toast.makeText(this@WorkLogActivity, "로컬 저장 완료 #$id", Toast.LENGTH_SHORT).show()

                // 네트워크 가능 시 자동 업로드 시도
                WorkManager.getInstance(this@WorkLogActivity)
                    .enqueue(OneTimeWorkRequestBuilder<SyncWorker>().build())
            }
        }
    }
    override fun onDestroy() { super.onDestroy(); scope.cancel() }
}