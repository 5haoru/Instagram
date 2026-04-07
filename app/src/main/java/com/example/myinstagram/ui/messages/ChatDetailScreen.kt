package com.example.myinstagram.ui.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myinstagram.model.Conversation
import com.example.myinstagram.model.Message
import com.example.myinstagram.presenter.MessagesPresenter
import com.example.myinstagram.ui.components.UserAvatar
import com.example.myinstagram.ui.theme.*

@Composable
fun ChatDetailScreen(
    conversationId: String,
    presenter: MessagesPresenter,
    onBack: () -> Unit
) {
    LaunchedEffect(conversationId) { presenter.loadData() }

    val conversation = presenter.conversations.find { it.conversationId == conversationId }
    val otherUser = conversation?.let { presenter.getOtherParticipant(it) }
    val currentUserId = presenter.currentUser?.userId ?: ""
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(conversation?.messages?.size) {
        listState.scrollToItem(0)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(InstagramBlack)
            .statusBarsPadding()
            .imePadding()
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
            UserAvatar(
                username = otherUser?.username ?: "",
                size = 32.dp,
                avatarUrl = otherUser?.avatarUrl ?: ""
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = otherUser?.displayName ?: "",
                    color = InstagramWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = otherUser?.username ?: "",
                    color = InstagramTextGray,
                    fontSize = 13.sp
                )
            }
        }

        HorizontalDivider(color = InstagramLightGray, thickness = 0.5.dp)

        // Messages list
        val messages = conversation?.messages ?: emptyList()
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            reverseLayout = true
        ) {
            items(messages.reversed()) { message ->
                MessageBubble(
                    message = message,
                    isCurrentUser = message.senderId == currentUserId
                )
            }
        }

        HorizontalDivider(color = InstagramLightGray, thickness = 0.5.dp)

        // Input bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                placeholder = {
                    Text("Message...", color = InstagramTextGray, fontSize = 14.sp)
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedBorderColor = InstagramLightGray,
                    focusedBorderColor = InstagramBlue,
                    cursorColor = InstagramWhite,
                    focusedTextColor = InstagramWhite,
                    unfocusedTextColor = InstagramWhite
                ),
                singleLine = true
            )
            if (messageText.isNotEmpty()) {
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = {
                    if (messageText.isNotBlank()) {
                        presenter.sendMessage(conversationId, messageText.trim())
                        messageText = ""
                    }
                }) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = InstagramBlue
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(
    message: Message,
    isCurrentUser: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(
                    if (isCurrentUser) InstagramBlue else InstagramMediumGray,
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isCurrentUser) 16.dp else 4.dp,
                        bottomEnd = if (isCurrentUser) 4.dp else 16.dp
                    )
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Column {
                Text(
                    text = message.text,
                    color = if (isCurrentUser) Color.White else InstagramWhite,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = message.timestamp,
                    color = if (isCurrentUser) Color.White.copy(alpha = 0.7f) else InstagramTextGray,
                    fontSize = 10.sp
                )
            }
        }
    }
}
