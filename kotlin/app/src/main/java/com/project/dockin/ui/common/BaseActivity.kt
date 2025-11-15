package com.project.dockin.ui.common

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.project.dockin.R
import com.project.dockin.ui.chat.ChatRoomActivity
import com.project.dockin.ui.settings.SettingsActivity

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        attachGlobalOverlay()
    }

    override fun setContentView(view: View?) {
        super.setContentView(view)
        attachGlobalOverlay()
    }

    private fun attachGlobalOverlay() {
        val root = findViewById<ViewGroup>(android.R.id.content) ?: return

        // 이미 붙어 있으면 중복 방지
        if (root.findViewById<View>(R.id.btnGlobalSettings) != null) {
            return
        }

        LayoutInflater.from(this).inflate(R.layout.overlay_global_actions, root, true)

        root.findViewById<View>(R.id.btnGlobalSettings)?.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        root.findViewById<View>(R.id.btnGlobalChat)?.setOnClickListener {
            val intent = Intent(this, ChatRoomActivity::class.java).apply {
                putExtra(ChatRoomActivity.EXTRA_ROOM_ID, ChatRoomActivity.CHATBOT_ROOM_ID)
            }
            startActivity(intent)
        }
    }
}