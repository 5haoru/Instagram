package com.example.myinstagram.presenter

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.myinstagram.data.DataRepository
import com.example.myinstagram.model.Comment
import com.example.myinstagram.model.Reel
import com.example.myinstagram.model.User

class ReelsPresenter {

    var reels by mutableStateOf<List<Reel>>(emptyList())
        private set
    var currentIndex by mutableIntStateOf(0)
        private set
    var currentUser by mutableStateOf<User?>(null)
        private set

    fun loadData() {
        currentUser = DataRepository.getCurrentUser()
        reels = DataRepository.getReels()
    }

    fun getUserById(userId: String): User? = DataRepository.getUserById(userId)

    fun getUsers(): List<User> = DataRepository.getUsers()

    fun updateCurrentIndex(index: Int) {
        currentIndex = index
    }

    fun toggleLikeReel(reelId: String) {
        val userId = currentUser?.userId ?: return
        DataRepository.toggleLikeReel(reelId, userId)
        reels = DataRepository.getReels()
    }

    fun toggleSaveReel(reelId: String) {
        val userId = currentUser?.userId ?: return
        DataRepository.toggleSaveReel(reelId, userId)
        reels = DataRepository.getReels()
    }

    fun addCommentToReel(reelId: String, text: String) {
        val userId = currentUser?.userId ?: return
        val comment = Comment(
            commentId = "comment_${System.currentTimeMillis()}",
            postId = reelId,
            userId = userId,
            text = text,
            timestamp = "Just now"
        )
        DataRepository.addCommentToReel(reelId, comment)
        reels = DataRepository.getReels()
    }

    fun toggleLikeReelComment(reelId: String, commentId: String) {
        val userId = currentUser?.userId ?: return
        DataRepository.toggleLikeReelComment(reelId, commentId, userId)
        reels = DataRepository.getReels()
    }

    fun toggleFollowUser(targetUserId: String) {
        val userId = currentUser?.userId ?: return
        DataRepository.toggleFollowUser(targetUserId, userId)
        currentUser = DataRepository.getCurrentUser()
    }

    fun isReelLiked(reel: Reel): Boolean {
        return currentUser?.userId in reel.likedBy
    }

    fun isReelSaved(reel: Reel): Boolean {
        return currentUser?.userId in reel.savedBy
    }

    fun isCommentLiked(comment: Comment): Boolean {
        return currentUser?.userId in comment.likedBy
    }

    fun isFollowingUser(userId: String): Boolean {
        return userId in (currentUser?.following ?: emptyList())
    }
}
