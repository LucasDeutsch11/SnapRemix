package com.example.snapchatremix.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

/**
 * Observable state holder for the SnapRemix bottom navigation bar.
 *
 * Tracks:
 *  - the currently selected destination ([selectedId])
 *  - the set of disabled item ids ([disabledIds]) — destinations the user
 *    has removed from their bar (e.g. Spotlight)
 *
 * The derived [visibleItems] list filters [BottomNavItem.defaultOrder] by the
 * disabled set so the UI can render only what the user wants and evenly
 * distribute the remaining items across the bar's width.
 *
 * Example: disabling [BottomNavItem.Spotlight] collapses the bar from 5 tabs
 * to 4, each of which expands to fill 1/4 of the bar instead of 1/5.
 */
@Stable
class BottomNavState(
    initialSelectedId: String = BottomNavItem.Camera.id,
    initialDisabledIds: Set<String> = emptySet(),
) {
    var selectedId: String by mutableStateOf(initialSelectedId)
        private set

    var disabledIds: Set<String> by mutableStateOf(initialDisabledIds)
        private set

    /**
     * The ordered list of items that should currently render in the bar.
     * Falls back to the full default list if every item has been disabled
     * (defensive — the UI should never end up with an empty bar).
     */
    val visibleItems: List<BottomNavItem>
        get() {
            val filtered = BottomNavItem.defaultOrder.filter { it.id !in disabledIds }
            return if (filtered.isEmpty()) BottomNavItem.defaultOrder else filtered
        }

    /** The currently selected item, or the first visible item as a fallback. */
    val selectedItem: BottomNavItem
        get() = visibleItems.firstOrNull { it.id == selectedId } ?: visibleItems.first()

    /** Select [item]. No-op if the item is currently disabled. */
    fun select(item: BottomNavItem) {
        if (item.id !in disabledIds) {
            selectedId = item.id
        }
    }

    /**
     * Disable [item] so it no longer appears in the bar. If the disabled
     * item was selected, selection jumps to the first remaining visible item
     * so the user never lands on a tab that isn't rendered.
     */
    fun disable(item: BottomNavItem) {
        if (item.id in disabledIds) return
        disabledIds = disabledIds + item.id
        if (selectedId == item.id) {
            selectedId = visibleItems.first().id
        }
    }

    /** Re-enable [item] so it appears in the bar again. */
    fun enable(item: BottomNavItem) {
        if (item.id !in disabledIds) return
        disabledIds = disabledIds - item.id
    }

    /** Convenience: flip the enabled/disabled state for [item]. */
    fun toggle(item: BottomNavItem) {
        if (item.id in disabledIds) enable(item) else disable(item)
    }

    companion object {
        /** Saver for [rememberSaveable] so state survives config changes. */
        val Saver: Saver<BottomNavState, *> = listSaver(
            save = { state ->
                listOf(state.selectedId, state.disabledIds.toList())
            },
            restore = { saved ->
                @Suppress("UNCHECKED_CAST")
                BottomNavState(
                    initialSelectedId = saved[0] as String,
                    initialDisabledIds = (saved[1] as List<String>).toSet(),
                )
            },
        )
    }
}

/**
 * Remembers a [BottomNavState] across recompositions and configuration
 * changes. Defaults to Camera selected and every item enabled.
 */
@Composable
fun rememberBottomNavState(
    initialSelectedId: String = BottomNavItem.Camera.id,
    initialDisabledIds: Set<String> = emptySet(),
): BottomNavState = rememberSaveable(saver = BottomNavState.Saver) {
    BottomNavState(
        initialSelectedId = initialSelectedId,
        initialDisabledIds = initialDisabledIds,
    )
}
