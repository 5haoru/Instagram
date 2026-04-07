package com.example.myinstagram.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myinstagram.presenter.ProfilePresenter
import com.example.myinstagram.ui.components.UserAvatar
import com.example.myinstagram.ui.theme.*

@Composable
fun ShareProfileScreen(
    presenter: ProfilePresenter,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val user = presenter.currentUser ?: return

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(InstagramBlack)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
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
            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // QR Code card
        Box(
            modifier = Modifier
                .size(280.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(InstagramDarkGray)
                .border(1.dp, InstagramLightGray, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                UserAvatar(
                    username = user.username,
                    size = 64.dp,
                    avatarUrl = user.avatarUrl
                )
                Spacer(modifier = Modifier.height(12.dp))

                // QR code placeholder
                Icon(
                    Icons.Filled.QrCode2,
                    contentDescription = "QR Code",
                    tint = InstagramWhite,
                    modifier = Modifier.size(120.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = user.username,
                    color = InstagramWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Share options
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 48.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ShareProfileOption(
                icon = Icons.Filled.Share,
                label = "Share profile"
            )
            ShareProfileOption(
                icon = Icons.Filled.QrCode2,
                label = "QR code"
            )
            ShareProfileOption(
                icon = Icons.Filled.ContentCopy,
                label = "Copy link"
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Profile link text
        Text(
            text = "instagram.com/${user.username}",
            color = InstagramTextGray,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ShareProfileOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .background(InstagramMediumGray, RoundedCornerShape(50)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = InstagramWhite,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            color = InstagramWhite,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            maxLines = 2,
            lineHeight = 14.sp
        )
    }
}
