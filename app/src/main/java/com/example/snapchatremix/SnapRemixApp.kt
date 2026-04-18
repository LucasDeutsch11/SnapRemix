package com.example.snapchatremix

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import com.example.snapchatremix.chat.ChatScreen
import com.example.snapchatremix.navigation.BottomNavItem
import com.example.snapchatremix.navigation.BottomNavState
import com.example.snapchatremix.navigation.SnapRemixBottomBar

@Composable
fun SnapRemixApp(
    isScreenshotActive: Boolean = false
) {
    val navState = remember {
        BottomNavState(initialSelectedId = BottomNavItem.Chat.id)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            SnapRemixBottomBar(
                state = navState,
                onItemSelected = { item ->
                    navState.select(item)
                }
            )
        }
    ) { innerPadding ->

        when (navState.selectedId) {
            BottomNavItem.Map.id -> {
                PlaceholderScreen(
                    title = "Map Screen",
                    modifier = Modifier.padding(innerPadding)
                )
            }

            BottomNavItem.Chat.id -> {
                ChatScreen(
                    modifier = Modifier.padding(innerPadding)
                )
            }

            BottomNavItem.Camera.id -> {
                PlaceholderScreen(
                    title = "Camera Screen",
                    modifier = Modifier.padding(innerPadding)
                )
            }

            BottomNavItem.Stories.id -> {
                PlaceholderScreen(
                    title = "Stories Screen",
                    modifier = Modifier.padding(innerPadding)
                )
            }

            BottomNavItem.Spotlight.id -> {
                PlaceholderScreen(
                    title = "Spotlight Screen",
                    modifier = Modifier.padding(innerPadding)
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