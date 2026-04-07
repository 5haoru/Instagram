package com.example.myinstagram.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.myinstagram.ui.theme.InstagramLightGray
import com.example.myinstagram.ui.theme.InstagramStoryRing

@Composable
fun UserAvatar(
    username: String,
    size: Dp = 40.dp,
    showStoryRing: Boolean = false,
    avatarUrl: String = "",
    modifier: Modifier = Modifier
) {
    val ringModifier = if (showStoryRing) {
        Modifier.border(
            width = 2.dp,
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFF58529), InstagramStoryRing, Color(0xFF833AB4))
            ),
            shape = CircleShape
        )
    } else {
        Modifier.border(1.dp, InstagramLightGray, CircleShape)
    }

    Box(
        modifier = modifier
            .size(size + if (showStoryRing) 6.dp else 0.dp)
            .then(ringModifier),
        contentAlignment = Alignment.Center
    ) {
        if (avatarUrl.isNotEmpty()) {
            val context = LocalContext.current
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(context)
                        .data("file:///android_asset/$avatarUrl")
                        .crossfade(true)
                        .build()
                ),
                contentDescription = username,
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            val avatarColor = generateColorForUser(username)
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(avatarColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = username,
                    tint = Color.White,
                    modifier = Modifier.size(size * 0.6f)
                )
            }
        }
    }
}

private fun generateColorForUser(username: String): Color {
    val colors = listOf(
        Color(0xFFE91E63), Color(0xFF9C27B0), Color(0xFF673AB7),
        Color(0xFF3F51B5), Color(0xFF2196F3), Color(0xFF009688),
        Color(0xFF4CAF50), Color(0xFFFF9800), Color(0xFFFF5722),
        Color(0xFF795548)
    )
    return colors[kotlin.math.abs(username.hashCode()) % colors.size]
}
