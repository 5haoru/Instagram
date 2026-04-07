package com.example.myinstagram.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.myinstagram.presenter.OtherUserProfilePresenter
import com.example.myinstagram.ui.components.UserAvatar
import com.example.myinstagram.ui.theme.*
import com.example.myinstagram.ui.theme.Color_White

@Composable
fun OtherUserProfileScreen(
    userId: String,
    presenter: OtherUserProfilePresenter,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(userId) { presenter.loadUser(userId) }

    val user = presenter.targetUser ?: return

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(InstagramBlack)
            .statusBarsPadding()
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = InstagramWhite
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = user.username,
                    color = InstagramWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                if (user.isVerified) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        Icons.Filled.Verified,
                        contentDescription = "Verified",
                        tint = InstagramBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }
                if (user.isPrivate) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        Icons.Filled.Lock,
                        contentDescription = "Private",
                        tint = InstagramTextGray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            IconButton(onClick = { }) {
                Icon(
                    Icons.Filled.MoreVert,
                    contentDescription = "More",
                    tint = InstagramWhite
                )
            }
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
                StatColumn(count = formatCount(user.followersCount), label = "Followers")
                StatColumn(count = formatCount(user.followingCount), label = "Following")
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

        // Action buttons (Follow / Message)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { presenter.toggleFollow() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (presenter.isFollowing) InstagramMediumGray else InstagramBlue
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(vertical = 6.dp)
            ) {
                Text(
                    text = if (presenter.isFollowing) "Following" else "Follow",
                    color = if (presenter.isFollowing) InstagramWhite else Color_White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Button(
                onClick = { },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = InstagramMediumGray),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(vertical = 6.dp)
            ) {
                Text(
                    text = "Message",
                    color = InstagramWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
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

        // Content area
        if (user.isPrivate && !presenter.isFollowing) {
            // Private account notice
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.Lock,
                        contentDescription = "Private",
                        tint = InstagramWhite,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "This Account is Private",
                        color = InstagramWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Follow this account to see their photos and videos.",
                        color = InstagramTextGray,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // Posts grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(presenter.userPosts) { post ->
                    if (post.imageUrl.isNotEmpty()) {
                        val context = LocalContext.current
                        Image(
                            painter = rememberAsyncImagePainter(
                                ImageRequest.Builder(context)
                                    .data("file:///android_asset/${post.imageUrl}")
                                    .crossfade(true)
                                    .build()
                            ),
                            contentDescription = "Post",
                            modifier = Modifier.aspectRatio(1f),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .background(Color(0xFFE8D5C4)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("\uD83D\uDCF7", fontSize = 24.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatColumn(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
