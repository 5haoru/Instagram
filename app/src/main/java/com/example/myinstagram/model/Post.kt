package com.example.myinstagram.model

data class Post(
    val postId: String,
    val userId: String,
    val imageUrl: String = "",
    val caption: String = "",
    val location: String? = null,
    val timestamp: String = "",
    val likesCount: Int = 0,
    val sharesCount: Int = 0,
    val likedBy: List<String> = emptyList(),
    val savedBy: List<String> = emptyList(),
    val repostedBy: List<String>? = null,
    val comments: List<Comment> = emptyList()
)
