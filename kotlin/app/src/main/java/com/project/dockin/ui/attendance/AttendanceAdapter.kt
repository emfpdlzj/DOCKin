package com.project.dockin.ui.attendance

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.dockin.R
import com.project.dockin.data.api.AttendanceApi
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class AttendanceAdapter :
    ListAdapter<AttendanceApi.AttendanceDto, AttendanceAdapter.VH>(DIFF) {

    object DIFF : DiffUtil.ItemCallback<AttendanceApi.AttendanceDto>() {
        override fun areItemsTheSame(
            oldItem: AttendanceApi.AttendanceDto,
            newItem: AttendanceApi.AttendanceDto
        ) = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: AttendanceApi.AttendanceDto,
            newItem: AttendanceApi.AttendanceDto
        ) = oldItem == newItem
    }

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvDateStatus: TextView = v.findViewById(R.id.tvDateStatus)
        val tvTime: TextView = v.findViewById(R.id.tvTime)
        val tvLocation: TextView = v.findViewById(R.id.tvLocation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_attendance, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)

        // 날짜/상태 라벨 (clockInTime 기준)
        val inTimeStr = formatDateTime(item.clockInTime)
        val statusLabel = when (item.status) {
            "NORMAL" -> "정상 출근"
            "LATE" -> "지각"
            "ABSENT" -> "결근"
            else -> item.status
        }
        holder.tvDateStatus.text = "$inTimeStr · $statusLabel"

        // 시간 텍스트
        val outTimeStr = item.clockOutTime?.let { formatTime(it) } ?: "퇴근 기록 없음"
        holder.tvTime.text = "출근: ${formatTime(item.clockInTime)} / 퇴근: $outTimeStr"

        // 위치 텍스트
        val inLoc = item.inLocation ?: "-"
        val outLoc = item.outLocation ?: "-"
        holder.tvLocation.text = "출근 위치: $inLoc · 퇴근 위치: $outLoc"
    }

    private fun formatDateTime(iso: String): String {
        return try {
            val odt = OffsetDateTime.parse(iso)
            odt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        } catch (e: Exception) {
            iso
        }
    }

    private fun formatTime(iso: String): String {
        return try {
            val odt = OffsetDateTime.parse(iso)
            odt.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        } catch (e: Exception) {
            iso
        }
    }
}