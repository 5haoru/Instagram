package com.example.myinstagram.presenter

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.myinstagram.data.DataRepository
import com.example.myinstagram.model.Post
import com.example.myinstagram.model.User

class SearchPresenter {

    var allPosts by mutableStateOf<List<Post>>(emptyList())
        private set
    var searchQuery by mutableStateOf("")
        private set
    var searchResults by mutableStateOf<List<User>>(emptyList())
        private set
    var isSearching by mutableStateOf(false)
        private set

    fun loadData() {
        allPosts = DataRepository.getPosts()
    }

    fun onSearchQueryChange(query: String) {
        searchQuery = query
        isSearching = query.isNotEmpty()
        if (query.isNotEmpty()) {
            searchResults = DataRepository.getUsers().filter {
                it.username.contains(query, ignoreCase = true) ||
                    it.displayName.contains(query, ignoreCase = true)
            }
        } else {
            searchResults = emptyList()
        }
    }
}
