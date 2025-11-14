package com.project.dockin.data.api

import retrofit2.http.*

interface ArApi {

    // 장비/위치 메모 조회 GET /api/ar/memo/{equipmentId}
    @GET("/api/ar/memo/{equipmentId}")
    suspend fun getMemos(@Path("equipmentId") equipmentId: Int): List<ArMemoDto>

    // 새 AR 메모 저장 POST /api/ar/memo
    @POST("/api/ar/memo")
    suspend fun saveMemo(@Body req: SaveMemoRequest): ArMemoDto

    // 특정 메모 삭제 DELETE /api/ar/memo/{memoId}
    @DELETE("/api/ar/memo/{memoId}")
    suspend fun deleteMemo(@Path("memoId") memoId: Long)

    // AR 경로 요청 GET /api/ar/navigate?from=...&to=...
    @GET("/api/ar/navigate")
    suspend fun getRoute(
        @Query("from") from: String,
        @Query("to") to: String
    ): ArRouteResponse

    data class ArMemoDto(
        val memoId: Long,
        val equipmentId: Int,
        val memoText: String,
        val createdBy: String,
        val createdAt: String
    )

    data class SaveMemoRequest(
        val equipmentId: Int,
        val memoText: String
    )

    data class ArRouteResponse(
        val points: List<RoutePoint>
    )

    data class RoutePoint(
        val lat: Double,
        val lng: Double,
        val alt: Double?
    )
}