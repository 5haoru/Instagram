package com.example.myinstagram.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File

/**
 * Exports app state to JSON files under files/autotest/ for automated testing.
 * Check scripts can read these via: adb exec-out run-as com.example.myinstagram cat files/autotest/<file>.json
 */
object AutoTestExporter {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private var autotestDir: File? = null

    fun init(context: Context) {
        autotestDir = File(context.filesDir, "autotest").also { it.mkdirs() }
        // Export initial state
        exportAll()
    }

    private fun writeJson(filename: String, data: Any) {
        val dir = autotestDir ?: return
        try {
            File(dir, filename).writeText(gson.toJson(data))
        } catch (_: Exception) {
        }
    }

    /** Export all state at once */
    fun exportAll() {
        exportUserState()
        exportPostsState()
        exportReelsState()
        exportConversationsState()
    }

    /** Export current user state: privacy, following, followers, closeFriends, blockedUsers, displayName, etc. */
    fun exportUserState() {
        val currentUser = DataRepository.getCurrentUser() ?: return
        val data = mapOf(
            "userId" to currentUser.userId,
            "username" to currentUser.username,
            "displayName" to currentUser.displayName,
            "bio" to currentUser.bio,
            "isPrivate" to currentUser.isPrivate,
            "followersCount" to currentUser.followersCount,
            "followingCount" to currentUser.followingCount,
            "followers" to currentUser.followers,
            "following" to currentUser.following,
            "closeFriends" to currentUser.closeFriends,
            "blockedUsers" to currentUser.blockedUsers,
            "postsCount" to currentUser.postsCount
        )
        writeJson("user_state.json", data)
    }

    /** Export posts state: likedBy, savedBy, comments for each post */
    fun exportPostsState() {
        val posts = DataRepository.getPosts()
        val data = posts.map { post ->
            mapOf(
                "postId" to post.postId,
                "userId" to post.userId,
                "caption" to post.caption,
                "location" to post.location,
                "imageUrl" to post.imageUrl,
                "likedBy" to post.likedBy,
                "savedBy" to post.savedBy,
                "commentsCount" to post.comments.size,
                "comments" to post.comments.map { c ->
                    mapOf(
                        "commentId" to c.commentId,
                        "userId" to c.userId,
                        "text" to c.text,
                        "likedBy" to c.likedBy
                    )
                }
            )
        }
        writeJson("posts_state.json", data)
    }

    /** Export reels state: likedBy, savedBy, comments for each reel */
    fun exportReelsState() {
        val reels = DataRepository.getReels()
        val data = reels.map { reel ->
            mapOf(
                "reelId" to reel.reelId,
                "userId" to reel.userId,
                "caption" to reel.caption,
                "likedBy" to reel.likedBy,
                "savedBy" to reel.savedBy,
                "commentsCount" to reel.comments.size,
                "comments" to reel.comments.map { c ->
                    mapOf(
                        "commentId" to c.commentId,
                        "userId" to c.userId,
                        "text" to c.text,
                        "likedBy" to c.likedBy
                    )
                }
            )
        }
        writeJson("reels_state.json", data)
    }

    /** Export conversations state: messages */
    fun exportConversationsState() {
        val convs = DataRepository.getConversations()
        val data = convs.map { conv ->
            mapOf(
                "conversationId" to conv.conversationId,
                "participantIds" to conv.participantIds,
                "messagesCount" to conv.messages.size,
                "messages" to conv.messages.map { m ->
                    mapOf(
                        "messageId" to m.messageId,
                        "senderId" to m.senderId,
                        "text" to m.text
                    )
                }
            )
        }
        writeJson("conversations_state.json", data)
    }

    /** Export search state for automated validation */
    fun exportSearchState(
        query: String,
        userResultsCount: Int,
        postResultsCount: Int,
        reelResultsCount: Int,
        isSearching: Boolean
    ) {
        val data = mapOf(
            "query" to query,
            "isSearching" to isSearching,
            "userResultsCount" to userResultsCount,
            "postResultsCount" to postResultsCount,
            "reelResultsCount" to reelResultsCount,
            "totalResultsCount" to (userResultsCount + postResultsCount + reelResultsCount)
        )
        writeJson("search_state.json", data)
    }

    /** Record a new post creation event with all its settings */
    fun exportNewPostEvent(
        postId: String,
        caption: String,
        location: String?,
        hashtags: List<String>,
        musicTitle: String?,
        audience: String,
        shareToFacebook: Boolean,
        hideLikesAndViews: Boolean,
        turnOffComments: Boolean,
        isReel: Boolean
    ) {
        val dir = autotestDir ?: return
        val eventFile = File(dir, "new_post_events.json")

        // Read existing events
        val existing: MutableList<Map<String, Any?>> = try {
            if (eventFile.exists()) {
                gson.fromJson(eventFile.readText(),
                    com.google.gson.reflect.TypeToken.getParameterized(
                        List::class.java, Map::class.java
                    ).type
                ) ?: mutableListOf()
            } else mutableListOf()
        } catch (_: Exception) {
            mutableListOf()
        }

        existing.add(mapOf(
            "postId" to postId,
            "caption" to caption,
            "location" to location,
            "hashtags" to hashtags,
            "musicTitle" to musicTitle,
            "audience" to audience,
            "shareToFacebook" to shareToFacebook,
            "hideLikesAndViews" to hideLikesAndViews,
            "turnOffComments" to turnOffComments,
            "isReel" to isReel,
            "timestamp" to System.currentTimeMillis()
        ))

        writeJson("new_post_events.json", existing)
    }
}
