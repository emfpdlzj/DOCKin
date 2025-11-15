package com.project.dockin.ui.ar

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.dockin.R
import com.project.dockin.data.api.ArApi
import com.project.dockin.data.api.Network
import com.project.dockin.data.repo.ArRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.project.dockin.ui.common.BaseActivity
class ArMemoActivity : AppCompatActivity() {

    private val viewModel: ArMemoViewModel by viewModels {
        val api: ArApi = Network.arApi(this@ArMemoActivity)
        val repo = ArRepository(api)

        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ArMemoViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return ArMemoViewModel(repo) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    private lateinit var adapter: ArMemoAdapter
    private var equipmentId: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar_memo)

        equipmentId = intent.getIntExtra("equipmentId", 1)

        val tvTitle = findViewById<TextView>(R.id.tvArMemoTitle)
        val tvEquipmentInfo = findViewById<TextView>(R.id.tvEquipmentInfo)
        val rvMemos = findViewById<RecyclerView>(R.id.rvMemos)
        val etMemo = findViewById<EditText>(R.id.etMemo)
        val btnSave = findViewById<Button>(R.id.btnSaveMemo)

        tvTitle.text = "AR 메모 모드"
        tvEquipmentInfo.text = "장비 ID: $equipmentId 메모 목록"

        adapter = ArMemoAdapter(emptyList()) { memo ->
            viewModel.deleteMemo(equipmentId, memo.memoId)
        }

        rvMemos.layoutManager = LinearLayoutManager(this)
        rvMemos.adapter = adapter

        btnSave.setOnClickListener {
            val text = etMemo.text.toString().trim()
            if (text.isEmpty()) {
                Toast.makeText(this, "메모 내용을 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.addMemo(equipmentId, text)
            etMemo.setText("")
        }

        lifecycleScope.launch {
            viewModel.memos.collectLatest { list ->
                Log.d("ArMemoActivity", "memo count = ${list.size}")
                adapter.submitList(list)
            }
        }

        lifecycleScope.launch {
            viewModel.error.collectLatest { message ->
                if (message != null) {
                    Toast.makeText(this@ArMemoActivity, "오류: $message", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.loadMemos(equipmentId)
    }
}