package com.example.myinstagram.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Actual white for elements that should always be white (e.g. text on blue buttons)
val Color_White = Color(0xFFFFFFFF)
val Color_Black = Color(0xFF000000)

private val InstagramLightScheme = lightColorScheme(
    primary = InstagramBlue,
    onPrimary = Color_White,
    background = InstagramBlack,
    onBackground = InstagramWhite,
    surface = InstagramBlack,
    onSurface = InstagramWhite,
    surfaceVariant = InstagramDarkGray,
    onSurfaceVariant = InstagramTextGray,
    outline = InstagramLightGray,
    error = InstagramLikeRed
)

@Composable
fun MyInstagramTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = InstagramLightScheme,
        typography = Typography,
        content = content
    )
}
