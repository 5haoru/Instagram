package com.example.myinstagram.presenter

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.myinstagram.data.DataRepository
import com.example.myinstagram.model.Post
import com.example.myinstagram.model.User

class ProfilePresenter {

    var currentUser by mutableStateOf<User?>(null)
        private set
    var userPosts by mutableStateOf<List<Post>>(emptyList())
        private set

    fun loadData() {
        currentUser = DataRepository.getCurrentUser()
        val userId = currentUser?.userId ?: return
        userPosts = DataRepository.getPosts().filter { it.userId == userId }
    }
}
