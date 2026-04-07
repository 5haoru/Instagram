package com.example.myinstagram.model

data class MusicTrack(
    val id: String,
    val title: String,
    val artist: String,
    val thumbnailUrl: String = ""
)
