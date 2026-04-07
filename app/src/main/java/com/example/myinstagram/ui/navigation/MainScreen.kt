package com.example.myinstagram.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.myinstagram.presenter.*
import com.example.myinstagram.ui.home.HomeScreen
import com.example.myinstagram.ui.messages.ChatDetailScreen
import com.example.myinstagram.ui.messages.MessagesScreen
import com.example.myinstagram.ui.notifications.NotificationsScreen
import com.example.myinstagram.ui.post.NewPostScreen
import com.example.myinstagram.ui.profile.EditProfileScreen
import com.example.myinstagram.ui.profile.FollowersFollowingScreen
import com.example.myinstagram.ui.profile.OtherUserProfileScreen
import com.example.myinstagram.ui.profile.ProfileScreen
import com.example.myinstagram.ui.profile.ShareProfileScreen
import com.example.myinstagram.ui.reels.ReelsScreen
import com.example.myinstagram.ui.search.SearchScreen
import com.example.myinstagram.ui.settings.SettingsScreen
import com.example.myinstagram.ui.theme.InstagramBlack
import com.example.myinstagram.ui.theme.InstagramLightGray
import com.example.myinstagram.ui.theme.InstagramMediumGray
import com.example.myinstagram.ui.theme.InstagramWhite
import com.example.myinstagram.ui.theme.Color_White

data class BottomNavItem(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val label: String
)

@Composable
fun MainScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    var viewingUserId by remember { mutableStateOf<String?>(null) }
    var showEditProfile by remember { mutableStateOf(false) }
    var showShareProfile by remember { mutableStateOf(false) }
    var showNotifications by remember { mutableStateOf(false) }
    var showNewPost by remember { mutableStateOf(false) }
    var showFollowersFollowing by remember { mutableStateOf(false) }
    var followersFollowingInitialTab by remember { mutableIntStateOf(0) }
    var chatConversationId by remember { mutableStateOf<String?>(null) }
    var showSettings by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val homePresenter = remember { HomePresenter() }
    val searchPresenter = remember { SearchPresenter() }
    val reelsPresenter = remember { ReelsPresenter() }
    val messagesPresenter = remember { MessagesPresenter() }
    val profilePresenter = remember { ProfilePresenter() }
    val otherUserProfilePresenter = remember { OtherUserProfilePresenter() }
    val editProfilePresenter = remember { EditProfilePresenter() }
    val notificationsPresenter = remember { NotificationsPresenter() }
    val newPostPresenter = remember { NewPostPresenter() }
    val followersFollowingPresenter = remember { FollowersFollowingPresenter() }

    val showToast: (String) -> Unit = { message ->
        coroutineScope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(message)
        }
    }

    val navItems = listOf(
        BottomNavItem(Icons.Filled.Home, Icons.Outlined.Home, "Home"),
        BottomNavItem(Icons.Filled.Search, Icons.Outlined.Search, "Search"),
        BottomNavItem(Icons.Filled.VideoLibrary, Icons.Outlined.VideoLibrary, "Reels"),
        BottomNavItem(
            Icons.Filled.AccountCircle,
            Icons.Outlined.ChatBubbleOutline,
            "Messages"
        ),
        BottomNavItem(Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle, "Profile")
    )

    // Full-screen overlays (no bottom nav)
    if (viewingUserId != null) {
        OtherUserProfileScreen(
            userId = viewingUserId!!,
            presenter = otherUserProfilePresenter,
            onBack = { viewingUserId = null }
        )
        return
    }

    if (showEditProfile) {
        EditProfileScreen(
            presenter = editProfilePresenter,
            onBack = { showEditProfile = false }
        )
        return
    }

    if (showShareProfile) {
        ShareProfileScreen(
            presenter = profilePresenter,
            onBack = { showShareProfile = false }
        )
        return
    }

    if (showNotifications) {
        NotificationsScreen(
            presenter = notificationsPresenter,
            onBack = { showNotifications = false }
        )
        return
    }

    if (showNewPost) {
        NewPostScreen(
            presenter = newPostPresenter,
            onBack = { showNewPost = false },
            onShowToast = showToast
        )
        return
    }

    if (showFollowersFollowing) {
        FollowersFollowingScreen(
            presenter = followersFollowingPresenter,
            initialTab = followersFollowingInitialTab,
            onBack = {
                showFollowersFollowing = false
                profilePresenter.loadData()
            }
        )
        return
    }

    if (chatConversationId != null) {
        ChatDetailScreen(
            conversationId = chatConversationId!!,
            presenter = messagesPresenter,
            onBack = {
                chatConversationId = null
                messagesPresenter.loadData()
            }
        )
        return
    }

    if (showSettings) {
        SettingsScreen(
            onBack = { showSettings = false }
        )
        return
    }

    Scaffold(
        containerColor = InstagramBlack,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Snackbar(
                        containerColor = InstagramMediumGray,
                        contentColor = InstagramWhite,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = data.visuals.message,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        },
        bottomBar = {
            val isReelsTab = selectedTab == 2
            NavigationBar(
                containerColor = if (isReelsTab) Color.Black else InstagramBlack,
                tonalElevation = 0.dp
            ) {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == index) item.selectedIcon
                                else item.unselectedIcon,
                                contentDescription = item.label
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = if (isReelsTab) Color_White else InstagramWhite,
                            unselectedIconColor = if (isReelsTab) Color_White else InstagramWhite,
                            indicatorColor = if (isReelsTab) Color(0xFF363636) else InstagramLightGray
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        when (selectedTab) {
            0 -> HomeScreen(
                presenter = homePresenter,
                onNavigateToUserProfile = { userId -> viewingUserId = userId },
                onShowToast = showToast,
                onNavigateToNotifications = { showNotifications = true },
                onNavigateToNewPost = { showNewPost = true },
                modifier = modifier
            )
            1 -> SearchScreen(searchPresenter, modifier)
            2 -> ReelsScreen(reelsPresenter, Modifier.padding(bottom = innerPadding.calculateBottomPadding()))
            3 -> MessagesScreen(messagesPresenter, onOpenChat = { chatConversationId = it }, modifier)
            4 -> ProfileScreen(
                presenter = profilePresenter,
                onEditProfile = { showEditProfile = true },
                onShareProfile = { showShareProfile = true },
                onMenuClick = { showSettings = true },
                onNavigateToFollowers = {
                    followersFollowingInitialTab = 0
                    showFollowersFollowing = true
                },
                onNavigateToFollowing = {
                    followersFollowingInitialTab = 1
                    showFollowersFollowing = true
                },
                modifier = modifier
            )
        }
    }
}
