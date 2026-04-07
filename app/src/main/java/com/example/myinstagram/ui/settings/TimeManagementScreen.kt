package com.example.myinstagram.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myinstagram.ui.theme.*

@Composable
fun TimeManagementScreen(
    onBack: () -> Unit
) {
    var dailyLimit by remember { mutableStateOf(false) }
    var dailyLimitMinutes by remember { mutableStateOf(60f) }
    var sleepMode by remember { mutableStateOf(false) }

    // Mock usage data (minutes per day for the last 7 days)
    val weeklyUsage = remember { listOf(45, 62, 38, 71, 55, 48, 60) }
    val dayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val avgMinutes = weeklyUsage.average().toInt()

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
            Text("Time management", color = InstagramWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        HorizontalDivider(color = InstagramLightGray, thickness = 0.5.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Time spent section
            Text("Time spent", color = InstagramWhite, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Daily average: $avgMinutes min", color = InstagramTextGray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(16.dp))

            // Bar chart
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                weeklyUsage.forEachIndexed { index, minutes ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "${minutes}m",
                            color = InstagramTextGray,
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height((minutes * 1.2).dp)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(InstagramBlue)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = dayLabels[index],
                            color = InstagramTextGray,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider(color = InstagramLightGray, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // Daily time limit
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Set daily time limit", color = InstagramWhite, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Get a reminder when you've reached the time limit you set.",
                        color = InstagramTextGray,
                        fontSize = 13.sp
                    )
                }
                Switch(
                    checked = dailyLimit,
                    onCheckedChange = { dailyLimit = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = InstagramBlack,
                        checkedTrackColor = InstagramBlue,
                        uncheckedThumbColor = InstagramBlack,
                        uncheckedTrackColor = InstagramLightGray
                    )
                )
            }

            if (dailyLimit) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Limit: ${dailyLimitMinutes.toInt()} minutes",
                    color = InstagramWhite,
                    fontSize = 14.sp
                )
                Slider(
                    value = dailyLimitMinutes,
                    onValueChange = { dailyLimitMinutes = it },
                    valueRange = 15f..180f,
                    steps = 10,
                    colors = SliderDefaults.colors(
                        thumbColor = InstagramBlue,
                        activeTrackColor = InstagramBlue,
                        inactiveTrackColor = InstagramLightGray
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = InstagramLightGray, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // Sleep mode
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Sleep mode", color = InstagramWhite, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Mute notifications and set a bedtime reminder to help you wind down.",
                        color = InstagramTextGray,
                        fontSize = 13.sp
                    )
                }
                Switch(
                    checked = sleepMode,
                    onCheckedChange = { sleepMode = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = InstagramBlack,
                        checkedTrackColor = InstagramBlue,
                        uncheckedThumbColor = InstagramBlack,
                        uncheckedTrackColor = InstagramLightGray
                    )
                )
            }

            if (sleepMode) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Schedule: 11:00 PM - 7:00 AM", color = InstagramTextGray, fontSize = 14.sp)
            }
        }
    }
}
