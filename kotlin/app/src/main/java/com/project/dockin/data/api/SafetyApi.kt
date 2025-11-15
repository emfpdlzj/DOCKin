package com.project.dockin.data.api

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SafetyApi {

    // 전체 교육 목록 조회
    // GET /api/safety/courses
    @GET("/api/safety/courses")
    suspend fun listCourses(): List<SafetyCourseDto>

    // 특정 교육 상세 조회
    // GET /api/safety/courses/{courseId}
    @GET("/api/safety/courses/{courseId}")
    suspend fun getCourse(
        @Path("courseId") courseId: Int
    ): SafetyCourseDto

    // 교육 이수 처리
    // POST /api/safety/enroll/{id}
    @POST("/api/safety/enroll/{id}")
    suspend fun enroll(
        @Path("id") courseId: Int
    ): EnrollResponse

    // (관리자) 사용자 이수 현황 조회
    // GET /api/safety/admin/enrollments
    @GET("/api/safety/admin/enrollments")
    suspend fun listEnrollments(): List<SafetyEnrollmentDto>
}