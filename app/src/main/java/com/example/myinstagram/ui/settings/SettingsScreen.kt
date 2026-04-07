package com.example.myinstagram.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myinstagram.presenter.SettingsPresenter
import com.example.myinstagram.ui.theme.*

@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {
    var currentPage by remember { mutableStateOf("main") }
    val settingsPresenter = remember { SettingsPresenter() }

    when (currentPage) {
        "main" -> SettingsMainPage(
            onBack = onBack,
            onNavigate = { currentPage = it }
        )
        "Saved" -> SavedScreen(
            presenter = settingsPresenter,
            onBack = { currentPage = "main" }
        )
        "Time management" -> TimeManagementScreen(
            onBack = { currentPage = "main" }
        )
        "Account privacy" -> AccountPrivacyScreen(
            presenter = settingsPresenter,
            onBack = { currentPage = "main" }
        )
        "Close friends" -> CloseFriendsScreen(
            presenter = settingsPresenter,
            onBack = { currentPage = "main" }
        )
        "Blocked" -> BlockedScreen(
            presenter = settingsPresenter,
            onBack = { currentPage = "main" }
        )
        else -> SettingsSubPage(
            title = currentPage,
            onBack = { currentPage = "main" }
        )
    }
}

@Composable
private fun SettingsMainPage(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit
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
                text = "Settings and activity",
                color = InstagramWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = {}) {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = "Search",
                    tint = InstagramWhite
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Your account
            SettingsSection(title = "Your account") {
                SettingsItem(
                    icon = Icons.Outlined.Person,
                    label = "Accounts Center",
                    subtitle = "Password, security, personal details, ad preferences",
                    onClick = { onNavigate("Accounts Center") }
                )
            }

            // How you use Instagram
            SettingsSection(title = "How you use Instagram") {
                SettingsItem(
                    icon = Icons.Outlined.BookmarkBorder,
                    label = "Saved",
                    onClick = { onNavigate("Saved") }
                )
                SettingsItem(
                    icon = Icons.Outlined.Archive,
                    label = "Archive",
                    onClick = { onNavigate("Archive") }
                )
                SettingsItem(
                    icon = Icons.Outlined.AccessTime,
                    label = "Your activity",
                    onClick = { onNavigate("Your activity") }
                )
                SettingsItem(
                    icon = Icons.Outlined.Notifications,
                    label = "Notifications",
                    onClick = { onNavigate("Notifications") }
                )
                SettingsItem(
                    icon = Icons.Outlined.Timer,
                    label = "Time management",
                    onClick = { onNavigate("Time management") }
                )
            }

            // Who can see your content
            SettingsSection(title = "Who can see your content") {
                SettingsItem(
                    icon = Icons.Outlined.Lock,
                    label = "Account privacy",
                    onClick = { onNavigate("Account privacy") }
                )
                SettingsItem(
                    icon = Icons.Outlined.StarBorder,
                    label = "Close friends",
                    onClick = { onNavigate("Close friends") }
                )
                SettingsItem(
                    icon = Icons.Outlined.Block,
                    label = "Blocked",
                    onClick = { onNavigate("Blocked") }
                )
                SettingsItem(
                    icon = Icons.Outlined.VisibilityOff,
                    label = "Hide story and live",
                    onClick = { onNavigate("Hide story and live") }
                )
            }

            // How others can interact with you
            SettingsSection(title = "How others can interact with you") {
                SettingsItem(
                    icon = Icons.Outlined.ChatBubbleOutline,
                    label = "Messages",
                    onClick = { onNavigate("Messages") }
                )
                SettingsItem(
                    icon = Icons.Outlined.AlternateEmail,
                    label = "Tags and mentions",
                    onClick = { onNavigate("Tags and mentions") }
                )
                SettingsItem(
                    icon = Icons.Outlined.ModeComment,
                    label = "Comments",
                    onClick = { onNavigate("Comments") }
                )
                SettingsItem(
                    icon = Icons.Outlined.Share,
                    label = "Sharing",
                    onClick = { onNavigate("Sharing") }
                )
                SettingsItem(
                    icon = Icons.Outlined.Warning,
                    label = "Restricted",
                    onClick = { onNavigate("Restricted") }
                )
                SettingsItem(
                    icon = Icons.Outlined.Shield,
                    label = "Limited interactions",
                    onClick = { onNavigate("Limited interactions") }
                )
                SettingsItem(
                    icon = Icons.Outlined.TextFields,
                    label = "Hidden words",
                    onClick = { onNavigate("Hidden words") }
                )
            }

            // What you see
            SettingsSection(title = "What you see") {
                SettingsItem(
                    icon = Icons.Outlined.Favorite,
                    label = "Favorites",
                    onClick = { onNavigate("Favorites") }
                )
                SettingsItem(
                    icon = Icons.Outlined.VolumeOff,
                    label = "Muted accounts",
                    onClick = { onNavigate("Muted accounts") }
                )
                SettingsItem(
                    icon = Icons.Outlined.ContentCut,
                    label = "Suggested content",
                    onClick = { onNavigate("Suggested content") }
                )
                SettingsItem(
                    icon = Icons.Outlined.ThumbDown,
                    label = "Like and share counts",
                    onClick = { onNavigate("Like and share counts") }
                )
            }

            // Your app and media
            SettingsSection(title = "Your app and media") {
                SettingsItem(
                    icon = Icons.Outlined.Devices,
                    label = "Device permissions",
                    onClick = { onNavigate("Device permissions") }
                )
                SettingsItem(
                    icon = Icons.Outlined.Translate,
                    label = "Language",
                    onClick = { onNavigate("Language") }
                )
                SettingsItem(
                    icon = Icons.Outlined.DataUsage,
                    label = "Data usage and media quality",
                    onClick = { onNavigate("Data usage and media quality") }
                )
                SettingsItem(
                    icon = Icons.Outlined.Web,
                    label = "Website permissions",
                    onClick = { onNavigate("Website permissions") }
                )
            }

            // For professionals
            SettingsSection(title = "For professionals") {
                SettingsItem(
                    icon = Icons.Outlined.Business,
                    label = "Account type and tools",
                    onClick = { onNavigate("Account type and tools") }
                )
            }

            // More info and support
            SettingsSection(title = "More info and support") {
                SettingsItem(
                    icon = Icons.Outlined.HelpOutline,
                    label = "Help",
                    onClick = { onNavigate("Help") }
                )
                SettingsItem(
                    icon = Icons.Outlined.Info,
                    label = "About",
                    onClick = { onNavigate("About") }
                )
            }

            // Login section
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Log in",
                color = InstagramBlue,
                fontSize = 15.sp,
                modifier = Modifier
                    .clickable { }
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            )
            Text(
                text = "Log out",
                color = InstagramLikeRed,
                fontSize = 15.sp,
                modifier = Modifier
                    .clickable { }
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            color = InstagramTextGray,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 8.dp)
        )
        content()
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    label: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = InstagramWhite,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = InstagramWhite,
                fontSize = 15.sp
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = InstagramTextGray,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }
        }
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = InstagramTextGray,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SettingsSubPage(
    title: String,
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
                text = title,
                color = InstagramWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        HorizontalDivider(color = InstagramLightGray, thickness = 0.5.dp)

        // Placeholder content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = "This feature is coming soon.",
                color = InstagramTextGray,
                fontSize = 14.sp
            )
        }
    }
}
