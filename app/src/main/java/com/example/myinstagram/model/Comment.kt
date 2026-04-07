package com.example.myinstagram.model

data class Comment(
    val commentId: String,
    val postId: String,
    val userId: String,
    val text: String,
    val timestamp: String = "",
    val likedBy: List<String> = emptyList()
)
