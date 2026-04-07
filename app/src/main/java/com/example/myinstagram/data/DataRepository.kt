package com.example.myinstagram.data

import android.content.Context
import com.example.myinstagram.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object DataRepository {

    private val gson = Gson()
    private var users: MutableList<User> = mutableListOf()
    private var posts: MutableList<Post> = mutableListOf()
    private var stories: MutableList<Story> = mutableListOf()
    private var reels: MutableList<Reel> = mutableListOf()
    private var conversations: MutableList<Conversation> = mutableListOf()
    private var music: MutableList<MusicTrack> = mutableListOf()
    private var locations: MutableList<LocationItem> = mutableListOf()
    private var isLoaded = false

    fun initialize(context: Context) {
        if (isLoaded) return
        users = loadList(context, "data/users.json")
        posts = loadList(context, "data/posts.json")
        stories = loadList(context, "data/stories.json")
        reels = loadList(context, "data/reels.json")
        conversations = loadList(context, "data/conversations.json")
        music = loadList(context, "data/music.json")
        locations = loadList(context, "data/locations.json")
        isLoaded = true
    }

    private inline fun <reified T> loadList(context: Context, path: String): MutableList<T> {
        val json = context.assets.open(path).bufferedReader().use { it.readText() }
        val type = TypeToken.getParameterized(List::class.java, T::class.java).type
        return gson.fromJson<List<T>>(json, type).toMutableList()
    }

    fun getCurrentUser(): User? = users.find { it.isCurrentUser }

    fun getUsers(): List<User> = users

    fun getUserById(userId: String): User? = users.find { it.userId == userId }

    fun getPosts(): List<Post> = posts

    fun getStories(): List<Story> = stories

    fun getReels(): List<Reel> = reels

    fun getConversations(): List<Conversation> = conversations

    fun getMusic(): List<MusicTrack> = music

    fun getLocations(): List<LocationItem> = locations

    fun addPost(post: Post) {
        posts.add(0, post)
        val idx = users.indexOfFirst { it.userId == post.userId }
        if (idx >= 0) {
            users[idx] = users[idx].copy(postsCount = users[idx].postsCount + 1)
        }
    }

    fun getSuggestedUsers(): List<User> {
        val currentUser = getCurrentUser() ?: return emptyList()
        return users.filter { !it.isCurrentUser && it.userId !in currentUser.following }
    }

    fun toggleLikePost(postId: String, userId: String) {
        val idx = posts.indexOfFirst { it.postId == postId }
        if (idx < 0) return
        val post = posts[idx]
        val newLikedBy = post.likedBy.toMutableList()
        if (userId in newLikedBy) newLikedBy.remove(userId) else newLikedBy.add(userId)
        posts[idx] = post.copy(likedBy = newLikedBy)
    }

    fun toggleSavePost(postId: String, userId: String) {
        val idx = posts.indexOfFirst { it.postId == postId }
        if (idx < 0) return
        val post = posts[idx]
        val newSavedBy = post.savedBy.toMutableList()
        if (userId in newSavedBy) newSavedBy.remove(userId) else newSavedBy.add(userId)
        posts[idx] = post.copy(savedBy = newSavedBy)
    }

    fun addCommentToPost(postId: String, comment: Comment) {
        val idx = posts.indexOfFirst { it.postId == postId }
        if (idx < 0) return
        val post = posts[idx]
        posts[idx] = post.copy(comments = post.comments + comment)
    }

    fun toggleLikeComment(postId: String, commentId: String, userId: String) {
        val postIdx = posts.indexOfFirst { it.postId == postId }
        if (postIdx < 0) return
        val post = posts[postIdx]
        val newComments = post.comments.map { comment ->
            if (comment.commentId == commentId) {
                val newLikedBy = comment.likedBy.toMutableList()
                if (userId in newLikedBy) newLikedBy.remove(userId) else newLikedBy.add(userId)
                comment.copy(likedBy = newLikedBy)
            } else comment
        }
        posts[postIdx] = post.copy(comments = newComments)
    }

    fun toggleLikeReel(reelId: String, userId: String) {
        val idx = reels.indexOfFirst { it.reelId == reelId }
        if (idx < 0) return
        val reel = reels[idx]
        val newLikedBy = reel.likedBy.toMutableList()
        if (userId in newLikedBy) newLikedBy.remove(userId) else newLikedBy.add(userId)
        reels[idx] = reel.copy(likedBy = newLikedBy)
    }

    fun toggleSaveReel(reelId: String, userId: String) {
        val idx = reels.indexOfFirst { it.reelId == reelId }
        if (idx < 0) return
        val reel = reels[idx]
        val newSavedBy = reel.savedBy.toMutableList()
        if (userId in newSavedBy) newSavedBy.remove(userId) else newSavedBy.add(userId)
        reels[idx] = reel.copy(savedBy = newSavedBy)
    }

    fun addCommentToReel(reelId: String, comment: Comment) {
        val idx = reels.indexOfFirst { it.reelId == reelId }
        if (idx < 0) return
        val reel = reels[idx]
        reels[idx] = reel.copy(comments = reel.comments + comment)
    }

    fun toggleLikeReelComment(reelId: String, commentId: String, userId: String) {
        val reelIdx = reels.indexOfFirst { it.reelId == reelId }
        if (reelIdx < 0) return
        val reel = reels[reelIdx]
        val newComments = reel.comments.map { comment ->
            if (comment.commentId == commentId) {
                val newLikedBy = comment.likedBy.toMutableList()
                if (userId in newLikedBy) newLikedBy.remove(userId) else newLikedBy.add(userId)
                comment.copy(likedBy = newLikedBy)
            } else comment
        }
        reels[reelIdx] = reel.copy(comments = newComments)
    }

    fun addMessage(conversationId: String, message: Message) {
        val idx = conversations.indexOfFirst { it.conversationId == conversationId }
        if (idx < 0) return
        val conv = conversations[idx]
        conversations[idx] = conv.copy(messages = conv.messages + message)
    }

    fun markStoryViewed(storyId: String, userId: String) {
        val idx = stories.indexOfFirst { it.storyId == storyId }
        if (idx < 0) return
        val story = stories[idx]
        if (userId !in story.viewedBy) {
            stories[idx] = story.copy(viewedBy = story.viewedBy + userId)
        }
    }

    fun toggleFollowUser(targetUserId: String, currentUserId: String) {
        val currentIdx = users.indexOfFirst { it.userId == currentUserId }
        val targetIdx = users.indexOfFirst { it.userId == targetUserId }
        if (currentIdx < 0 || targetIdx < 0) return

        val current = users[currentIdx]
        val target = users[targetIdx]
        val newFollowing = current.following.toMutableList()
        val newFollowers = target.followers.toMutableList()

        if (targetUserId in newFollowing) {
            newFollowing.remove(targetUserId)
            newFollowers.remove(currentUserId)
        } else {
            newFollowing.add(targetUserId)
            newFollowers.add(currentUserId)
        }

        users[currentIdx] = current.copy(
            following = newFollowing,
            followingCount = newFollowing.size
        )
        users[targetIdx] = target.copy(
            followers = newFollowers,
            followersCount = newFollowers.size
        )
    }

    fun getSavedPosts(userId: String): List<Post> {
        return posts.filter { userId in it.savedBy }
    }

    fun getSavedReels(userId: String): List<Reel> {
        return reels.filter { userId in it.savedBy }
    }

    fun togglePrivacy(userId: String) {
        val idx = users.indexOfFirst { it.userId == userId }
        if (idx < 0) return
        users[idx] = users[idx].copy(isPrivate = !users[idx].isPrivate)
    }

    fun toggleCloseFriend(currentUserId: String, targetUserId: String) {
        val idx = users.indexOfFirst { it.userId == currentUserId }
        if (idx < 0) return
        val user = users[idx]
        val newList = user.closeFriends.toMutableList()
        if (targetUserId in newList) newList.remove(targetUserId) else newList.add(targetUserId)
        users[idx] = user.copy(closeFriends = newList)
    }

    fun toggleBlockUser(currentUserId: String, targetUserId: String) {
        val idx = users.indexOfFirst { it.userId == currentUserId }
        if (idx < 0) return
        val user = users[idx]
        val newList = user.blockedUsers.toMutableList()
        if (targetUserId in newList) newList.remove(targetUserId) else newList.add(targetUserId)
        users[idx] = user.copy(blockedUsers = newList)
    }
}
