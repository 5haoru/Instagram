package com.example.myinstagram.ui.notifications

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.myinstagram.presenter.NotificationItem
import com.example.myinstagram.presenter.NotificationsPresenter
import com.example.myinstagram.ui.components.UserAvatar
import com.example.myinstagram.ui.theme.*

@Composable
fun NotificationsScreen(
    presenter: NotificationsPresenter,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) { presenter.loadData() }

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
                text = "Notifications",
                color = InstagramWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        HorizontalDivider(color = InstagramLightGray, thickness = 0.5.dp)

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            // Follow requests section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Follow requests",
                        color = InstagramWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        Icons.Filled.KeyboardArrowRight,
                        contentDescription = "View",
                        tint = InstagramTextGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
                HorizontalDivider(color = InstagramLightGray, thickness = 0.5.dp)
            }

            // This month section
            val notifications = presenter.notifications
            if (notifications.isNotEmpty()) {
                item {
                    Text(
                        text = "This month",
                        color = InstagramWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }

                val recentNotifications = notifications.take(notifications.size / 2 + 1)
                items(recentNotifications) { notification ->
                    NotificationRow(notification, presenter)
                }

                // Earlier section
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Earlier",
                        color = InstagramWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }

                val earlierNotifications = notifications.drop(notifications.size / 2 + 1)
                items(earlierNotifications) { notification ->
                    NotificationRow(notification, presenter)
                }
            }
        }
    }
}

@Composable
private fun NotificationRow(
    notification: NotificationItem,
    presenter: NotificationsPresenter
) {
    val user = presenter.getUserById(notification.userId)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserAvatar(
            username = user?.username ?: "",
            size = 44.dp,
            avatarUrl = user?.avatarUrl ?: ""
        )
        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(user?.username ?: "")
                }
                append(" ")
                // Remove the username prefix from description to avoid duplication
                val descWithoutUsername = notification.description
                    .removePrefix("${user?.username ?: ""} ")
                append(descWithoutUsername)
                withStyle(SpanStyle(color = InstagramTextGray)) {
                    append(" ${notification.timestamp}")
                }
            },
            color = InstagramWhite,
            fontSize = 13.sp,
            modifier = Modifier.weight(1f),
            maxLines = 3
        )

        Spacer(modifier = Modifier.width(8.dp))

        if (notification.type == "follow") {
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = InstagramBlue),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Follow",
                    color = Color_White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        } else if (notification.postImageUrl.isNotEmpty()) {
            val context = LocalContext.current
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(context)
                        .data("file:///android_asset/${notification.postImageUrl}")
                        .crossfade(true)
                        .build()
                ),
                contentDescription = "Post",
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}
