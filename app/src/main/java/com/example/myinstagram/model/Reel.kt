package com.example.myinstagram.model

data class Reel(
    val reelId: String,
    val userId: String,
    val videoUrl: String = "",
    val caption: String = "",
    val audioName: String = "",
    val likedBy: List<String> = emptyList(),
    val savedBy: List<String> = emptyList(),
    val comments: List<Comment> = emptyList(),
    val shareCount: Int = 0
)
