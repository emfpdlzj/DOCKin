package com.project.dockin.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.project.dockin.R

class SafetyAdminFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_safety_admin_placeholder,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnManageCourses     = view.findViewById<Button>(R.id.btnManageCourses)
        val btnViewEnrollments   = view.findViewById<Button>(R.id.btnViewEnrollments)
        val btnEmergencyBroadcast = view.findViewById<Button>(R.id.btnEmergencyBroadcast)

        btnManageCourses.setOnClickListener {
            Toast.makeText(requireContext(), "안전교육 콘텐츠 관리 (Spring /api/safety/admin/courses)", Toast.LENGTH_SHORT).show()
            // 필요하면 여기서 SafetyListActivity 같은 곳으로 이동해도 됨
        }

        btnViewEnrollments.setOnClickListener {
            Toast.makeText(requireContext(), "사용자 이수 현황 (/api/safety/admin/enrollments)", Toast.LENGTH_SHORT).show()
        }

        btnEmergencyBroadcast.setOnClickListener {
            Toast.makeText(requireContext(), "긴급 공지 발송 화면 (FCM 목업)", Toast.LENGTH_SHORT).show()
        }
    }
}