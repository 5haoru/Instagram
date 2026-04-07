package com.example.myinstagram.ui.post

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Poll
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.myinstagram.presenter.NewPostPresenter
import com.example.myinstagram.presenter.NewPostStep
import com.example.myinstagram.ui.components.UserAvatar
import com.example.myinstagram.ui.theme.*

@Composable
fun NewPostDraftScreen(
    presenter: NewPostPresenter,
    onBack: () -> Unit,
    onShare: () -> Unit
) {
    val context = LocalContext.current
    var showFullscreenImage by remember { mutableStateOf(false) }
    var hashtagInput by remember { mutableStateOf("") }

    // Fullscreen image preview dialog
    if (showFullscreenImage) {
        Dialog(
            onDismissRequest = { showFullscreenImage = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.9f))
                    .clickable { showFullscreenImage = false },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(context)
                            .data("file:///android_asset/${presenter.selectedImage}")
                            .crossfade(true)
                            .build()
                    ),
                    contentDescription = "Preview",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(InstagramBlack)
            .statusBarsPadding()
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
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
                text = "New post",
                color = InstagramWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "Share",
                color = InstagramBlue,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clickable { onShare() }
                    .padding(horizontal = 12.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Image preview
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(context)
                        .data("file:///android_asset/${presenter.selectedImage}")
                        .crossfade(true)
                        .build()
                ),
                contentDescription = "Post image",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clickable { showFullscreenImage = true },
                contentScale = ContentScale.Crop
            )

            // Caption input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val user = presenter.currentUser
                UserAvatar(
                    username = user?.username ?: "",
                    avatarUrl = user?.avatarUrl ?: "",
                    size = 36.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                TextField(
                    value = presenter.caption,
                    onValueChange = { presenter.caption = it },
                    placeholder = {
                        Text("Write a caption...", color = InstagramTextGray, fontSize = 15.sp)
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = InstagramBlack,
                        unfocusedContainerColor = InstagramBlack,
                        focusedTextColor = InstagramWhite,
                        unfocusedTextColor = InstagramWhite,
                        cursorColor = InstagramBlue,
                        focusedIndicatorColor = InstagramBlack,
                        unfocusedIndicatorColor = InstagramBlack
                    ),
                    modifier = Modifier.weight(1f),
                    singleLine = false,
                    maxLines = 3
                )
            }

            HorizontalDivider(color = InstagramLightGray, thickness = 0.5.dp)

            // Menu items
            DraftMenuItem(
                icon = Icons.Outlined.Poll,
                label = "Poll",
                value = if (presenter.voteQuestion.isNotEmpty()) presenter.voteQuestion else null,
                onClick = { presenter.currentStep = NewPostStep.VOTE }
            )

            // Hashtag/Topic inline editing
            DraftMenuItem(
                icon = Icons.Outlined.Tag,
                label = "Topics",
                onClick = {}
            )
            if (presenter.hashtags.isNotEmpty()) {
                Text(
                    text = presenter.hashtags.joinToString(" ") { "#$it" },
                    color = InstagramBlue,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 56.dp, end = 56.dp, bottom = 4.dp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 56.dp, end = 56.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = hashtagInput,
                    onValueChange = { input ->
                        if (input.endsWith(" ") || input.endsWith("\n")) {
                            val tag = input.trim()
                            if (tag.isNotEmpty()) {
                                presenter.addHashtag(tag)
                            }
                            hashtagInput = ""
                        } else {
                            hashtagInput = input
                        }
                    },
                    placeholder = {
                        Text("Add a hashtag", color = InstagramTextGray, fontSize = 13.sp)
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = InstagramBlack,
                        unfocusedContainerColor = InstagramBlack,
                        focusedTextColor = InstagramWhite,
                        unfocusedTextColor = InstagramWhite,
                        cursorColor = InstagramBlue,
                        focusedIndicatorColor = InstagramLightGray,
                        unfocusedIndicatorColor = InstagramBlack
                    ),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            DraftMenuItem(
                icon = Icons.Outlined.MusicNote,
                label = "Add music",
                value = presenter.selectedMusic?.title,
                onClick = { presenter.currentStep = NewPostStep.MUSIC }
            )

            DraftMenuItem(
                icon = Icons.Outlined.LocationOn,
                label = "Add location",
                value = presenter.selectedLocation?.name,
                onClick = { presenter.currentStep = NewPostStep.LOCATION }
            )

            DraftMenuItem(
                icon = Icons.Outlined.People,
                label = "Audience",
                value = presenter.selectedAudience.label,
                onClick = { presenter.currentStep = NewPostStep.AUDIENCE }
            )

            DraftMenuItem(
                icon = Icons.Outlined.Share,
                label = "Also share to...",
                onClick = { presenter.currentStep = NewPostStep.SHARE_TO }
            )

            DraftMenuItem(
                icon = Icons.Outlined.MoreHoriz,
                label = "More options",
                onClick = { presenter.currentStep = NewPostStep.MORE_OPTIONS }
            )
        }
    }
}

@Composable
private fun DraftMenuItem(
    icon: ImageVector,
    label: String,
    value: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = InstagramWhite,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            color = InstagramWhite,
            fontSize = 15.sp,
            modifier = Modifier.weight(1f)
        )
        if (value != null) {
            Text(
                text = value,
                color = InstagramTextGray,
                fontSize = 14.sp,
                maxLines = 1
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = InstagramTextGray,
            modifier = Modifier.size(20.dp)
        )
    }
    HorizontalDivider(color = InstagramLightGray, thickness = 0.5.dp)
}
