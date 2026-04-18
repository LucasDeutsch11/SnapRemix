package com.example.snapchatremix.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class BottomNavState(
    initialSelectedId: String = BottomNavItem.Chat.id,
    initialDisabledIds: Set<String> = emptySet(),
) {
    private var disabledIds by mutableStateOf(initialDisabledIds)

    var selectedId by mutableStateOf(initialSelectedId)
        private set

    val visibleItems: List<BottomNavItem>
        get() = BottomNavItem.defaultOrder.filterNot { item ->
            item.id in disabledIds
        }

    fun select(item: BottomNavItem) {
        selectedId = item.id
    }

    fun toggle(item: BottomNavItem, enabled: Boolean) {
        // Guard: never let the user disable the very last visible tab. An
        // empty bottom bar traps them with nothing to select.
        if (!enabled && visibleItems.size <= 1 && item.id !in disabledIds) return

        disabledIds = if (enabled) {
            disabledIds - item.id
        } else {
            disabledIds + item.id
        }

        if (selectedId == item.id && item.id in disabledIds) {
            selectedId = visibleItems.firstOrNull()?.id ?: BottomNavItem.Chat.id
        }
    }

    fun isEnabled(item: BottomNavItem): Boolean {
        return item.id !in disabledIds
    }
}