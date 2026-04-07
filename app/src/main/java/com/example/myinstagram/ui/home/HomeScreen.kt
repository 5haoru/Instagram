package com.example.myinstagram.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myinstagram.model.Story
import com.example.myinstagram.model.User
import com.example.myinstagram.presenter.HomePresenter
import com.example.myinstagram.ui.components.UserAvatar
import com.example.myinstagram.ui.theme.*
import com.example.myinstagram.ui.theme.Color_White

@Composable
fun HomeScreen(
    presenter: HomePresenter,
    onNavigateToUserProfile: (String) -> Unit,
    onShowToast: (String) -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToNewPost: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) { presenter.loadData() }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(InstagramBlack)
    ) {
        // Top Bar
        item { HomeTopBar(onNavigateToNotifications, onNavigateToNewPost) }

        // Stories Bar
        item {
            StoriesBar(
                stories = presenter.stories,
                currentUser = presenter.currentUser,
                getUserById = { presenter.getUserById(it) },
                onAvatarClick = onNavigateToUserProfile
            )
        }

        // Suggested For You
        if (presenter.suggestedUsers.isNotEmpty()) {
            item {
                SuggestedSection(
                    suggestedUsers = presenter.suggestedUsers,
                    onAvatarClick = onNavigateToUserProfile
                )
            }
        }

        // Post Feed
        items(presenter.posts) { post ->
            val postUser = presenter.getUserById(post.userId)
            PostItem(
                post = post,
                postUser = postUser,
                currentUserId = presenter.currentUser?.userId ?: "",
                isLiked = presenter.isPostLiked(post),
                isSaved = presenter.isPostSaved(post),
                isFollowing = presenter.isFollowingUser(post.userId),
                allUsers = presenter.getUsers(),
                onLikeClick = { presenter.toggleLikePost(post.postId) },
                onSaveClick = { presenter.toggleSavePost(post.postId) },
                onFollowClick = { presenter.toggleFollowUser(post.userId) },
                onAvatarClick = onNavigateToUserProfile,
                onAddComment = { text -> presenter.addCommentToPost(post.postId, text) },
                onToggleLikeComment = { commentId -> presenter.toggleLikeComment(post.postId, commentId) },
                isCommentLiked = { comment -> presenter.isCommentLiked(comment) },
                getUserById = { presenter.getUserById(it) },
                onShowToast = onShowToast
            )
        }
    }
}

@Composable
private fun HomeTopBar(
    onNavigateToNotifications: () -> Unit,
    onNavigateToNewPost: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Filled.AddBox,
            contentDescription = "New Post",
            tint = InstagramWhite,
            modifier = Modifier
                .size(26.dp)
                .clickable { onNavigateToNewPost() }
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Instagram",
            color = InstagramWhite,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Cursive,
            fontStyle = FontStyle.Normal
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            Icons.Filled.FavoriteBorder,
            contentDescription = "Notifications",
            tint = InstagramWhite,
            modifier = Modifier
                .size(26.dp)
                .clickable { onNavigateToNotifications() }
        )
    }
}

@Composable
private fun StoriesBar(
    stories: List<Story>,
    currentUser: User?,
    getUserById: (String) -> User?,
    onAvatarClick: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier.padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // "Your Story" item
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(72.dp)
            ) {
                Box {
                    UserAvatar(
                        username = currentUser?.username ?: "me",
                        size = 64.dp,
                        avatarUrl = currentUser?.avatarUrl ?: ""
                    )
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(InstagramBlue)
                            .border(2.dp, InstagramBlack, CircleShape)
                            .align(Alignment.BottomEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "Add story",
                            tint = Color_White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Your story",
                    color = InstagramWhite,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
        }

        // Other users' stories
        items(stories) { story ->
            val user = getUserById(story.userId)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(72.dp)
                    .clickable { user?.userId?.let { onAvatarClick(it) } }
            ) {
                UserAvatar(
                    username = user?.username ?: "",
                    size = 64.dp,
                    showStoryRing = true,
                    avatarUrl = user?.avatarUrl ?: ""
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = user?.username ?: "",
                    color = InstagramWhite,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
        }
    }

    HorizontalDivider(color = InstagramLightGray, thickness = 0.5.dp)
}

@Composable
private fun SuggestedSection(
    suggestedUsers: List<User>,
    onAvatarClick: (String) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Suggested for you",
                color = InstagramWhite,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Text(
                text = "See All",
                color = InstagramBlue,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                modifier = Modifier.clickable { }
            )
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(suggestedUsers) { user ->
                Card(
                    modifier = Modifier
                        .width(160.dp)
                        .clickable { onAvatarClick(user.userId) },
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = InstagramMediumGray)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        UserAvatar(username = user.username, size = 64.dp, avatarUrl = user.avatarUrl)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = user.username,
                            color = InstagramWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = InstagramBlue
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(vertical = 6.dp)
                        ) {
                            Text("Follow", color = Color_White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }

    HorizontalDivider(color = InstagramLightGray, thickness = 0.5.dp)
}
