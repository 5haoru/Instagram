package com.example.myinstagram.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myinstagram.presenter.EditProfilePresenter
import com.example.myinstagram.ui.components.UserAvatar
import com.example.myinstagram.ui.theme.*

@Composable
fun EditProfileScreen(
    presenter: EditProfilePresenter,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) { presenter.loadData() }

    val user = presenter.currentUser ?: return

    Column(
        modifier = modifier
            .fillMaxSize()
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
                    Icons.Filled.Close,
                    contentDescription = "Close",
                    tint = InstagramWhite
                )
            }
            Text(
                text = "Edit profile",
                color = InstagramWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
        }

        HorizontalDivider(color = InstagramLightGray, thickness = 0.5.dp)

        // Profile photo section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UserAvatar(
                username = user.username,
                size = 80.dp,
                avatarUrl = user.avatarUrl
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Edit picture or avatar",
                color = InstagramBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { }
            )
        }

        HorizontalDivider(color = InstagramLightGray, thickness = 0.5.dp)

        // Form fields
        EditProfileField(label = "Name", value = presenter.name, onValueChange = { presenter.name = it })
        EditProfileField(label = "Username", value = presenter.username, onValueChange = { presenter.username = it })
        EditProfileField(label = "Pronouns", value = presenter.pronouns, onValueChange = { presenter.pronouns = it })
        EditProfileField(label = "Bio", value = presenter.bio, onValueChange = { presenter.bio = it })
        EditProfileNavigationField(label = "Links", value = if (presenter.links.isEmpty()) "Add links" else presenter.links)
        EditProfileField(label = "Gender", value = presenter.gender, onValueChange = { presenter.gender = it })

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = InstagramLightGray, thickness = 0.5.dp)

        // Bottom options
        Text(
            text = "Switch to professional account",
            color = InstagramBlue,
            fontSize = 14.sp,
            modifier = Modifier
                .clickable { }
                .padding(horizontal = 16.dp, vertical = 14.dp)
        )
        Text(
            text = "Personal information settings",
            color = InstagramBlue,
            fontSize = 14.sp,
            modifier = Modifier
                .clickable { }
                .padding(horizontal = 16.dp, vertical = 14.dp)
        )
    }
}

@Composable
private fun EditProfileField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            color = InstagramTextGray,
            fontSize = 12.sp
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = InstagramBlack,
                focusedContainerColor = InstagramBlack,
                unfocusedIndicatorColor = InstagramLightGray,
                focusedIndicatorColor = InstagramBlue,
                cursorColor = InstagramBlue,
                focusedTextColor = InstagramWhite,
                unfocusedTextColor = InstagramWhite
            ),
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp),
            singleLine = true
        )
    }
}

@Composable
private fun EditProfileNavigationField(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = InstagramTextGray,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = InstagramTextGray,
                fontSize = 16.sp
            )
        }
        Icon(
            Icons.Filled.KeyboardArrowRight,
            contentDescription = "Navigate",
            tint = InstagramTextGray,
            modifier = Modifier.size(20.dp)
        )
    }
    HorizontalDivider(
        color = InstagramLightGray,
        thickness = 0.5.dp,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}
