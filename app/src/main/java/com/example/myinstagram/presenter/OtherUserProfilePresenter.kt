package com.example.myinstagram.presenter

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.myinstagram.data.DataRepository
import com.example.myinstagram.model.Post
import com.example.myinstagram.model.User

class OtherUserProfilePresenter {

    var targetUser by mutableStateOf<User?>(null)
        private set
    var currentUser by mutableStateOf<User?>(null)
        private set
    var userPosts by mutableStateOf<List<Post>>(emptyList())
        private set
    var isFollowing by mutableStateOf(false)
        private set

    fun loadUser(userId: String) {
        currentUser = DataRepository.getCurrentUser()
        targetUser = DataRepository.getUserById(userId)
        val target = targetUser ?: return
        userPosts = DataRepository.getPosts().filter { it.userId == userId }
        isFollowing = currentUser?.userId in target.followers
    }

    fun toggleFollow() {
        val currentUserId = currentUser?.userId ?: return
        val targetUserId = targetUser?.userId ?: return
        DataRepository.toggleFollowUser(targetUserId, currentUserId)
        currentUser = DataRepository.getCurrentUser()
        targetUser = DataRepository.getUserById(targetUserId)
        isFollowing = currentUser?.userId in (targetUser?.followers ?: emptyList())
    }
}
