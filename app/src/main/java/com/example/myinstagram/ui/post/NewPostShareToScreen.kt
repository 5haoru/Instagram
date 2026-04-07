package com.example.myinstagram.ui.post

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myinstagram.presenter.NewPostPresenter
import com.example.myinstagram.ui.theme.*

@Composable
fun NewPostShareToScreen(
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
                text = "Also share to...",
                color = InstagramWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        HorizontalDivider(color = InstagramLightGray, thickness = 0.5.dp)

        ShareToggleItem(
            label = "Facebook",
            checked = presenter.shareToFacebook,
            onCheckedChange = { presenter.shareToFacebook = it }
        )

        ShareToggleItem(
            label = "Threads",
            checked = presenter.shareToThreads,
            onCheckedChange = { presenter.shareToThreads = it }
        )

        ShareToggleItem(
            label = "X",
            checked = presenter.shareToX,
            onCheckedChange = { presenter.shareToX = it }
        )
    }
}

@Composable
private fun ShareToggleItem(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = InstagramWhite,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = InstagramBlack,
                checkedTrackColor = InstagramBlue,
                uncheckedThumbColor = InstagramBlack,
                uncheckedTrackColor = InstagramLightGray
            )
        )
    }
    HorizontalDivider(
        color = InstagramLightGray,
        thickness = 0.5.dp,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}
