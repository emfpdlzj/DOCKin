package com.project.dockin.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.project.dockin.R

class NavigationFragment : Fragment(R.layout.fragment_navigation) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnArNav  = view.findViewById<Button>(R.id.btnOpenArNavigation)
        val btnArMemo = view.findViewById<Button>(R.id.btnOpenArMemo)

        btnArNav.setOnClickListener {
            // Unity 기반 AR 네비 Activity 로 연결
            startActivity(Intent(requireContext(), com.project.dockin.ui.ar.ArNavigationActivity::class.java))
        }

        btnArMemo.setOnClickListener {
            // Unity/카메라 + 메모 목록/작성 Activity
            startActivity(Intent(requireContext(), com.project.dockin.ui.ar.ArMemoActivity::class.java))
        }
    }
}