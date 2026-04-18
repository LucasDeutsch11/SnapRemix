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
        // NOTE: this must be `by lazy`, not an eager `val`. `BottomNavItem` is a
        // sealed class whose own nested `object` singletons (Map, Chat, ...)
        // extend it. If we build this list during the companion's class
        // initializer, we can hit it mid-init — e.g. `BottomNavState`'s default
        // arg `BottomNavItem.Chat.id` starts Chat's <clinit>, which runs the
        // BottomNavItem super-constructor, which triggers the companion init,
        // which references `Chat` before Chat.INSTANCE has been assigned. The
        // list ends up with a null slot and later NPEs in `getVisibleItems`.
        // Deferring with `by lazy` guarantees every singleton is fully
        // constructed before the list is materialized.
        val defaultOrder: List<BottomNavItem> by lazy {
            listOf(
                Map,
                Chat,
                Camera,
                Stories,
                Spotlight
            )
        }

        fun fromId(id: String): BottomNavItem? =
            defaultOrder.firstOrNull { it.id == id }
    }
}