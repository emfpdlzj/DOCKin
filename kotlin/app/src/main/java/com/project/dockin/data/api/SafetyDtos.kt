package com.project.dockin.data.api

// /api/safety/courses 응답 1건
data class SafetyCourseDto(
    val courseId: Int,          // course_id
    val title: String,
    val description: String?,   // NULL 가능
    val videoUrl: String,       // video_url (필수라고 가정)
    val durationMinutes: Int,   // duration_minutes
    val isMandatory: Boolean,   // is_mandatory
    val createdAt: String?      // created_at
)

// /api/safety/enroll/{id} 응답
data class EnrollResponse(
    val success: Boolean,
    val message: String?
)

// (관리자 이수 현황 조회용 – 나중에 쓸 수도 있음)
data class SafetyEnrollmentDto(
    val enrollmentId: Int,
    val userId: String,
    val courseId: Int,
    val isCompleted: Boolean,
    val completionDate: String?
)