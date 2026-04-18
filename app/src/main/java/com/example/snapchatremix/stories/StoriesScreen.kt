package com.example.snapchatremix.stories

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Stories feed. Horizontal row of friends' stories on top, then a 2-column
 * "Discover" grid below. All data is fake and lives in [StoryRepository] —
 * this is purely a visual demo.
 *
 * Implementation note: the outer container is a vertically scrollable
 * [Column] rather than a LazyColumn so we can drop in a LazyRow + a hand-
 * rolled 2-column grid without running into nested-lazy-container
 * constraints. The item count is tiny, so virtualization isn't needed.
 *
 * Tapping a card fires [onStoryOpened] — currently a stub. Hook up a full-
 * screen story player when one exists.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoriesScreen(
    modifier: Modifier = Modifier,
    onOpenSettings: () -> Unit = {},
    onStoryOpened: (Story) -> Unit = {},
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Stories") },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Open settings",
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
            SectionLabel(text = "Friends")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(StoryRepository.friends, key = { it.id }) { story ->
                    FriendStoryCard(
                        story = story,
                        onClick = { onStoryOpened(story) },
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            SectionLabel(text = "Discover")
            DiscoverGrid(
                stories = StoryRepository.discover,
                onStoryClick = onStoryOpened,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
    )
}

/**
 * Simple 2-column grid built from Rows. Fine for a small demo feed; swap
 * for a paging grid once this pulls from a real source.
 */
@Composable
private fun DiscoverGrid(
    stories: List<Story>,
    onStoryClick: (Story) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        stories.chunked(2).forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                rowItems.forEach { story ->
                    Box(modifier = Modifier.weight(1f)) {
                        DiscoverStoryCard(
                            story = story,
                            onClick = { onStoryClick(story) },
                        )
                    }
                }
                // Fill space if this row is a single orphan, so the lone
                // card doesn't stretch across the whole width.
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/**
 * Tall friend-story card with an avatar overlay. Gradient background stands
 * in for the story's actual media.
 */
@Composable
private fun FriendStoryCard(
    story: Story,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .width(112.dp)
            .height(180.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(story.gradientStart, story.gradientEnd),
                ),
            )
            .clickable(onClick = onClick),
    ) {
        // Top-left avatar with a "seen vs unseen" ring.
        val ringColor = if (story.viewed) {
            Color.White.copy(alpha = 0.45f)
        } else {
            Color.White
        }
        Box(
            modifier = Modifier
                .padding(8.dp)
                .size(36.dp)
                .border(width = 2.dp, color = ringColor, shape = CircleShape)
                .padding(3.dp)
                .background(color = story.avatarColor, shape = CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = story.authorInitials,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
            )
        }

        // Bottom-left author + headline.
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp),
        ) {
            Text(
                text = story.author,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = story.headline,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 11.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/**
 * Roughly square publisher card for the Discover row.
 */
@Composable
private fun DiscoverStoryCard(
    story: Story,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.9f)
            .clip(RoundedCornerShape(14.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(story.gradientStart, story.gradientEnd),
                ),
            )
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(10.dp),
        ) {
            Text(
                text = story.author.uppercase(),
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 10.sp,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = story.headline,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewStoriesScreen() {
    StoriesScreen()
}
