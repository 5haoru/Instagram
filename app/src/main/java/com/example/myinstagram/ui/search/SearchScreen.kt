package com.example.myinstagram.ui.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.myinstagram.data.DataRepository
import com.example.myinstagram.model.Post
import com.example.myinstagram.model.Reel
import com.example.myinstagram.presenter.SearchPresenter
import com.example.myinstagram.ui.components.UserAvatar
import com.example.myinstagram.ui.theme.*

@Composable
fun SearchScreen(presenter: SearchPresenter, modifier: Modifier = Modifier) {
    LaunchedEffect(Unit) { presenter.loadData() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(InstagramBlack)
    ) {
        // Search Bar
        OutlinedTextField(
            value = presenter.searchQuery,
            onValueChange = { presenter.onSearchQueryChange(it) },
            placeholder = {
                Text("Search", color = InstagramTextGray, fontSize = 16.sp)
            },
            leadingIcon = {
                Icon(Icons.Filled.Search, contentDescription = "Search", tint = InstagramTextGray)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
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
            singleLine = true
        )

        if (presenter.isSearching) {
            // Search Results - users, posts and reels
            LazyColumn {
                item {
                    Text(
                        text = "Search results for \"${presenter.searchQuery}\"",
                        color = InstagramWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // Users section
                if (presenter.searchResults.isNotEmpty()) {
                    item {
                        Text(
                            text = "Users",
                            color = InstagramWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    items(presenter.searchResults) { user ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            UserAvatar(username = user.username, size = 44.dp, avatarUrl = user.avatarUrl)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = user.username,
                                    color = InstagramWhite,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = user.displayName,
                                    color = InstagramTextGray,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                // Posts section
                if (presenter.searchPostResults.isNotEmpty()) {
                    item {
                        Text(
                            text = "Posts",
                            color = InstagramWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    items(presenter.searchPostResults) { post ->
                        SearchPostItem(post)
                    }
                }

                // Reels section
                if (presenter.searchReelResults.isNotEmpty()) {
                    item {
                        Text(
                            text = "Reels",
                            color = InstagramWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    items(presenter.searchReelResults) { reel ->
                        SearchReelItem(reel)
                    }
                }

                // No results
                if (presenter.searchResults.isEmpty() &&
                    presenter.searchPostResults.isEmpty() &&
                    presenter.searchReelResults.isEmpty()
                ) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No results found for \"${presenter.searchQuery}\"",
                                color = InstagramTextGray,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        } else {
            // Explore Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(presenter.allPosts) { post ->
                    ExploreGridItem(post)
                }
                // Add extra placeholder items to fill the grid
                items(generatePlaceholderIds()) { id ->
                    val imageIndex = (kotlin.math.abs(id.hashCode()) % 10) + 1
                    val context = LocalContext.current
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(context)
                                .data("file:///android_asset/image/$imageIndex.jpeg")
                                .crossfade(true)
                                .build()
                        ),
                        contentDescription = "Explore image",
                        modifier = Modifier.aspectRatio(1f),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchPostItem(post: Post) {
    val context = LocalContext.current
    val user = DataRepository.getUserById(post.userId)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Post thumbnail
        if (post.imageUrl.isNotEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(context)
                        .data("file:///android_asset/${post.imageUrl}")
                        .crossfade(true)
                        .build()
                ),
                contentDescription = "Post",
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(InstagramMediumGray),
                contentAlignment = Alignment.Center
            ) {
                Text("\uD83D\uDCF7", fontSize = 20.sp)
            }
        }

        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user?.username ?: "unknown",
                color = InstagramWhite,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            if (post.caption.isNotEmpty()) {
                Text(
                    text = post.caption,
                    color = InstagramTextGray,
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = "${post.likedBy.size} likes",
                color = InstagramTextGray,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun SearchReelItem(reel: Reel) {
    val user = DataRepository.getUserById(reel.userId)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Reel thumbnail placeholder
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF1A1A2E)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.PlayArrow,
                contentDescription = "Reel",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = user?.username ?: "unknown",
                    color = InstagramWhite,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    Icons.Filled.VideoLibrary,
                    contentDescription = null,
                    tint = InstagramTextGray,
                    modifier = Modifier.size(14.dp)
                )
            }
            if (reel.caption.isNotEmpty()) {
                Text(
                    text = reel.caption,
                    color = InstagramTextGray,
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = "${reel.likedBy.size} likes · ${reel.comments.size} comments",
                color = InstagramTextGray,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun ExploreGridItem(post: Post) {
    if (post.imageUrl.isNotEmpty()) {
        val context = LocalContext.current
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(context)
                    .data("file:///android_asset/${post.imageUrl}")
                    .crossfade(true)
                    .build()
            ),
            contentDescription = "Explore image",
            modifier = Modifier.aspectRatio(1f),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .background(generateExploreColor(post.postId)),
            contentAlignment = Alignment.Center
        ) {
            Text("\uD83D\uDCF7", fontSize = 24.sp)
        }
    }
}

private fun generateExploreColor(id: String): Color {
    val colors = listOf(
        Color(0xFFE8D5C4), Color(0xFFD5C4B3), Color(0xFFC4D5E0),
        Color(0xFFB3C4D5), Color(0xFFC4B3D5), Color(0xFFD5E0C4),
        Color(0xFFE0D5C4), Color(0xFFC4D5C4), Color(0xFFD5C4D5)
    )
    return colors[kotlin.math.abs(id.hashCode()) % colors.size]
}

private fun generatePlaceholderIds(): List<String> {
    return (1..12).map { "explore_$it" }
}
