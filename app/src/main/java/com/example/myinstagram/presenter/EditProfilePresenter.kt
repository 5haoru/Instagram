package com.example.myinstagram.presenter

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.myinstagram.data.DataRepository
import com.example.myinstagram.model.User

class EditProfilePresenter {

    var currentUser by mutableStateOf<User?>(null)
        private set

    var name by mutableStateOf("")
    var username by mutableStateOf("")
    var pronouns by mutableStateOf("")
    var bio by mutableStateOf("")
    var links by mutableStateOf("")
    var gender by mutableStateOf("")

    fun loadData() {
        currentUser = DataRepository.getCurrentUser()
        currentUser?.let { user ->
            name = user.displayName
            username = user.username
            bio = user.bio
        }
    }
}
