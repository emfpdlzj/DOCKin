package com.project.dockin.data.chat

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class ChatRepository(private val context: Context) {

    // Kotlin data class용 어댑터 추가
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    // lazy 로 한 번만 로딩
    private val data: ChatData by lazy { load() }

    private fun load(): ChatData {
        val input = context.assets.open("chat_data.json")
        val json = input.bufferedReader().use { it.readText() }

        // 제네릭 타입 아님 → 그냥 클래스 전달하면 됨
        val adapter = moshi.adapter(ChatData::class.java)

        return adapter.fromJson(json) ?: ChatData(emptyList())
    }

    fun getRooms(): List<ChatRoom> = data.rooms

    fun getRoom(id: Int): ChatRoom? =
        data.rooms.firstOrNull { it.id == id }
}