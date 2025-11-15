package com.project.dockin.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.project.dockin.R
import com.project.dockin.ui.safety.SafetyListActivity

class SafetyLearnFragment : Fragment(R.layout.fragment_safety_learn) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnCourses = view.findViewById<Button>(R.id.btnOpenSafetyCourses)
        val btnAgreement = view.findViewById<Button>(R.id.btnOpenLaborAgreement)

        // 1. 미이수/전체 교육 목록 보기
        btnCourses.setOnClickListener {
            startActivity(Intent(requireContext(), SafetyListActivity::class.java))
        }

        // 2. 월별 근로 동의서 (지금은 백엔드/화면 없으니까 토스트만)
        btnAgreement.setOnClickListener {
            Toast.makeText(requireContext(), "근로 동의서 서명 화면은 목업입니다.", Toast.LENGTH_SHORT).show()
        }
    }
}