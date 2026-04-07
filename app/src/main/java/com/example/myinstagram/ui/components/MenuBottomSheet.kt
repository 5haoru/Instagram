package com.example.myinstagram.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.HideSource
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myinstagram.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuBottomSheet(
    isFollowing: Boolean,
    onUnfollow: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = InstagramDarkGray,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .background(InstagramTextGray, RoundedCornerShape(2.dp))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            MenuItem(
                icon = Icons.Filled.Star,
                label = "Add to favorites",
                onClick = onDismiss
            )
            MenuItem(
                icon = Icons.Filled.VisibilityOff,
                label = "Not interested",
                onClick = onDismiss
            )
            if (isFollowing) {
                MenuItem(
                    icon = Icons.Filled.PersonRemove,
                    label = "Unfollow",
                    tint = InstagramLikeRed,
                    textColor = InstagramLikeRed,
                    onClick = {
                        onUnfollow()
                        onDismiss()
                    }
                )
            }
            MenuItem(
                icon = Icons.Filled.Info,
                label = "About this account",
                onClick = onDismiss
            )
            MenuItem(
                icon = Icons.Filled.HideSource,
                label = "Hide",
                onClick = onDismiss
            )
            MenuItem(
                icon = Icons.Filled.Flag,
                label = "Report",
                tint = InstagramLikeRed,
                textColor = InstagramLikeRed,
                onClick = onDismiss
            )
        }
    }
}

@Composable
private fun MenuItem(
    icon: ImageVector,
    label: String,
    tint: Color = InstagramWhite,
    textColor: Color = InstagramWhite,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            color = textColor,
            fontSize = 15.sp
        )
    }
}
