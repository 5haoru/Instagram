package com.example.myinstagram.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myinstagram.model.User
import com.example.myinstagram.presenter.FollowersFollowingPresenter
import com.example.myinstagram.ui.components.UserAvatar
import com.example.myinstagram.ui.theme.*

@Composable
fun FollowersFollowingScreen(
    presenter: FollowersFollowingPresenter,
    initialTab: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(initialTab) { presenter.loadData(initialTab) }

    val user = presenter.currentUser ?: return

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
            Text(
                text = user.username,
                color = InstagramWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Tabs: Followers / Following
        TabRow(
            selectedTabIndex = presenter.selectedTab,
            containerColor = InstagramBlack,
            contentColor = InstagramWhite,
            divider = { HorizontalDivider(color = InstagramLightGray, thickness = 0.5.dp) }
        ) {
            Tab(
                selected = presenter.selectedTab == 0,
                onClick = { presenter.selectedTab = 0 },
                text = {
                    Text(
                        text = "${formatCount(presenter.followerUsers.size)} Followers",
                        fontWeight = if (presenter.selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 14.sp
                    )
                },
                selectedContentColor = InstagramWhite,
                unselectedContentColor = InstagramTextGray
            )
            Tab(
                selected = presenter.selectedTab == 1,
                onClick = { presenter.selectedTab = 1 },
                text = {
                    Text(
                        text = "${formatCount(presenter.followingUsers.size)} Following",
                        fontWeight = if (presenter.selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 14.sp
                    )
                },
                selectedContentColor = InstagramWhite,
                unselectedContentColor = InstagramTextGray
            )
        }

        // User list
        val users = if (presenter.selectedTab == 0) presenter.followerUsers else presenter.followingUsers

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(users, key = { it.userId }) { listUser ->
                UserListItem(
                    user = listUser,
                    isFollowersTab = presenter.selectedTab == 0,
                    isFollowing = presenter.isFollowing(listUser.userId),
                    onActionClick = {
                        if (presenter.selectedTab == 0) {
                            presenter.removeFollower(listUser.userId)
                        } else {
                            presenter.toggleFollow(listUser.userId)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun UserListItem(
    user: User,
    isFollowersTab: Boolean,
    isFollowing: Boolean,
    onActionClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserAvatar(
            username = user.username,
            size = 48.dp,
            avatarUrl = user.avatarUrl
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.username,
                color = InstagramWhite,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Text(
                text = user.displayName,
                color = InstagramTextGray,
                fontSize = 13.sp
            )
        }
        Spacer(modifier = Modifier.width(8.dp))

        if (isFollowersTab) {
            Button(
                onClick = onActionClick,
                colors = ButtonDefaults.buttonColors(containerColor = InstagramMediumGray),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Remove",
                    color = InstagramWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        } else {
            Button(
                onClick = onActionClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFollowing) InstagramMediumGray else InstagramBlue
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isFollowing) "Following" else "Follow",
                    color = if (isFollowing) InstagramWhite else Color_White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
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
