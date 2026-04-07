package com.example.myinstagram.ui.settings

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
import com.example.myinstagram.presenter.SettingsPresenter
import com.example.myinstagram.ui.theme.*

@Composable
fun AccountPrivacyScreen(
    presenter: SettingsPresenter,
    onBack: () -> Unit
) {
    LaunchedEffect(Unit) { presenter.loadData() }
    val isPrivate = presenter.currentUser?.isPrivate ?: false

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
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = InstagramWhite)
            }
            Text("Account privacy", color = InstagramWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        HorizontalDivider(color = InstagramLightGray, thickness = 0.5.dp)

        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Private account",
                        color = InstagramWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "When your account is private, only people you approve can see your photos and videos. Your existing followers won't be affected.",
                        color = InstagramTextGray,
                        fontSize = 13.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Switch(
                    checked = isPrivate,
                    onCheckedChange = { presenter.togglePrivacy() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = InstagramBlack,
                        checkedTrackColor = InstagramBlue,
                        uncheckedThumbColor = InstagramBlack,
                        uncheckedTrackColor = InstagramLightGray
                    )
                )
            }
        }
    }
}
