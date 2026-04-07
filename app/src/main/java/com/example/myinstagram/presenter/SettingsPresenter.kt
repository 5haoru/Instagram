package com.example.myinstagram.presenter

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.myinstagram.data.DataRepository
import com.example.myinstagram.model.Post
import com.example.myinstagram.model.Reel
import com.example.myinstagram.model.User

class SettingsPresenter {

    var currentUser by mutableStateOf<User?>(null)
        private set
    var allUsers by mutableStateOf<List<User>>(emptyList())
        private set
    var savedPosts by mutableStateOf<List<Post>>(emptyList())
        private set
    var savedReels by mutableStateOf<List<Reel>>(emptyList())
        private set

    fun loadData() {
        currentUser = DataRepository.getCurrentUser()
        allUsers = DataRepository.getUsers()
        val userId = currentUser?.userId ?: return
        savedPosts = DataRepository.getSavedPosts(userId)
        savedReels = DataRepository.getSavedReels(userId)
    }

    fun getUserById(userId: String): User? = DataRepository.getUserById(userId)

    fun togglePrivacy() {
        val userId = currentUser?.userId ?: return
        DataRepository.togglePrivacy(userId)
        currentUser = DataRepository.getCurrentUser()
    }

    fun toggleCloseFriend(targetUserId: String) {
        val userId = currentUser?.userId ?: return
        DataRepository.toggleCloseFriend(userId, targetUserId)
        currentUser = DataRepository.getCurrentUser()
    }

    fun isCloseFriend(targetUserId: String): Boolean {
        return targetUserId in (currentUser?.closeFriends ?: emptyList())
    }

    fun toggleBlockUser(targetUserId: String) {
        val userId = currentUser?.userId ?: return
        DataRepository.toggleBlockUser(userId, targetUserId)
        currentUser = DataRepository.getCurrentUser()
    }

    fun isBlocked(targetUserId: String): Boolean {
        return targetUserId in (currentUser?.blockedUsers ?: emptyList())
    }

    fun getOtherUsers(): List<User> {
        return allUsers.filter { !it.isCurrentUser }
    }

    fun getBlockedUsersList(): List<User> {
        val blockedIds = currentUser?.blockedUsers ?: emptyList()
        return allUsers.filter { it.userId in blockedIds }
    }

    fun getCloseFriendsList(): List<User> {
        val closeFriendIds = currentUser?.closeFriends ?: emptyList()
        return allUsers.filter { it.userId in closeFriendIds }
    }
}
