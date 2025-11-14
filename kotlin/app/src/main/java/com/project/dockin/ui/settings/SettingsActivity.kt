package com.project.dockin.ui.settings

import android.os.Bundle
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import com.project.dockin.R
import com.project.dockin.data.pref.SessionStore
import com.project.dockin.ui.common.BaseActivity

class SettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val session = SessionStore(this)

        val tvName = findViewById<TextView>(R.id.tvUserName)
        val tvRole = findViewById<TextView>(R.id.tvUserRole)
        val tvArea = findViewById<TextView>(R.id.tvUserArea)
        val tvEmail = findViewById<TextView>(R.id.tvEmail)
        val tvPhone = findViewById<TextView>(R.id.tvPhone)

        val swPush = findViewById<Switch>(R.id.swPush)
        val swSound = findViewById<Switch>(R.id.swSound)
        val tvLogout = findViewById<TextView>(R.id.tvLogout)

        // 일단 더미 데이터. 나중에 Spring /api/users/me 붙이면 교체
        tvName.text = "${session.userId ?: "사번 미등록"}"
        tvRole.text = session.role.name
        tvArea.text = "B-8구역"
        tvEmail.text = "kim.chulsu@company.com"
        tvPhone.text = "010-1234-5678"

        swPush.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(this, "푸시 알림: $isChecked", Toast.LENGTH_SHORT).show()
        }

        swSound.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(this, "알림 소리: $isChecked", Toast.LENGTH_SHORT).show()
        }

        tvLogout.setOnClickListener {
            Toast.makeText(this, "로그아웃은 나중에 구현", Toast.LENGTH_SHORT).show()
        }
    }
}