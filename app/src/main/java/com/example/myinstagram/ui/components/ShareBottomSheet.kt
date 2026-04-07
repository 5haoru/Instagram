package com.example.myinstagram.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myinstagram.model.User
import com.example.myinstagram.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareBottomSheet(
    users: List<User>,
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
            // Search bar placeholder
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Search", color = InstagramTextGray, fontSize = 14.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = InstagramMediumGray,
                    focusedContainerColor = InstagramMediumGray,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = InstagramBlue,
                    cursorColor = InstagramWhite,
                    focusedTextColor = InstagramWhite,
                    unfocusedTextColor = InstagramWhite
                ),
                singleLine = true,
                readOnly = true
            )

            // User avatars row
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                items(users) { user ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(64.dp)
                    ) {
                        UserAvatar(
                            username = user.username,
                            size = 56.dp,
                            avatarUrl = user.avatarUrl
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = user.username,
                            color = InstagramWhite,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            HorizontalDivider(color = InstagramLightGray, thickness = 0.5.dp)

            // Share options grid
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ShareOption(icon = Icons.Filled.BookmarkBorder, label = "Add to story", onClick = onDismiss)
                ShareOption(icon = Icons.Filled.Link, label = "Copy link", onClick = onDismiss)
                ShareOption(icon = Icons.Filled.Share, label = "Share to...", onClick = onDismiss)
                ShareOption(icon = Icons.Filled.QrCode, label = "QR code", onClick = onDismiss)
            }
        }
    }
}

@Composable
private fun ShareOption(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp)
            .width(72.dp)
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
