package com.example.myinstagram.ui.post

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
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
fun NewPostVoteScreen(
    presenter: NewPostPresenter,
    onBack: () -> Unit
) {
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
                text = "Poll",
                color = InstagramWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onBack) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = "Done",
                    tint = InstagramBlue
                )
            }
        }

        HorizontalDivider(color = InstagramLightGray, thickness = 0.5.dp)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Question field
            Text(
                text = "Ask a question",
                color = InstagramTextGray,
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            TextField(
                value = presenter.voteQuestion,
                onValueChange = { presenter.voteQuestion = it },
                placeholder = { Text("Ask a question", color = InstagramTextGray) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = InstagramDarkGray,
                    unfocusedContainerColor = InstagramDarkGray,
                    focusedTextColor = InstagramWhite,
                    unfocusedTextColor = InstagramWhite,
                    cursorColor = InstagramBlue,
                    focusedIndicatorColor = InstagramBlue,
                    unfocusedIndicatorColor = InstagramLightGray
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Option fields
            presenter.voteOptions.forEachIndexed { index, option ->
                Text(
                    text = "Option ${index + 1}",
                    color = InstagramTextGray,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 4.dp, top = if (index > 0) 12.dp else 0.dp)
                )
                TextField(
                    value = option,
                    onValueChange = { presenter.updateVoteOption(index, it) },
                    placeholder = { Text("Option ${index + 1}", color = InstagramTextGray) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = InstagramDarkGray,
                        unfocusedContainerColor = InstagramDarkGray,
                        focusedTextColor = InstagramWhite,
                        unfocusedTextColor = InstagramWhite,
                        cursorColor = InstagramBlue,
                        focusedIndicatorColor = InstagramBlue,
                        unfocusedIndicatorColor = InstagramLightGray
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            // Add option button
            if (presenter.voteOptions.size < 4) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .clickable { presenter.addVoteOption() }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Add option",
                        tint = InstagramBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add option",
                        color = InstagramBlue,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
