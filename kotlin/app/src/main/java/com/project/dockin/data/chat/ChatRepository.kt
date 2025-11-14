package com.project.dockin.data.chat

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class ChatRepository(private val context: Context) {

    private val moshi = Moshi.Builder().build()
    private val data: ChatData by lazy { load() }

    private fun load(): ChatData {
        val input = context.assets.open("chat_data.json")
        val json = input.bufferedReader().use { it.readText() }

        val type = Types.newParameterizedType(ChatData::class.java)
        val adapter = moshi.adapter(ChatData::class.java)
        return adapter.fromJson(json) ?: ChatData(emptyList())
    }

    fun getRooms(): List<ChatRoom> = data.rooms

    fun getRoom(id: Int): ChatRoom? = data.rooms.firstOrNull { it.id == id }
}