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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.myinstagram.model.Post
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
            // Search Results
            LazyColumn {
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
