package com.project.dockin.ui.worklog

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.collectLatest
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.project.dockin.R
import com.project.dockin.data.db.AppDb
import com.project.dockin.data.sync.SyncWorker
import kotlinx.coroutines.launch
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class WorkLogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worklog) // 이미 만든 레이아웃
    }
}

class WorkLogListActivity : AppCompatActivity() {
    private val adapter = WorkLogAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worklog_list)

        val rv = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv)
        val swipe = findViewById<androidx.
        swiperefreshlayout.widget.SwipeRefreshLayout>(R.id.swipe)

        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        // Room Flow 관찰 → 자동 갱신
        val dao = AppDb.get(this).workLogDao()

        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                dao.observeAll().collectLatest { list ->
                    adapter.submitList(list)
                }
            }
        }

        // 아래로 당겨 동기화(업로드) 트리거
        swipe.setOnRefreshListener {
            WorkManager.getInstance(this)
                .enqueue(OneTimeWorkRequestBuilder<SyncWorker>().build())
            swipe.isRefreshing = false
        }
    }
}