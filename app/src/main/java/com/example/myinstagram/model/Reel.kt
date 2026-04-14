package com.example.myinstagram.model

data class Reel(
    val reelId: String,
    val userId: String,
    val videoUrl: String = "",
    val caption: String = "",
    val audioName: String = "",
    val likesCount: Int = 0,
    val sharesCount: Int = 0,
    val likedBy: List<String> = emptyList(),
    val savedBy: List<String> = emptyList(),
    val comments: List<Comment> = emptyList()
)
