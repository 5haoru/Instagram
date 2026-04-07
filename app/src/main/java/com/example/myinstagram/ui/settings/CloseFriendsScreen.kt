package com.example.myinstagram.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myinstagram.presenter.SettingsPresenter
import com.example.myinstagram.ui.components.UserAvatar
import com.example.myinstagram.ui.theme.*

@Composable
fun CloseFriendsScreen(
    presenter: SettingsPresenter,
    onBack: () -> Unit
) {
    LaunchedEffect(Unit) { presenter.loadData() }
    var searchQuery by remember { mutableStateOf("") }

    val otherUsers = remember(presenter.allUsers) {
        presenter.getOtherUsers()
    }
    val filteredUsers = remember(searchQuery, otherUsers) {
        if (searchQuery.isBlank()) otherUsers
        else otherUsers.filter {
            it.username.contains(searchQuery, ignoreCase = true) ||
                    it.displayName.contains(searchQuery, ignoreCase = true)
        }
    }

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
            Text("Close friends", color = InstagramWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        // Description
        Text(
            text = "People you add to your close friends list will be able to see your close friends stories and posts.",
            color = InstagramTextGray,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // Search bar
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search", color = InstagramTextGray) },
            leadingIcon = {
                Icon(Icons.Filled.Search, contentDescription = null, tint = InstagramTextGray)
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = InstagramDarkGray,
                unfocusedContainerColor = InstagramDarkGray,
                focusedTextColor = InstagramWhite,
                unfocusedTextColor = InstagramWhite,
                cursorColor = InstagramBlue,
                focusedIndicatorColor = InstagramBlack,
                unfocusedIndicatorColor = InstagramBlack
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // User list
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(filteredUsers) { user ->
                val isCloseFriend = presenter.isCloseFriend(user.userId)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UserAvatar(
                        username = user.username,
                        size = 44.dp,
                        avatarUrl = user.avatarUrl
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(user.username, color = InstagramWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text(user.displayName, color = InstagramTextGray, fontSize = 13.sp)
                    }
                    Button(
                        onClick = { presenter.toggleCloseFriend(user.userId) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCloseFriend) InstagramMediumGray else InstagramBlue
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isCloseFriend) "Remove" else "Add",
                            color = if (isCloseFriend) InstagramWhite else androidx.compose.ui.graphics.Color.White,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
