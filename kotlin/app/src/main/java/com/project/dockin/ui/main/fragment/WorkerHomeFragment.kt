package com.project.dockin.ui.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.project.dockin.R
import com.project.dockin.data.api.AttendanceApi
import com.project.dockin.data.api.Network
import kotlinx.coroutines.launch

class WorkerHomeFragment : Fragment() {

    private lateinit var api: AttendanceApi

    private lateinit var tvStatus: TextView
    private lateinit var tvLastInOut: TextView
    private lateinit var btnIn: Button
    private lateinit var btnOut: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 로그인 된 상태라 토큰 헤더 붙은 retrofit 사용
        api = Network.retrofit(requireContext()).create(AttendanceApi::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_worker_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvStatus   = view.findViewById(R.id.tvStatus)
        tvLastInOut = view.findViewById(R.id.tvLastInOut)
        btnIn      = view.findViewById(R.id.btnIn)
        btnOut     = view.findViewById(R.id.btnOut)

        btnIn.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                runCatching { api.clockIn(AttendanceApi.InReq("Office_A")) }
                    .onSuccess {
                        tvStatus.text = "현재 상태: 출근 (${it.status})"
                        tvLastInOut.text = "출근 위치: ${it.inLocation ?: "-"}"
                        Toast.makeText(requireContext(), "출근 처리 완료", Toast.LENGTH_SHORT).show()
                    }
                    .onFailure {
                        Toast.makeText(
                            requireContext(),
                            "출근 실패: ${it.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }

        btnOut.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                runCatching { api.clockOut(AttendanceApi.OutReq("사무실 5층")) }
                    .onSuccess {
                        tvStatus.text = "현재 상태: 퇴근 (${it.status})"
                        tvLastInOut.text = "퇴근 위치: ${it.outLocation ?: "-"}"
                        Toast.makeText(requireContext(), "퇴근 처리 완료", Toast.LENGTH_SHORT).show()
                    }
                    .onFailure {
                        Toast.makeText(
                            requireContext(),
                            "퇴근 실패: ${it.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    }
}