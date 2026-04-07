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
fun NewPostMoreOptionsScreen(
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
                text = "More options",
                color = InstagramWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        HorizontalDivider(color = InstagramLightGray, thickness = 0.5.dp)

        MoreOptionToggleItem(
            title = "Hide like and view counts",
            description = "Only you will be able to see the total number of likes and views on this post. You can change this later in the post menu.",
            checked = presenter.hideLikesAndViews,
            onCheckedChange = { presenter.hideLikesAndViews = it }
        )

        MoreOptionToggleItem(
            title = "Turn off commenting",
            description = "No one will be able to comment on this post.",
            checked = presenter.turnOffComments,
            onCheckedChange = { presenter.turnOffComments = it }
        )
    }
}

@Composable
private fun MoreOptionToggleItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = InstagramWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                color = InstagramTextGray,
                fontSize = 13.sp
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
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
