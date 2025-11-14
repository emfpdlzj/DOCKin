package com.project.dockin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ARActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: 나중에 Unity View 붙이면 여기서 setContentView(...) or Unity 초기화
        // 지금은 빌드만 통과시키는 스텁(stub) 상태로 둠
    }

    // 나중에 Unity에서 호출해도 되는 public 함수
    fun showToastFromUnity(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}