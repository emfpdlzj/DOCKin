package com.project.dockin.data.api
import retrofit2.http.*

interface WorkLogApi {
    @POST("api/work-logs")
    suspend fun create(@Body body: CreateReq): WorkLogDto
    data class CreateReq(val title: String, val log_text: String, val equipment_id: Long? = null)
}
data class WorkLogDto(
    val log_id: Long, val user_id: String,
    val equipment_id: Long?, val title: String, val log_text: String,
    val created_at: String, val updated_at: String
)