package com.example.myinstagram.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.myinstagram.data.DataRepository
import com.example.myinstagram.model.Post
import com.example.myinstagram.model.Reel
import com.example.myinstagram.presenter.ProfilePresenter
import com.example.myinstagram.ui.components.UserAvatar
import com.example.myinstagram.ui.theme.*

@Composable
fun ProfileScreen(
    presenter: ProfilePresenter,
    onEditProfile: () -> Unit,
    onShareProfile: () -> Unit,
    onMenuClick: () -> Unit = {},
    onNavigateToFollowers: () -> Unit,
    onNavigateToFollowing: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) { presenter.loadData() }

    val user = presenter.currentUser ?: return

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(InstagramBlack)
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = user.username,
                color = InstagramWhite,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Icon(
                Icons.Filled.Menu,
                contentDescription = "Menu",
                tint = InstagramWhite,
                modifier = Modifier
                    .size(26.dp)
                    .clickable { onMenuClick() }
            )
        }

        // Profile info section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserAvatar(username = user.username, size = 80.dp, avatarUrl = user.avatarUrl)
            Spacer(modifier = Modifier.width(24.dp))
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatColumn(count = user.postsCount.toString(), label = "Posts")
                StatColumn(
                    count = formatCount(user.followersCount),
                    label = "Followers",
                    onClick = onNavigateToFollowers
                )
                StatColumn(
                    count = formatCount(user.followingCount),
                    label = "Following",
                    onClick = onNavigateToFollowing
                )
            }
        }

        // Name and bio
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
            Text(
                text = user.displayName,
                color = InstagramWhite,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            if (user.bio.isNotEmpty()) {
                Text(
                    text = user.bio,
                    color = InstagramWhite,
                    fontSize = 14.sp
                )
            }
        }

        // Action buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onEditProfile,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = InstagramMediumGray),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(vertical = 6.dp)
            ) {
                Text("Edit profile", color = InstagramWhite, fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold)
            }
            Button(
                onClick = onShareProfile,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = InstagramMediumGray),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(vertical = 6.dp)
            ) {
                Text("Share profile", color = InstagramWhite, fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold)
            }
        }

        // Content tabs
        var selectedContentTab by remember { mutableIntStateOf(0) }
        TabRow(
            selectedTabIndex = selectedContentTab,
            containerColor = InstagramBlack,
            contentColor = InstagramWhite,
            divider = { HorizontalDivider(color = InstagramLightGray, thickness = 0.5.dp) }
        ) {
            Tab(
                selected = selectedContentTab == 0,
                onClick = { selectedContentTab = 0 },
                icon = {
                    Icon(Icons.Filled.GridOn, contentDescription = "Posts",
                        modifier = Modifier.size(24.dp))
                },
                selectedContentColor = InstagramWhite,
                unselectedContentColor = InstagramTextGray
            )
            Tab(
                selected = selectedContentTab == 1,
                onClick = { selectedContentTab = 1 },
                icon = {
                    Icon(Icons.Filled.VideoLibrary, contentDescription = "Reels",
                        modifier = Modifier.size(24.dp))
                },
                selectedContentColor = InstagramWhite,
                unselectedContentColor = InstagramTextGray
            )
            Tab(
                selected = selectedContentTab == 2,
                onClick = { selectedContentTab = 2 },
                icon = {
                    Icon(Icons.Filled.PersonPin, contentDescription = "Tagged",
                        modifier = Modifier.size(24.dp))
                },
                selectedContentColor = InstagramWhite,
                unselectedContentColor = InstagramTextGray
            )
        }

        // Content based on selected tab
        when (selectedContentTab) {
            0 -> PostsGrid(presenter.userPosts)
            1 -> ReelsGrid()
            2 -> {
                // Tagged tab - empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tagged posts",
                        color = InstagramTextGray,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun PostsGrid(posts: List<Post>) {
    val allImages = (1..10).map { "image/$it.jpeg" }
    // Combine actual posts with additional images to fill the grid
    val displayItems = posts.map { it.imageUrl }.filter { it.isNotEmpty() } +
            allImages.filter { img -> posts.none { it.imageUrl == img } }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        items(displayItems) { imageUrl ->
            val context = LocalContext.current
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(context)
                        .data("file:///android_asset/$imageUrl")
                        .crossfade(true)
                        .build()
                ),
                contentDescription = "Post",
                modifier = Modifier.aspectRatio(1f),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun ReelsGrid() {
    val reels = DataRepository.getReels()
    // Show reels by current user, plus some others
    val displayReels = reels

    if (displayReels.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No reels yet",
                color = InstagramTextGray,
                fontSize = 14.sp
            )
        }
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        items(displayReels) { reel ->
            Box(
                modifier = Modifier
                    .aspectRatio(9f / 16f)
                    .background(Color(0xFF1A1A2E)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "\uD83C\uDFAC",
                    fontSize = 24.sp
                )
                // View count at bottom
                Text(
                    text = "\u25B6 ${formatReelViews(reel.likedBy.size)}",
                    color = Color_White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(4.dp)
                )
            }
        }
    }
}

@Composable
private fun StatColumn(
    count: String,
    label: String,
    onClick: (() -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = if (onClick != null) Modifier.clickable { onClick() } else Modifier
    ) {
        Text(
            text = count,
            color = InstagramWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Text(
            text = label,
            color = InstagramWhite,
            fontSize = 13.sp
        )
    }
}

private fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
        count >= 1_000 -> String.format("%.1fK", count / 1_000.0)
        else -> count.toString()
    }
}

private fun formatReelViews(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
        count >= 1_000 -> String.format("%.1fK", count / 1_000.0)
        else -> count.toString()
    }
}

private fun generateGridColor(id: String): Color {
    val colors = listOf(
        Color(0xFFE8D5C4), Color(0xFFD5C4B3), Color(0xFFC4D5E0),
        Color(0xFFB3C4D5), Color(0xFFC4B3D5)
    )
    return colors[kotlin.math.abs(id.hashCode()) % colors.size]
}
