package com.example.myinstagram.model

data class Message(
    val messageId: String,
    val conversationId: String,
    val senderId: String,
    val text: String,
    val timestamp: String = "",
    val isRead: Boolean = false
)
