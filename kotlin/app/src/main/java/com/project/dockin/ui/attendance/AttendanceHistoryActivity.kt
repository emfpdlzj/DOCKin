package com.project.dockin.ui.attendance

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.project.dockin.R
import com.project.dockin.data.api.AttendanceApi
import com.project.dockin.data.api.Network
import kotlinx.coroutines.launch

class AttendanceHistoryActivity : AppCompatActivity() {

    private lateinit var api: AttendanceApi
    private lateinit var adapter: AttendanceAdapter
    private lateinit var swipe: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance_history)

        // Retrofit + API
        val retrofit = Network.retrofit(this)
        api = retrofit.create(AttendanceApi::class.java)

        // View 세팅
        swipe = findViewById(R.id.swipe)
        val rv = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv)

        adapter = AttendanceAdapter()
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        swipe.setOnRefreshListener {
            loadData()
        }

        // 최초 1회 로드
        swipe.isRefreshing = true
        loadData()
    }

    private fun loadData() {
        lifecycleScope.launch {
            runCatching {
                api.getMyAttendance()
            }.onSuccess { list ->
                adapter.submitList(list)
            }.onFailure { e ->
                Toast.makeText(
                    this@AttendanceHistoryActivity,
                    "근태 기록 불러오기 실패: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }.also {
                swipe.isRefreshing = false
            }
        }
    }
}