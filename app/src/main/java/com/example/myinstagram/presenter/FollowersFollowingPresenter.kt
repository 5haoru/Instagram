package com.example.myinstagram.presenter

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.myinstagram.data.DataRepository
import com.example.myinstagram.model.User

class FollowersFollowingPresenter {

    var currentUser by mutableStateOf<User?>(null)
        private set
    var followerUsers by mutableStateOf<List<User>>(emptyList())
        private set
    var followingUsers by mutableStateOf<List<User>>(emptyList())
        private set
    var selectedTab by mutableIntStateOf(0)

    fun loadData(initialTab: Int = 0) {
        currentUser = DataRepository.getCurrentUser()
        selectedTab = initialTab
        refreshLists()
    }

    private fun refreshLists() {
        currentUser = DataRepository.getCurrentUser()
        val user = currentUser ?: return
        followerUsers = user.followers.mapNotNull { DataRepository.getUserById(it) }
        followingUsers = user.following.mapNotNull { DataRepository.getUserById(it) }
    }

    fun removeFollower(userId: String) {
        val currentUserId = currentUser?.userId ?: return
        DataRepository.toggleFollowUser(currentUserId, userId)
        refreshLists()
    }

    fun toggleFollow(targetUserId: String) {
        val currentUserId = currentUser?.userId ?: return
        DataRepository.toggleFollowUser(targetUserId, currentUserId)
        refreshLists()
    }

    fun isFollowing(userId: String): Boolean {
        return currentUser?.following?.contains(userId) == true
    }
}
