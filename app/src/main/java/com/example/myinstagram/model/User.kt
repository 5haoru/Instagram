package com.example.myinstagram.model

data class User(
    val userId: String,
    val username: String,
    val displayName: String,
    val bio: String = "",
    val avatarUrl: String = "",
    val isVerified: Boolean = false,
    val isPrivate: Boolean = false,
    val postsCount: Int = 0,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val followers: List<String> = emptyList(),
    val following: List<String> = emptyList(),
    val isCurrentUser: Boolean = false,
    val closeFriends: List<String> = emptyList(),
    val blockedUsers: List<String> = emptyList()
)
