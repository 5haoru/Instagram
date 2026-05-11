package com.example.myinstagram.presenter

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.myinstagram.data.AutoTestExporter
import com.example.myinstagram.data.DataRepository
import com.example.myinstagram.model.LocationItem
import com.example.myinstagram.model.MusicTrack
import com.example.myinstagram.model.Post
import com.example.myinstagram.model.Reel
import com.example.myinstagram.model.User

enum class NewPostStep {
    GALLERY, DRAFT, VOTE, MUSIC, LOCATION, AUDIENCE, SHARE_TO, MORE_OPTIONS
}

enum class AudienceType(val label: String) {
    EVERYONE("Everyone"),
    CLOSE_FRIENDS("Close Friends"),
    FOLLOWERS_EXCEPT("Followers Except...")
}

enum class CreateMode(val label: String) {
    POST("Post"),
    REEL("Reel"),
    LIVE("Live")
}

class NewPostPresenter {

    var currentUser by mutableStateOf<User?>(null)
        private set

    // Navigation
    var currentStep by mutableStateOf(NewPostStep.GALLERY)

    // Create mode
    var createMode by mutableStateOf(CreateMode.POST)

    // Gallery (images for post)
    val availableImages: List<String> = (1..10).map { "image/$it.jpeg" }
    var selectedImageIndex by mutableIntStateOf(0)
        private set
    val selectedImage: String
        get() = availableImages[selectedImageIndex]

    // Videos (for reel)
    val availableVideos: List<String> = (1..5).map { "reels/$it.mp4" }
    var selectedVideoIndex by mutableIntStateOf(0)
        private set
    val selectedVideo: String
        get() = availableVideos[selectedVideoIndex]

    // Caption
    var caption by mutableStateOf("")

    // Vote/Poll
    var voteQuestion by mutableStateOf("")
    val voteOptions = mutableStateListOf("", "")

    // Hashtags
    val hashtags = mutableStateListOf<String>()

    // Music
    var musicTracks by mutableStateOf<List<MusicTrack>>(emptyList())
        private set
    var selectedMusic by mutableStateOf<MusicTrack?>(null)

    // Location
    var locationItems by mutableStateOf<List<LocationItem>>(emptyList())
        private set
    var selectedLocation by mutableStateOf<LocationItem?>(null)

    // Audience
    var selectedAudience by mutableStateOf(AudienceType.EVERYONE)

    // Share-to toggles
    var shareToFacebook by mutableStateOf(false)
    var shareToThreads by mutableStateOf(false)
    var shareToX by mutableStateOf(false)

    // More options
    var hideLikesAndViews by mutableStateOf(false)
    var turnOffComments by mutableStateOf(false)

    fun loadData() {
        currentUser = DataRepository.getCurrentUser()
        musicTracks = DataRepository.getMusic()
        locationItems = DataRepository.getLocations()
    }

    fun selectImage(index: Int) {
        if (index in availableImages.indices) {
            selectedImageIndex = index
        }
    }

    fun selectVideo(index: Int) {
        if (index in availableVideos.indices) {
            selectedVideoIndex = index
        }
    }

    fun onNextFromGallery() {
        currentStep = NewPostStep.DRAFT
    }

    // Vote helpers
    fun addVoteOption() {
        if (voteOptions.size < 4) {
            voteOptions.add("")
        }
    }

    fun updateVoteOption(index: Int, text: String) {
        if (index in voteOptions.indices) {
            voteOptions[index] = text
        }
    }

    fun removeVoteOption(index: Int) {
        if (index in voteOptions.indices && voteOptions.size > 2) {
            voteOptions.removeAt(index)
        }
    }

    // Hashtag helpers
    fun addHashtag(tag: String) {
        val cleaned = tag.trim().removePrefix("#")
        if (cleaned.isNotEmpty() && cleaned !in hashtags) {
            hashtags.add(cleaned)
        }
    }

    fun removeHashtag(index: Int) {
        if (index in hashtags.indices) {
            hashtags.removeAt(index)
        }
    }

    fun sharePost() {
        val userId = currentUser?.userId ?: return
        if (createMode == CreateMode.REEL) {
            shareReel()
            return
        }
        val postId = "post_${System.currentTimeMillis()}"
        val post = Post(
            postId = postId,
            userId = userId,
            imageUrl = selectedImage,
            caption = caption,
            location = selectedLocation?.name,
            timestamp = "Just now"
        )
        DataRepository.addPost(post)
        AutoTestExporter.exportNewPostEvent(
            postId = postId,
            caption = caption,
            location = selectedLocation?.name,
            hashtags = hashtags.toList(),
            musicTitle = selectedMusic?.title,
            audience = selectedAudience.label,
            shareToFacebook = shareToFacebook,
            hideLikesAndViews = hideLikesAndViews,
            turnOffComments = turnOffComments,
            isReel = false
        )
    }

    private fun shareReel() {
        val userId = currentUser?.userId ?: return
        val reelId = "reel_${System.currentTimeMillis()}"
        val reel = Reel(
            reelId = reelId,
            userId = userId,
            videoUrl = selectedVideo,
            caption = caption,
            audioName = selectedMusic?.title ?: "Original audio"
        )
        DataRepository.addReel(reel)
        AutoTestExporter.exportNewPostEvent(
            postId = reelId,
            caption = caption,
            location = selectedLocation?.name,
            hashtags = hashtags.toList(),
            musicTitle = selectedMusic?.title,
            audience = selectedAudience.label,
            shareToFacebook = shareToFacebook,
            hideLikesAndViews = hideLikesAndViews,
            turnOffComments = turnOffComments,
            isReel = true
        )
    }

    fun resetDraft() {
        currentStep = NewPostStep.GALLERY
        selectedImageIndex = 0
        selectedVideoIndex = 0
        caption = ""
        voteQuestion = ""
        voteOptions.clear()
        voteOptions.addAll(listOf("", ""))
        hashtags.clear()
        selectedMusic = null
        selectedLocation = null
        selectedAudience = AudienceType.EVERYONE
        shareToFacebook = false
        shareToThreads = false
        shareToX = false
        hideLikesAndViews = false
        turnOffComments = false
    }
}
