package com.project.dockin.data.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AttendanceApi {

    data class InReq(val inLocation: String)
    data class OutReq(val outLocation: String)

    // 근태 단건 응답(로그에 찍힌 JSON 구조 기반)
    data class AttendanceDto(
        val id: Long,
        val userId: String,
        val clockInTime: String,
        val clockOutTime: String?,
        val totalWorkTime: String?,   // 예: "PT6.276104S"
        val status: String,           // "NORMAL", "LATE" 등
        val inLocation: String?,
        val outLocation: String?
    )

    @POST("/api/attendance/in")
    suspend fun clockIn(@Body req: InReq): AttendanceDto

    @POST("/api/attendance/out")
    suspend fun clockOut(@Body req: OutReq): AttendanceDto

    // ✅ 내 근태 기록 전체 조회 (백엔드랑 URL만 맞춰주면 됨)
    @GET("/api/attendance/my")
    suspend fun getMyAttendance(): List<AttendanceDto>
}