package com.example.myinstagram.model

data class Conversation(
    val conversationId: String,
    val participantIds: List<String> = emptyList(),
    val messages: List<Message> = emptyList(),
    val unreadCount: Int = 0
)
