package com.example.myinstagram.ui.post

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.myinstagram.presenter.NewPostPresenter
import com.example.myinstagram.ui.theme.*

@Composable
fun NewPostMusicScreen(
    presenter: NewPostPresenter,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    val filteredTracks = remember(searchQuery, presenter.musicTracks) {
        if (searchQuery.isBlank()) presenter.musicTracks
        else presenter.musicTracks.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.artist.contains(searchQuery, ignoreCase = true)
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
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = InstagramWhite
                )
            }
            Text(
                text = "Add music",
                color = InstagramWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Search bar
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search music", color = InstagramTextGray) },
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

        Spacer(modifier = Modifier.height(16.dp))

        // Recommended label
        Text(
            text = "Recommended",
            color = InstagramWhite,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // Music track list
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(filteredTracks) { track ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            presenter.selectedMusic = track
                            onBack()
                        }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Thumbnail
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(context)
                                .data("file:///android_asset/${track.thumbnailUrl}")
                                .crossfade(true)
                                .build()
                        ),
                        contentDescription = track.title,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = track.title,
                            color = InstagramWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                        Text(
                            text = track.artist,
                            color = InstagramTextGray,
                            fontSize = 13.sp,
                            maxLines = 1
                        )
                    }
                    Icon(
                        Icons.Filled.PlayCircle,
                        contentDescription = "Play",
                        tint = InstagramTextGray,
                        modifier = Modifier.size(32.dp)
                    )
                }
                HorizontalDivider(
                    color = InstagramLightGray,
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(start = 76.dp)
                )
            }
        }
    }
}
