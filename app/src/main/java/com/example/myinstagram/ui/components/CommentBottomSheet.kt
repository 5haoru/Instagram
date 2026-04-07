package com.example.myinstagram.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myinstagram.model.Comment
import com.example.myinstagram.model.User
import com.example.myinstagram.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentBottomSheet(
    comments: List<Comment>,
    getUserById: (String) -> User?,
    currentUserId: String,
    onAddComment: (String) -> Unit,
    onToggleLikeComment: (String) -> Unit,
    isCommentLiked: (Comment) -> Boolean,
    onDismiss: () -> Unit
) {
    var commentText by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = InstagramDarkGray,
        dragHandle = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .width(40.dp)
                        .height(4.dp)
                        .background(InstagramTextGray, RoundedCornerShape(2.dp))
                )
                Text(
                    text = "Comments",
                    color = InstagramWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                HorizontalDivider(color = InstagramLightGray, thickness = 0.5.dp)
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 300.dp, max = 500.dp)
        ) {
            // Comments list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                if (comments.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No comments yet",
                                color = InstagramTextGray,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
                items(comments) { comment ->
                    val commentUser = getUserById(comment.userId)
                    val liked = isCommentLiked(comment)
                    CommentItem(
                        comment = comment,
                        commentUser = commentUser,
                        isLiked = liked,
                        onLikeClick = { onToggleLikeComment(comment.commentId) }
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
                val currentUser = getUserById(currentUserId)
                UserAvatar(
                    username = currentUser?.username ?: "",
                    size = 32.dp,
                    avatarUrl = currentUser?.avatarUrl ?: ""
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    placeholder = {
                        Text("Add a comment...", color = InstagramTextGray, fontSize = 14.sp)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
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
                if (commentText.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Post",
                        color = InstagramBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable {
                            if (commentText.isNotBlank()) {
                                onAddComment(commentText.trim())
                                commentText = ""
                            }
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun CommentItem(
    comment: Comment,
    commentUser: User?,
    isLiked: Boolean,
    onLikeClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        UserAvatar(
            username = commentUser?.username ?: "",
            size = 32.dp,
            avatarUrl = commentUser?.avatarUrl ?: ""
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                        append(commentUser?.username ?: "")
                    }
                    append(" ")
                    append(comment.text)
                },
                color = InstagramWhite,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = comment.timestamp,
                    color = InstagramTextGray,
                    fontSize = 11.sp
                )
                if (comment.likedBy.isNotEmpty()) {
                    Text(
                        text = "${comment.likedBy.size} likes",
                        color = InstagramTextGray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    text = "Reply",
                    color = InstagramTextGray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        Icon(
            if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = "Like comment",
            tint = if (isLiked) InstagramLikeRed else InstagramTextGray,
            modifier = Modifier
                .size(14.dp)
                .clickable { onLikeClick() }
                .align(Alignment.CenterVertically)
        )
    }
}
