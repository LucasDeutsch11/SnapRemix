package com.example.snapchatremix.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * The SnapRemix bottom navigation bar.
 *
 * This composable is the first custom SnapRemix feature: a bottom bar that
 * dynamically rebalances itself when the user disables a destination.
 *
 * Behavior:
 *  - Renders only [BottomNavState.visibleItems]. Disabled destinations are
 *    removed entirely — not greyed out — so the remaining items expand to
 *    fill the bar's width evenly.
 *  - Example: when Spotlight is disabled, the four remaining items each take
 *    1/4 of the bar instead of 1/5. This is automatic because Material3's
 *    [NavigationBar] lays its children out in a Row with equal weight.
 *
 * The bar is purely a view over [BottomNavState]; call [BottomNavState.toggle]
 * from settings UI to add or remove destinations.
 */
@Composable
fun SnapRemixBottomBar(
    state: BottomNavState,
    modifier: Modifier = Modifier,
    onItemSelected: (BottomNavItem) -> Unit = {},
) {
    NavigationBar(
        modifier = modifier.fillMaxWidth(),
    ) {
        // Only render the currently-visible items. Disabled destinations are
        // omitted so NavigationBar rebalances the remaining ones automatically.
        state.visibleItems.forEach { item ->
            NavigationBarItem(
                selected = state.selectedId == item.id,
                onClick = {
                    state.select(item)
                    onItemSelected(item)
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp),
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        textAlign = TextAlign.Center,
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(),
            )
        }
    }
}

/**
 * A settings row for the "manage your bottom bar" screen. Renders a labeled
 * switch for a single [BottomNavItem] so the user can add/remove it from the
 * bar. Lives here (rather than MainActivity) so a future settings screen can
 * compose several of these without touching the root activity.
 */
@Composable
fun BottomBarDestinationToggle(
    item: BottomNavItem,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
            ) {
                Text(text = item.label)
            }
            Switch(
                checked = enabled,
                onCheckedChange = onToggle,
            )
        }
    }
}

@Preview(showBackground = true, name = "All 5 tabs")
@Composable
private fun PreviewBottomBarAll() {
    val state = BottomNavState(initialSelectedId = BottomNavItem.Camera.id)
    SnapRemixBottomBar(state = state)
}

@Preview(showBackground = true, name = "Spotlight disabled (4 tabs)")
@Composable
private fun PreviewBottomBarNoSpotlight() {
    val state = BottomNavState(
        initialSelectedId = BottomNavItem.Camera.id,
        initialDisabledIds = setOf(BottomNavItem.Spotlight.id),
    )
    SnapRemixBottomBar(state = state)
}
