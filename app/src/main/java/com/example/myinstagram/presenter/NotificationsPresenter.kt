package com.example.myinstagram.presenter

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.myinstagram.data.DataRepository
import com.example.myinstagram.model.User

data class NotificationItem(
    val id: String,
    val userId: String,
    val type: String, // "like", "comment", "follow", "mention"
    val description: String,
    val timestamp: String,
    val postImageUrl: String = "",
    val isFollowRequest: Boolean = false
)

class NotificationsPresenter {

    var notifications by mutableStateOf<List<NotificationItem>>(emptyList())
        private set
    var currentUser by mutableStateOf<User?>(null)
        private set

    fun loadData() {
        currentUser = DataRepository.getCurrentUser()
        val currentUserId = currentUser?.userId ?: return
        val posts = DataRepository.getPosts()

        val items = mutableListOf<NotificationItem>()

        // Generate notifications from post likes (others who liked current user's posts)
        posts.filter { it.userId == currentUserId }.forEach { post ->
            post.likedBy.filter { it != currentUserId }.forEach { likerId ->
                val liker = DataRepository.getUserById(likerId)
                items.add(
                    NotificationItem(
                        id = "like_${post.postId}_$likerId",
                        userId = likerId,
                        type = "like",
                        description = "${liker?.username ?: likerId} liked your post.",
                        timestamp = post.timestamp,
                        postImageUrl = post.imageUrl
                    )
                )
            }
        }

        // Generate notifications from comments on current user's posts
        posts.filter { it.userId == currentUserId }.forEach { post ->
            post.comments.filter { it.userId != currentUserId }.forEach { comment ->
                val commenter = DataRepository.getUserById(comment.userId)
                items.add(
                    NotificationItem(
                        id = "comment_${comment.commentId}",
                        userId = comment.userId,
                        type = "comment",
                        description = "${commenter?.username ?: comment.userId} commented: ${comment.text}",
                        timestamp = comment.timestamp,
                        postImageUrl = post.imageUrl
                    )
                )
            }
        }

        // Generate follow notifications from followers
        currentUser?.followers?.forEach { followerId ->
            val follower = DataRepository.getUserById(followerId)
            items.add(
                NotificationItem(
                    id = "follow_$followerId",
                    userId = followerId,
                    type = "follow",
                    description = "${follower?.username ?: followerId} started following you.",
                    timestamp = "1w"
                )
            )
        }

        // Generate notifications from likes on current user's comments
        posts.forEach { post ->
            post.comments.filter { it.userId == currentUserId }.forEach { comment ->
                comment.likedBy.filter { it != currentUserId }.forEach { likerId ->
                    val liker = DataRepository.getUserById(likerId)
                    items.add(
                        NotificationItem(
                            id = "comment_like_${comment.commentId}_$likerId",
                            userId = likerId,
                            type = "like",
                            description = "${liker?.username ?: likerId} liked your comment.",
                            timestamp = post.timestamp
                        )
                    )
                }
            }
        }

        notifications = items
    }

    fun getUserById(userId: String): User? = DataRepository.getUserById(userId)
}
