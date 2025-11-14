package com.project.dockin.data.repo

import com.project.dockin.data.api.ArApi

class ArRepository(
    private val api: ArApi
) {
    suspend fun getMemos(equipmentId: Int): List<ArApi.ArMemoDto> {
        return api.getMemos(equipmentId)
    }

    suspend fun addMemo(equipmentId: Int, text: String): ArApi.ArMemoDto {
        val req = ArApi.SaveMemoRequest(
            equipmentId = equipmentId,
            memoText = text
        )
        return api.saveMemo(req)
    }

    suspend fun deleteMemo(memoId: Long) {
        api.deleteMemo(memoId)
    }

    suspend fun getRoute(from: String, to: String): ArApi.ArRouteResponse {
        return api.getRoute(from, to)
    }
}