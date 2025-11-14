package com.project.dockin.data.chat

data class ChatData(
    val rooms: List<ChatRoom>
)

data class ChatRoom(
    val id: Int,
    val name: String,
    val lastMessage: String,
    val messages: List<ChatMessage>
)

data class ChatMessage(
    val sender: String,
    val mine: Boolean,
    val text: String,
    val time: String
)