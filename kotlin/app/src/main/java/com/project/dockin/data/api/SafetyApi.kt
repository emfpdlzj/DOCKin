package com.project.dockin.data.api

import retrofit2.http.*

interface SafetyApi {

    @GET("/api/safety/courses")
    suspend fun getCourses(): List<SafetyCourseDto>

    @GET("/api/safety/courses/{id}")
    suspend fun getCourseDetail(@Path("id") id: Long): SafetyCourseDto

    @POST("/api/safety/enroll/{id}")
    suspend fun completeCourse(@Path("id") id: Long): EnrollResponse

    data class SafetyCourseDto(
        val courseId: Long,
        val title: String,
        val description: String?,
        val videoUrl: String,
        val durationMinutes: Int,
        val mandatory: Boolean
    )

    data class EnrollResponse(
        val success: Boolean,
        val message: String?
    )
}