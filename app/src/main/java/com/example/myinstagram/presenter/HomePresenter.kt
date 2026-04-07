package com.example.myinstagram.presenter

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.myinstagram.data.DataRepository
import com.example.myinstagram.model.Comment
import com.example.myinstagram.model.Post
import com.example.myinstagram.model.Story
import com.example.myinstagram.model.User

class HomePresenter {

    var posts by mutableStateOf<List<Post>>(emptyList())
        private set
    var stories by mutableStateOf<List<Story>>(emptyList())
        private set
    var suggestedUsers by mutableStateOf<List<User>>(emptyList())
        private set
    var currentUser by mutableStateOf<User?>(null)
        private set

    fun loadData() {
        currentUser = DataRepository.getCurrentUser()
        posts = DataRepository.getPosts()
        stories = DataRepository.getStories()
        suggestedUsers = DataRepository.getSuggestedUsers()
    }

    fun getUserById(userId: String): User? = DataRepository.getUserById(userId)

    fun getUsers(): List<User> = DataRepository.getUsers()

    fun toggleLikePost(postId: String) {
        val userId = currentUser?.userId ?: return
        DataRepository.toggleLikePost(postId, userId)
        posts = DataRepository.getPosts()
    }

    fun toggleSavePost(postId: String) {
        val userId = currentUser?.userId ?: return
        DataRepository.toggleSavePost(postId, userId)
        posts = DataRepository.getPosts()
    }

    fun toggleFollowUser(targetUserId: String) {
        val userId = currentUser?.userId ?: return
        DataRepository.toggleFollowUser(targetUserId, userId)
        currentUser = DataRepository.getCurrentUser()
        posts = DataRepository.getPosts()
    }

    fun isFollowingUser(targetUserId: String): Boolean {
        return targetUserId in (currentUser?.following ?: emptyList())
    }

    fun addCommentToPost(postId: String, text: String) {
        val userId = currentUser?.userId ?: return
        val comment = Comment(
            commentId = "comment_${System.currentTimeMillis()}",
            postId = postId,
            userId = userId,
            text = text,
            timestamp = "Just now",
            likedBy = emptyList()
        )
        DataRepository.addCommentToPost(postId, comment)
        posts = DataRepository.getPosts()
    }

    fun toggleLikeComment(postId: String, commentId: String) {
        val userId = currentUser?.userId ?: return
        DataRepository.toggleLikeComment(postId, commentId, userId)
        posts = DataRepository.getPosts()
    }

    fun isCommentLiked(comment: Comment): Boolean {
        return currentUser?.userId in comment.likedBy
    }

    fun isPostLiked(post: Post): Boolean {
        return currentUser?.userId in post.likedBy
    }

    fun isPostSaved(post: Post): Boolean {
        return currentUser?.userId in post.savedBy
    }
}
