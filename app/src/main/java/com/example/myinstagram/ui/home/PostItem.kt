package com.example.myinstagram.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.myinstagram.model.Comment
import com.example.myinstagram.model.Post
import com.example.myinstagram.model.User
import com.example.myinstagram.ui.components.CommentBottomSheet
import com.example.myinstagram.ui.components.MenuBottomSheet
import com.example.myinstagram.ui.components.ShareBottomSheet
import com.example.myinstagram.ui.components.UserAvatar
import com.example.myinstagram.ui.theme.*

@Composable
fun PostItem(
    post: Post,
    postUser: User?,
    currentUserId: String,
    isLiked: Boolean,
    isSaved: Boolean,
    isFollowing: Boolean,
    allUsers: List<User>,
    onLikeClick: () -> Unit,
    onSaveClick: () -> Unit,
    onFollowClick: () -> Unit,
    onAvatarClick: (String) -> Unit,
    onAddComment: (String) -> Unit,
    onToggleLikeComment: (String) -> Unit,
    isCommentLiked: (Comment) -> Boolean,
    getUserById: (String) -> User?,
    onShowToast: (String) -> Unit
) {
    var showShareSheet by remember { mutableStateOf(false) }
    var showMenuSheet by remember { mutableStateOf(false) }
    var showCommentSheet by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Post Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.clickable {
                postUser?.userId?.let { onAvatarClick(it) }
            }) {
                UserAvatar(
                    username = postUser?.username ?: "",
                    size = 32.dp,
                    avatarUrl = postUser?.avatarUrl ?: ""
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = postUser?.username ?: "",
                        color = InstagramWhite,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        modifier = Modifier.clickable {
                            postUser?.userId?.let { onAvatarClick(it) }
                        }
                    )
                    if (postUser?.isVerified == true) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.Filled.Verified,
                            contentDescription = "Verified",
                            tint = InstagramBlue,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                if (post.location != null) {
                    Text(
                        text = post.location,
                        color = InstagramTextGray,
                        fontSize = 11.sp
                    )
                }
            }
            // Follow button (only for non-self, non-followed users)
            if (postUser != null && !postUser.isCurrentUser) {
                if (!isFollowing) {
                    Text(
                        text = "Follow",
                        color = InstagramBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier
                            .clickable { onFollowClick() }
                            .padding(horizontal = 8.dp)
                    )
                } else {
                    Text(
                        text = "Following",
                        color = InstagramTextGray,
                        fontSize = 13.sp,
                        modifier = Modifier
                            .clickable { onFollowClick() }
                            .padding(horizontal = 8.dp)
                    )
                }
            }
            Icon(
                Icons.Filled.MoreVert,
                contentDescription = "More",
                tint = InstagramWhite,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { showMenuSheet = true }
            )
        }

        // Post Image
        if (post.imageUrl.isNotEmpty()) {
            val context = LocalContext.current
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(context)
                        .data("file:///android_asset/${post.imageUrl}")
                        .crossfade(true)
                        .build()
                ),
                contentDescription = "Post image",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(generatePostColor(post.postId)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "\uD83D\uDCF7", fontSize = 48.sp)
            }
        }

        // Action Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Like
            Icon(
                if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = "Like",
                tint = if (isLiked) InstagramLikeRed else InstagramWhite,
                modifier = Modifier
                    .size(26.dp)
                    .clickable { onLikeClick() }
            )
            Spacer(modifier = Modifier.width(16.dp))
            // Comment
            Icon(
                Icons.Filled.ChatBubbleOutline,
                contentDescription = "Comment",
                tint = InstagramWhite,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { showCommentSheet = true }
            )
            Spacer(modifier = Modifier.width(16.dp))
            // Share (paper airplane)
            Icon(
                Icons.Outlined.Send,
                contentDescription = "Share",
                tint = InstagramWhite,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { showShareSheet = true }
            )
            Spacer(modifier = Modifier.width(16.dp))
            // Repost
            Icon(
                Icons.Filled.Repeat,
                contentDescription = "Repost",
                tint = InstagramWhite,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onShowToast("Reposted") }
            )
            Spacer(modifier = Modifier.weight(1f))
            // Save/Bookmark
            Icon(
                if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                contentDescription = "Save",
                tint = InstagramWhite,
                modifier = Modifier
                    .size(26.dp)
                    .clickable {
                        onSaveClick()
                        onShowToast(if (!isSaved) "Saved" else "Removed from saved")
                    }
            )
        }

        // Likes count
        if (post.likedBy.isNotEmpty()) {
            Text(
                text = "${formatCount(post.likedBy.size)} likes",
                color = InstagramWhite,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 14.dp)
            )
        }

        // Caption
        if (post.caption.isNotEmpty()) {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                        append(postUser?.username ?: "")
                    }
                    append(" ")
                    append(post.caption)
                },
                color = InstagramWhite,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 2.dp),
                maxLines = 2
            )
        }

        // Comments preview
        if (post.comments.isNotEmpty()) {
            Text(
                text = "View all ${post.comments.size} comments",
                color = InstagramTextGray,
                fontSize = 13.sp,
                modifier = Modifier
                    .padding(horizontal = 14.dp, vertical = 2.dp)
                    .clickable { showCommentSheet = true }
            )
        }

        // Timestamp
        Text(
            text = post.timestamp,
            color = InstagramTextGray,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 2.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))
    }

    // Share Bottom Sheet
    if (showShareSheet) {
        ShareBottomSheet(
            users = allUsers.filter { !it.isCurrentUser },
            onDismiss = { showShareSheet = false }
        )
    }

    // Menu Bottom Sheet
    if (showMenuSheet) {
        MenuBottomSheet(
            isFollowing = isFollowing,
            onUnfollow = { onFollowClick() },
            onDismiss = { showMenuSheet = false }
        )
    }

    // Comment Bottom Sheet
    if (showCommentSheet) {
        CommentBottomSheet(
            comments = post.comments,
            getUserById = getUserById,
            currentUserId = currentUserId,
            onAddComment = onAddComment,
            onToggleLikeComment = { commentId -> onToggleLikeComment(commentId) },
            isCommentLiked = isCommentLiked,
            onDismiss = { showCommentSheet = false }
        )
    }
}

private fun generatePostColor(postId: String): Color {
    val colors = listOf(
        Color(0xFFE8D5C4), Color(0xFFD5C4B3), Color(0xFFC4D5E0),
        Color(0xFFB3C4D5), Color(0xFFC4B3D5)
    )
    return colors[kotlin.math.abs(postId.hashCode()) % colors.size]
}

private fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
        count >= 1_000 -> String.format("%.1fK", count / 1_000.0)
        else -> count.toString()
    }
}
