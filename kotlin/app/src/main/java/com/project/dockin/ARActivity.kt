package com.project.dockin

import android.os.Bundle
import com.unity3d.player.UnityPlayerActivity

class ARActivity : UnityPlayerActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Unity 씬은 UnityPlayerActivity가 알아서 로딩해줌
        // 여기서는 아직 아무 브리지 호출 안 하고, "Unity 화면 잘 뜨는지"만 확인 단계
    }
}