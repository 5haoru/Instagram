package com.example.myinstagram.ui.post

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun NewPostGalleryScreen(
    presenter: NewPostPresenter,
    onBack: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(InstagramBlack)
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
                text = "New post",
                color = InstagramWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
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

        // Selected image preview
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

        // Gallery label
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Gallery",
                color = InstagramWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Photo grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.fillMaxSize(),
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
    }
}
