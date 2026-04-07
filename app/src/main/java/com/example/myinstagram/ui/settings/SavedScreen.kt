package com.example.myinstagram.ui.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
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
import com.example.myinstagram.presenter.SettingsPresenter
import com.example.myinstagram.ui.theme.*

@Composable
fun SavedScreen(
    presenter: SettingsPresenter,
    onBack: () -> Unit
) {
    LaunchedEffect(Unit) { presenter.loadData() }
    val context = LocalContext.current
    var showCreateDialog by remember { mutableStateOf(false) }
    var newCollectionName by remember { mutableStateOf("") }
    val collections = remember { mutableStateListOf("All Posts") }

    // Create collection dialog
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("New Collection", color = InstagramWhite) },
            text = {
                OutlinedTextField(
                    value = newCollectionName,
                    onValueChange = { newCollectionName = it },
                    placeholder = { Text("Collection name", color = InstagramTextGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = InstagramWhite,
                        unfocusedTextColor = InstagramWhite,
                        cursorColor = InstagramBlue,
                        focusedBorderColor = InstagramBlue,
                        unfocusedBorderColor = InstagramLightGray
                    ),
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newCollectionName.isNotBlank()) {
                        collections.add(newCollectionName.trim())
                        newCollectionName = ""
                        showCreateDialog = false
                    }
                }) {
                    Text("Create", color = InstagramBlue)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel", color = InstagramTextGray)
                }
            },
            containerColor = InstagramDarkGray
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
                text = "Saved",
                color = InstagramWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { showCreateDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "New collection", tint = InstagramWhite)
            }
        }

        HorizontalDivider(color = InstagramLightGray, thickness = 0.5.dp)

        // Collections list
        collections.forEach { collectionName ->
            val postsToShow = if (collectionName == "All Posts") presenter.savedPosts else emptyList()
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(
                    text = collectionName,
                    color = InstagramWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                if (postsToShow.isNotEmpty()) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier.heightIn(max = 400.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        items(postsToShow) { post ->
                            Image(
                                painter = rememberAsyncImagePainter(
                                    ImageRequest.Builder(context)
                                        .data("file:///android_asset/${post.imageUrl}")
                                        .crossfade(true)
                                        .build()
                                ),
                                contentDescription = null,
                                modifier = Modifier.aspectRatio(1f),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(InstagramDarkGray, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No saved items", color = InstagramTextGray, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
