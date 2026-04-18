package com.example.snapchatremix

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import com.example.snapchatremix.bookmarks.BookmarksScreen
import com.example.snapchatremix.chat.ChatScreen
import com.example.snapchatremix.navigation.BottomNavItem
import com.example.snapchatremix.navigation.BottomNavState
import com.example.snapchatremix.navigation.SettingsScreen
import com.example.snapchatremix.navigation.SnapRemixBottomBar
import com.example.snapchatremix.stories.StoriesScreen

/**
 * Root composable for the SnapRemix app.
 *
 * Composition of concerns:
 *  - [BottomNavState] drives which tab is selected and which tabs are
 *    visible at all (the user can hide destinations in Settings; the bar
 *    rebalances to fit what's left).
 *  - [showSettings] overlays the full [SettingsScreen] on top of the tab
 *    content when true. Kept here (not inside any single tab) so the
 *    Settings surface is reachable from multiple screens' top bars.
 */
@Composable
fun SnapRemixApp(
    isScreenshotActive: Boolean = false
) {
    val navState = remember {
        BottomNavState(initialSelectedId = BottomNavItem.Chat.id)
    }
    // Settings is a modal-style overlay rather than a tab. rememberSaveable
    // so rotation doesn't kick the user out of the Settings screen.
    var showSettings by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // Hide the bottom bar while Settings is open so Settings feels
            // like a full-screen modal instead of a peer tab.
            if (!showSettings) {
                SnapRemixBottomBar(
                    state = navState,
                    onItemSelected = { item ->
                        navState.select(item)
                    }
                )
            }
        }
    ) { innerPadding ->

        if (showSettings) {
            SettingsScreen(
                navState = navState,
                onBack = { showSettings = false },
                modifier = Modifier.padding(innerPadding),
            )
            return@Scaffold
        }

        when (navState.selectedId) {
            BottomNavItem.Map.id -> {
                PlaceholderScreen(
                    title = "Map Screen",
                    modifier = Modifier.padding(innerPadding)
                )
            }

            BottomNavItem.Chat.id -> {
                ChatScreen(
                    modifier = Modifier.padding(innerPadding),
                    onOpenSettings = { showSettings = true },
                )
            }

            BottomNavItem.Camera.id -> {
                PlaceholderScreen(
                    title = "Camera Screen",
                    modifier = Modifier.padding(innerPadding)
                )
            }

            BottomNavItem.Stories.id -> {
                StoriesScreen(
                    modifier = Modifier.padding(innerPadding),
                    onOpenSettings = { showSettings = true },
                )
            }

            BottomNavItem.Spotlight.id -> {
                PlaceholderScreen(
                    title = "Spotlight Screen",
                    modifier = Modifier.padding(innerPadding)
                )
            }

            BottomNavItem.Bookmarks.id -> {
                BookmarksScreen(
                    modifier = Modifier.padding(innerPadding),
                    onOpenSettings = { showSettings = true },
                    onOpenThread = { /* TODO: future deep-link to thread */ },
                )
            }
        }
    }
}

@Composable
fun PlaceholderScreen(
    title: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title)
    }
}
