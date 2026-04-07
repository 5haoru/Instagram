package com.example.myinstagram.model

data class Story(
    val storyId: String,
    val userId: String,
    val imageUrl: String = "",
    val timestamp: String = "",
    val viewedBy: List<String> = emptyList()
)
