package com.example.snapchatremix.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Settings surface for the SnapRemix prototype.
 *
 * For now this is a single-section screen: a list of switches for each bottom
 * nav destination so the user can remove destinations they don't use (e.g.
 * hide Spotlight) and add them back later. The remaining tabs automatically
 * rebalance to fill the bar — see [SnapRemixBottomBar].
 *
 * The screen is deliberately self-contained: it reads enabled/disabled state
 * and writes back through [BottomNavState] directly, so the host only needs
 * to pass the same state instance it gave to the bottom bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navState: BottomNavState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Close settings",
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
        ) {
            SectionHeader(text = "Bottom bar")
            HorizontalDivider()
            BottomNavItem.defaultOrder.forEach { item ->
                BottomBarDestinationToggle(
                    item = item,
                    enabled = navState.isEnabled(item),
                    onToggle = { navState.toggle(item, it) },
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewSettingsScreen() {
    SettingsScreen(
        navState = BottomNavState(),
        onBack = {},
    )
}
