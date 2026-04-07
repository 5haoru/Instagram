package com.example.myinstagram.ui.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myinstagram.model.Conversation
import com.example.myinstagram.presenter.MessagesPresenter
import com.example.myinstagram.ui.components.UserAvatar
import com.example.myinstagram.ui.theme.*

@Composable
fun MessagesScreen(presenter: MessagesPresenter, onOpenChat: (String) -> Unit = {}, modifier: Modifier = Modifier) {
    LaunchedEffect(Unit) { presenter.loadData() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(InstagramBlack)
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = presenter.currentUser?.username ?: "",
                    color = InstagramWhite,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Switch",
                    tint = InstagramWhite,
                    modifier = Modifier.size(24.dp)
                )
            }
            Icon(
                Icons.Filled.Edit,
                contentDescription = "New message",
                tint = InstagramWhite,
                modifier = Modifier.size(24.dp)
            )
        }

        // Search bar
        OutlinedTextField(
            value = "",
            onValueChange = { },
            placeholder = { Text("Search", color = InstagramTextGray) },
            leadingIcon = {
                Icon(Icons.Filled.Search, contentDescription = "Search", tint = InstagramTextGray)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = InstagramMediumGray,
                focusedContainerColor = InstagramMediumGray,
                unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                focusedBorderColor = InstagramBlue,
                cursorColor = InstagramWhite,
                focusedTextColor = InstagramWhite,
                unfocusedTextColor = InstagramWhite
            ),
            singleLine = true,
            readOnly = true
        )

        // Messages / Requests tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Messages",
                color = InstagramWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.width(24.dp))
            Text(
                text = "Requests",
                color = InstagramTextGray,
                fontSize = 16.sp
            )
        }

        // Conversation list
        LazyColumn {
            items(presenter.conversations) { conversation ->
                ConversationItem(
                    conversation = conversation,
                    presenter = presenter,
                    onOpenChat = onOpenChat
                )
            }
        }
    }
}

@Composable
private fun ConversationItem(
    conversation: Conversation,
    presenter: MessagesPresenter,
    onOpenChat: (String) -> Unit
) {
    val otherUser = presenter.getOtherParticipant(conversation)
    val lastMessage = presenter.getLastMessage(conversation)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenChat(conversation.conversationId) }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserAvatar(username = otherUser?.username ?: "", size = 52.dp, avatarUrl = otherUser?.avatarUrl ?: "")
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = otherUser?.username ?: "",
                color = if (conversation.unreadCount > 0) InstagramWhite
                else InstagramTextGray,
                fontWeight = if (conversation.unreadCount > 0) FontWeight.Bold
                else FontWeight.Normal,
                fontSize = 14.sp
            )
            Text(
                text = "${lastMessage?.text ?: ""} · ${lastMessage?.timestamp ?: ""}",
                color = InstagramTextGray,
                fontSize = 13.sp,
                maxLines = 1
            )
        }
        if (conversation.unreadCount > 0) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(InstagramBlue)
            )
        }
    }
}
