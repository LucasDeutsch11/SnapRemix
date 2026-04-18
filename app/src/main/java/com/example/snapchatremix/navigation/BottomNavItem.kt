package com.example.snapchatremix.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val id: String,
    val label: String,
    val icon: ImageVector,
) {
    object Map : BottomNavItem(
        id = "map",
        label = "Map",
        icon = Icons.Filled.Place,
    )

    object Chat : BottomNavItem(
        id = "chat",
        label = "Chat",
        icon = Icons.Filled.Email,
    )

    object Camera : BottomNavItem(
        id = "camera",
        label = "Camera",
        icon = Icons.Filled.Face,
    )

    object Stories : BottomNavItem(
        id = "stories",
        label = "Stories",
        icon = Icons.Filled.Star,
    )

    object Spotlight : BottomNavItem(
        id = "spotlight",
        label = "Spotlight",
        icon = Icons.Filled.PlayArrow,
    )

    companion object {
        val defaultOrder: List<BottomNavItem> = listOf(
            Map,
            Chat,
            Camera,
            Stories,
            Spotlight
        )

        fun fromId(id: String): BottomNavItem? =
            defaultOrder.firstOrNull { it.id == id }
    }
}