package com.example.myinstagram.ui.reels

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.myinstagram.model.Reel
import com.example.myinstagram.model.User
import com.example.myinstagram.presenter.ReelsPresenter
import com.example.myinstagram.ui.components.CommentBottomSheet
import com.example.myinstagram.ui.components.MenuBottomSheet
import com.example.myinstagram.ui.components.ShareBottomSheet
import com.example.myinstagram.ui.components.UserAvatar
import com.example.myinstagram.ui.theme.*
import com.example.myinstagram.ui.theme.Color_White

@Composable
fun ReelsScreen(presenter: ReelsPresenter, modifier: Modifier = Modifier) {
    LaunchedEffect(Unit) { presenter.loadData() }

    if (presenter.reels.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { presenter.reels.size })

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        VerticalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            val reel = presenter.reels[page]
            val user = presenter.getUserById(reel.userId)
            ReelItem(
                reel = reel,
                user = user,
                presenter = presenter,
                isCurrentPage = pagerState.currentPage == page
            )
        }

        // Top bar overlay
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Reels",
                color = Color_White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Icon(
                Icons.Filled.CameraAlt,
                contentDescription = "Camera",
                tint = Color_White,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

@Composable
private fun ReelItem(
    reel: Reel,
    user: User?,
    presenter: ReelsPresenter,
    isCurrentPage: Boolean
) {
    val context = LocalContext.current
    val isLiked = presenter.isReelLiked(reel)
    val isSaved = presenter.isReelSaved(reel)

    var showComments by remember { mutableStateOf(false) }
    var showShare by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    // Comment bottom sheet
    if (showComments) {
        CommentBottomSheet(
            comments = reel.comments,
            getUserById = { presenter.getUserById(it) },
            currentUserId = presenter.currentUser?.userId ?: "",
            onAddComment = { text -> presenter.addCommentToReel(reel.reelId, text) },
            onToggleLikeComment = { commentId -> presenter.toggleLikeReelComment(reel.reelId, commentId) },
            isCommentLiked = { presenter.isCommentLiked(it) },
            onDismiss = { showComments = false }
        )
    }

    // Share bottom sheet
    if (showShare) {
        ShareBottomSheet(
            users = presenter.getUsers().filter { !it.isCurrentUser },
            onDismiss = { showShare = false }
        )
    }

    // Menu bottom sheet
    if (showMenu) {
        val isFollowing = user?.userId?.let { presenter.isFollowingUser(it) } ?: false
        MenuBottomSheet(
            isFollowing = isFollowing,
            onUnfollow = { user?.userId?.let { presenter.toggleFollowUser(it) } },
            onDismiss = { showMenu = false }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Video player
        if (reel.videoUrl.isNotEmpty()) {
            val exoPlayer = remember(reel.reelId) {
                ExoPlayer.Builder(context).build().apply {
                    val uri = Uri.parse("asset:///${reel.videoUrl}")
                    setMediaItem(MediaItem.fromUri(uri))
                    repeatMode = Player.REPEAT_MODE_ONE
                    volume = 1f
                    prepare()
                }
            }

            LaunchedEffect(isCurrentPage) {
                if (isCurrentPage) {
                    exoPlayer.play()
                } else {
                    exoPlayer.pause()
                    exoPlayer.seekTo(0)
                }
            }

            DisposableEffect(reel.reelId) {
                onDispose {
                    exoPlayer.release()
                }
            }

            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = false
                        setShowBuffering(PlayerView.SHOW_BUFFERING_NEVER)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Right side actions
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Like
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (isLiked) InstagramLikeRed else Color_White,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { presenter.toggleLikeReel(reel.reelId) }
                )
                Text(
                    text = formatCount(reel.likedBy.size),
                    color = Color_White,
                    fontSize = 12.sp
                )
            }
            // Comment
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Filled.ChatBubbleOutline,
                    contentDescription = "Comment",
                    tint = Color_White,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { showComments = true }
                )
                Text(
                    text = reel.comments.size.toString(),
                    color = Color_White,
                    fontSize = 12.sp
                )
            }
            // Share
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Outlined.Send,
                    contentDescription = "Share",
                    tint = Color_White,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { showShare = true }
                )
                Text(
                    text = formatCount(reel.shareCount),
                    color = Color_White,
                    fontSize = 12.sp
                )
            }
            // Save
            Icon(
                if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                contentDescription = "Save",
                tint = Color_White,
                modifier = Modifier
                    .size(28.dp)
                    .clickable { presenter.toggleSaveReel(reel.reelId) }
            )
            // Menu
            Icon(
                Icons.Filled.MoreVert,
                contentDescription = "Menu",
                tint = Color_White,
                modifier = Modifier
                    .size(28.dp)
                    .clickable { showMenu = true }
            )
        }

        // Bottom info
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 12.dp, bottom = 16.dp, end = 60.dp)
        ) {
            // Username
            Row(verticalAlignment = Alignment.CenterVertically) {
                UserAvatar(username = user?.username ?: "", size = 32.dp, avatarUrl = user?.avatarUrl ?: "")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = user?.username ?: "",
                    color = Color_White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Caption
            Text(
                text = reel.caption,
                color = Color_White,
                fontSize = 13.sp,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Audio
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.MusicNote,
                    contentDescription = "Audio",
                    tint = Color_White,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = reel.audioName,
                    color = Color_White,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }
        }
    }
}

private fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
        count >= 1_000 -> String.format("%.1fK", count / 1_000.0)
        else -> count.toString()
    }
}
