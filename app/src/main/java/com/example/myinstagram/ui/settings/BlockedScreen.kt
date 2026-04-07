package com.example.myinstagram.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
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
fun BlockedScreen(
    presenter: SettingsPresenter,
    onBack: () -> Unit
) {
    LaunchedEffect(Unit) { presenter.loadData() }
    var showAddDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Add blocked user dialog
    if (showAddDialog) {
        BlockUserDialog(
            presenter = presenter,
            onDismiss = { showAddDialog = false }
        )
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
            Text(
                text = "Blocked",
                color = InstagramWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Block user", tint = InstagramWhite)
            }
        }

        HorizontalDivider(color = InstagramLightGray, thickness = 0.5.dp)

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
                .padding(horizontal = 16.dp, vertical = 8.dp),
            singleLine = true
        )

        val blockedUsers = presenter.getBlockedUsersList().let { list ->
            if (searchQuery.isBlank()) list
            else list.filter {
                it.username.contains(searchQuery, ignoreCase = true) ||
                        it.displayName.contains(searchQuery, ignoreCase = true)
            }
        }

        if (blockedUsers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Text("No blocked accounts", color = InstagramTextGray, fontSize = 14.sp)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(blockedUsers) { user ->
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
                            onClick = { presenter.toggleBlockUser(user.userId) },
                            colors = ButtonDefaults.buttonColors(containerColor = InstagramMediumGray),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                        ) {
                            Text("Unblock", color = InstagramWhite, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BlockUserDialog(
    presenter: SettingsPresenter,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val unblockedUsers = remember(presenter.currentUser) {
        presenter.getOtherUsers().filter { !presenter.isBlocked(it.userId) }
    }
    val filtered = remember(searchQuery, unblockedUsers) {
        if (searchQuery.isBlank()) unblockedUsers
        else unblockedUsers.filter {
            it.username.contains(searchQuery, ignoreCase = true) ||
                    it.displayName.contains(searchQuery, ignoreCase = true)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Block a user", color = InstagramWhite) },
        text = {
            Column(modifier = Modifier.heightIn(max = 400.dp)) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search user", color = InstagramTextGray) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = InstagramMediumGray,
                        unfocusedContainerColor = InstagramMediumGray,
                        focusedTextColor = InstagramWhite,
                        unfocusedTextColor = InstagramWhite,
                        cursorColor = InstagramBlue,
                        focusedIndicatorColor = InstagramBlack,
                        unfocusedIndicatorColor = InstagramBlack
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn {
                    items(filtered) { user ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            UserAvatar(username = user.username, size = 36.dp, avatarUrl = user.avatarUrl)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(user.username, color = InstagramWhite, fontSize = 14.sp, modifier = Modifier.weight(1f))
                            Button(
                                onClick = {
                                    presenter.toggleBlockUser(user.userId)
                                    onDismiss()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = InstagramLikeRed),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
                            ) {
                                Text("Block", color = androidx.compose.ui.graphics.Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = InstagramTextGray)
            }
        },
        containerColor = InstagramDarkGray
    )
}
