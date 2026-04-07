package com.example.myinstagram.ui.post

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.PersonOff
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myinstagram.presenter.AudienceType
import com.example.myinstagram.presenter.NewPostPresenter
import com.example.myinstagram.ui.theme.*

@Composable
fun NewPostAudienceScreen(
    presenter: NewPostPresenter,
    onBack: () -> Unit
) {
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
                text = "Audience",
                color = InstagramWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onBack) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = "Done",
                    tint = InstagramBlue
                )
            }
        }

        HorizontalDivider(color = InstagramLightGray, thickness = 0.5.dp)

        Spacer(modifier = Modifier.height(8.dp))

        AudienceOption(
            icon = Icons.Outlined.Public,
            title = "Everyone",
            description = "Anyone can see this post",
            isSelected = presenter.selectedAudience == AudienceType.EVERYONE,
            onClick = { presenter.selectedAudience = AudienceType.EVERYONE }
        )

        AudienceOption(
            icon = Icons.Filled.Star,
            title = "Close Friends",
            description = "Only people on your close friends list can see this post",
            isSelected = presenter.selectedAudience == AudienceType.CLOSE_FRIENDS,
            onClick = { presenter.selectedAudience = AudienceType.CLOSE_FRIENDS }
        )

        AudienceOption(
            icon = Icons.Outlined.PersonOff,
            title = "Followers Except...",
            description = "All your followers can see this post except the ones you exclude",
            isSelected = presenter.selectedAudience == AudienceType.FOLLOWERS_EXCEPT,
            onClick = { presenter.selectedAudience = AudienceType.FOLLOWERS_EXCEPT }
        )
    }
}

@Composable
private fun AudienceOption(
    icon: ImageVector,
    title: String,
    description: String,
    isSelected: Boolean,
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
            contentDescription = title,
            tint = InstagramWhite,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = InstagramWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                color = InstagramTextGray,
                fontSize = 13.sp
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = InstagramBlue,
                unselectedColor = InstagramTextGray
            )
        )
    }
}
