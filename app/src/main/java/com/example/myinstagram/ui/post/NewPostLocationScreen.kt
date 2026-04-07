package com.example.myinstagram.ui.post

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myinstagram.presenter.NewPostPresenter
import com.example.myinstagram.ui.theme.*

@Composable
fun NewPostLocationScreen(
    presenter: NewPostPresenter,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredLocations = remember(searchQuery, presenter.locationItems) {
        if (searchQuery.isBlank()) presenter.locationItems
        else presenter.locationItems.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.category.contains(searchQuery, ignoreCase = true)
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
                text = "Add location",
                color = InstagramWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

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
                .padding(horizontal = 16.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Location list
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(filteredLocations) { location ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            presenter.selectedLocation = location
                            onBack()
                        }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = InstagramWhite,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = location.name,
                            color = InstagramWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = location.category,
                            color = InstagramTextGray,
                            fontSize = 13.sp
                        )
                    }
                }
                HorizontalDivider(
                    color = InstagramLightGray,
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(start = 56.dp)
                )
            }
        }
    }
}
