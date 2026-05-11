package com.example.myinstagram.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myinstagram.data.DataRepository
import com.example.myinstagram.presenter.HomePresenter
import com.example.myinstagram.ui.home.PostItem
import com.example.myinstagram.ui.theme.InstagramBlack
import com.example.myinstagram.ui.theme.InstagramMediumGray
import com.example.myinstagram.ui.theme.InstagramWhite
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    postId: String,
    homePresenter: HomePresenter,
    onBack: () -> Unit,
    onNavigateToUserProfile: (String) -> Unit,
    onShowToast: (String) -> Unit
) {
    LaunchedEffect(Unit) { homePresenter.loadData() }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val showToast: (String) -> Unit = { message ->
        coroutineScope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(message)
        }
    }

    // Read contentVersion to trigger recomposition when data changes
    val version = DataRepository.contentVersion
    val post = DataRepository.getPosts().find { it.postId == postId }
    val currentUser = DataRepository.getCurrentUser()

    if (post == null || currentUser == null) {
        onBack()
        return
    }

    val postUser = DataRepository.getUserById(post.userId)
    val isLiked = post.likedBy.contains(currentUser.userId)
    val isSaved = post.savedBy.contains(currentUser.userId)
    val isFollowing = currentUser.following.contains(post.userId)

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
        topBar = {
            TopAppBar(
                title = { Text("Post", color = InstagramWhite) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = InstagramWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = InstagramBlack)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(InstagramBlack)
        ) {
            item {
                PostItem(
                    post = post,
                    postUser = postUser,
                    currentUserId = currentUser.userId,
                    isLiked = isLiked,
                    isSaved = isSaved,
                    isFollowing = isFollowing,
                    allUsers = DataRepository.getUsers(),
                    onLikeClick = { homePresenter.toggleLikePost(post.postId) },
                    onSaveClick = { homePresenter.toggleSavePost(post.postId) },
                    onFollowClick = { homePresenter.toggleFollowUser(post.userId) },
                    onHidePost = { onBack() },
                    onAvatarClick = onNavigateToUserProfile,
                    onAddComment = { text -> homePresenter.addCommentToPost(post.postId, text) },
                    onToggleLikeComment = { commentId -> homePresenter.toggleLikeComment(post.postId, commentId) },
                    isCommentLiked = { comment -> comment.likedBy.contains(currentUser.userId) },
                    getUserById = { userId -> DataRepository.getUserById(userId) },
                    onRepostClick = { homePresenter.repostPost(post.postId) },
                    onShowToast = showToast
                )
            }
        }
    }
}
