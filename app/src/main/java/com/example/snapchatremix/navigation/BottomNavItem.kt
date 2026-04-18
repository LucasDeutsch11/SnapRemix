package com.example.snapchatremix.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

// Note: icons here are drawn from `material-icons-core` only so no extra
// Gradle dependency (`material-icons-extended`) is required. Swap these for
// more accurate icons (Filled.Chat, Filled.PhotoCamera, Filled.PlayCircle)
// once the extended icons library is added to app/build.gradle.kts.

/**
 * Represents a single destination in the SnapRemix bottom navigation bar.
 *
 * Each item has a stable [id] used for routing / selection, a [label] shown
 * under the icon, and the [icon] drawn in the bar.
 *
 * The order of declaration in [BottomNavItem.defaultOrder] controls the
 * left-to-right rendering order in the bar.
 */
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
        // core placeholder — replace with Filled.Chat when extended icons are added.
        icon = Icons.Filled.Email,
    )

    object Camera : BottomNavItem(
        id = "camera",
        label = "Camera",
        // core placeholder — replace with Filled.PhotoCamera when extended icons are added.
        icon = Icons.Filled.Face,
    )

    object Stories : BottomNavItem(
        id = "stories",
        label = "Stories",
        // core placeholder — replace with Filled.PlayCircle when extended icons are added.
        icon = Icons.Filled.Star,
    )

    object Spotlight : BottomNavItem(
        id = "spotlight",
        label = "Spotlight",
        icon = Icons.Filled.PlayArrow,
    )

    companion object {
        /**
         * Canonical left-to-right order of destinations when every option
         * (including Spotlight) is enabled.
         */
        val defaultOrder: List<BottomNavItem> = listOf(
            Map,
            Chat,
            Camera,
            Stories,
            Spotlight,
        )

        /** Look up an item by its [id], or `null` if none match. */
        fun fromId(id: String): BottomNavItem? =
            defaultOrder.firstOrNull { it.id == id }
    }
}
