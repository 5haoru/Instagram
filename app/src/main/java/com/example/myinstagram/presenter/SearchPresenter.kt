package com.example.myinstagram.presenter

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.myinstagram.data.AutoTestExporter
import com.example.myinstagram.data.DataRepository
import com.example.myinstagram.model.Post
import com.example.myinstagram.model.Reel
import com.example.myinstagram.model.User

class SearchPresenter {

    var allPosts by mutableStateOf<List<Post>>(emptyList())
        private set
    var searchQuery by mutableStateOf("")
        private set
    var searchResults by mutableStateOf<List<User>>(emptyList())
        private set
    var searchPostResults by mutableStateOf<List<Post>>(emptyList())
        private set
    var searchReelResults by mutableStateOf<List<Reel>>(emptyList())
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
            val matchedUsers = DataRepository.getUsers().filter {
                it.username.contains(query, ignoreCase = true) ||
                    it.displayName.contains(query, ignoreCase = true)
            }
            val matchedPosts = DataRepository.getPosts().filter {
                it.caption.contains(query, ignoreCase = true) ||
                    (it.location?.contains(query, ignoreCase = true) == true)
            }
            val matchedReels = DataRepository.getReels().filter {
                it.caption.contains(query, ignoreCase = true) ||
                    it.audioName.contains(query, ignoreCase = true)
            }

            // If no exact matches found, show all content as recommendations
            if (matchedUsers.isEmpty() && matchedPosts.isEmpty() && matchedReels.isEmpty()) {
                searchResults = DataRepository.getUsers().filter { !it.isCurrentUser }
                searchPostResults = DataRepository.getPosts()
                searchReelResults = DataRepository.getReels()
            } else {
                searchResults = matchedUsers
                searchPostResults = matchedPosts
                searchReelResults = matchedReels
            }
            AutoTestExporter.exportSearchState(
                query = query,
                userResultsCount = searchResults.size,
                postResultsCount = searchPostResults.size,
                reelResultsCount = searchReelResults.size,
                isSearching = isSearching
            )
        } else {
            searchResults = emptyList()
            searchPostResults = emptyList()
            searchReelResults = emptyList()
            AutoTestExporter.exportSearchState(
                query = query,
                userResultsCount = 0,
                postResultsCount = 0,
                reelResultsCount = 0,
                isSearching = false
            )
        }
    }
}
