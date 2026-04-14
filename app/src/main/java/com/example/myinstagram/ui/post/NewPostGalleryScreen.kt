package com.example.myinstagram.ui.post

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.myinstagram.presenter.CreateMode
import com.example.myinstagram.presenter.NewPostPresenter
import com.example.myinstagram.ui.theme.*

@Composable
fun NewPostGalleryScreen(
    presenter: NewPostPresenter,
    onBack: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(InstagramBlack)
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                    text = when (presenter.createMode) {
                        CreateMode.POST -> "New post"
                        CreateMode.REEL -> "New reel"
                        CreateMode.LIVE -> "Live"
                    },
                    color = InstagramWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                if (presenter.createMode != CreateMode.LIVE) {
                    Text(
                        text = "Next",
                        color = InstagramBlue,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clickable { onNext() }
                            .padding(horizontal = 8.dp)
                    )
                }
            }

            // Preview area
            if (presenter.createMode == CreateMode.POST) {
                // Image preview
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(context)
                            .data("file:///android_asset/${presenter.selectedImage}")
                            .crossfade(true)
                            .build()
                    ),
                    contentDescription = "Selected photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    contentScale = ContentScale.Crop
                )
            } else if (presenter.createMode == CreateMode.REEL) {
                // Video preview placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .background(Color(0xFF1A1A2E)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.PlayArrow,
                            contentDescription = "Video preview",
                            tint = Color.White,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = presenter.selectedVideo,
                            color = InstagramTextGray,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                // Live placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .background(Color(0xFF1A1A2E)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.Videocam,
                            contentDescription = "Live",
                            tint = InstagramLikeRed,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Live is not available in simulator",
                            color = InstagramTextGray,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Gallery / Video label
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (presenter.createMode == CreateMode.REEL) "Videos" else "Gallery",
                    color = InstagramWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Grid
            if (presenter.createMode == CreateMode.POST) {
                // Photo grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    itemsIndexed(presenter.availableImages) { index, imageUrl ->
                        val isSelected = index == presenter.selectedImageIndex
                        Box {
                            Image(
                                painter = rememberAsyncImagePainter(
                                    ImageRequest.Builder(context)
                                        .data("file:///android_asset/$imageUrl")
                                        .crossfade(true)
                                        .build()
                                ),
                                contentDescription = "Photo $index",
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .then(
                                        if (isSelected) Modifier.border(3.dp, InstagramBlue)
                                        else Modifier
                                    )
                                    .clickable { presenter.selectImage(index) },
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            } else if (presenter.createMode == CreateMode.REEL) {
                // Video grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    itemsIndexed(presenter.availableVideos) { index, _ ->
                        val isSelected = index == presenter.selectedVideoIndex
                        Box(
                            modifier = Modifier
                                .aspectRatio(3f / 4f)
                                .then(
                                    if (isSelected) Modifier.border(3.dp, InstagramBlue)
                                    else Modifier
                                )
                                .background(Color(0xFF1A1A2E))
                                .clickable { presenter.selectVideo(index) },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Filled.PlayArrow,
                                    contentDescription = "Video $index",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Video ${index + 1}",
                                    color = InstagramTextGray,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom mode switcher - floating at bottom right
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(InstagramMediumGray.copy(alpha = 0.95f))
                .padding(horizontal = 4.dp, vertical = 4.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CreateMode.entries.forEach { mode ->
                val isSelected = presenter.createMode == mode
                Text(
                    text = mode.label,
                    color = if (isSelected) InstagramWhite else InstagramTextGray,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .then(
                            if (isSelected) Modifier.background(InstagramLightGray)
                            else Modifier
                        )
                        .clickable { presenter.createMode = mode }
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }
        }
    }
}
