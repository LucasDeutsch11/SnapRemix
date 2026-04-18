package com.example.snapchatremix.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Chat list screen: top search field + scrollable list of conversations.
 *
 * The search field filters the visible list on every keystroke via
 * [Conversation.matches], matching the user's typed text against the
 * friend's name and the last-message preview. Clearing the field restores
 * the full list.
 *
 * Tapping a row invokes [onOpenChat] with the selected conversation. Hosting
 * composables are expected to push a [ChatThreadScreen] in response — this
 * screen itself is purely presentational.
 */
@Composable
fun ChatListScreen(
    conversations: List<Conversation>,
    onOpenChat: (Conversation) -> Unit,
    modifier: Modifier = Modifier,
) {
    var query by remember { mutableStateOf("") }
    val filtered = remember(conversations, query) {
        conversations.filter { it.matches(query) }
    }

    Column(modifier = modifier.fillMaxSize()) {
        ChatSearchBar(
            query = query,
            onQueryChange = { query = it },
        )
        HorizontalDivider()
        if (filtered.isEmpty()) {
            EmptyResultsMessage(query = query)
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filtered, key = { it.friend.id }) { convo ->
                    ConversationRow(
                        conversation = convo,
                        onClick = { onOpenChat(convo) },
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

/**
 * The search field shown at the top of the chat list. Uses a leading search
 * icon and a trailing "clear" icon that appears only while the query is
 * non-empty.
 */
@Composable
private fun ChatSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        singleLine = true,
        placeholder = { Text("Search chats") },
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Search, contentDescription = "Search")
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = "Clear search",
                    )
                }
            }
        },
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
    )
}

/**
 * A single row in the chat list. Shows avatar, name, last-message preview,
 * and a timestamp. Unread rows render the name and preview in bold with a
 * small dot to the right of the timestamp.
 */
@Composable
private fun ConversationRow(
    conversation: Conversation,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FriendAvatar(friend = conversation.friend)
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = conversation.friend.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (conversation.unread) FontWeight.Bold else FontWeight.Normal,
            )
            Text(
                text = conversation.lastMessagePreview,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = if (conversation.unread) FontWeight.SemiBold else FontWeight.Normal,
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = conversation.timestamp,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (conversation.unread) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape,
                        ),
                )
            }
        }
    }
}

/**
 * Colored circle with the friend's initials — stand-in for a profile photo.
 */
@Composable
private fun FriendAvatar(
    friend: Friend,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .background(color = friend.avatarColor, shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = friend.initials,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
        )
    }
}

/** Shown in place of the list when the search query matches nothing. */
@Composable
private fun EmptyResultsMessage(query: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (query.isBlank()) {
                "No chats yet — snap a friend to start one."
            } else {
                "No chats match \"$query\""
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewChatListScreen() {
    ChatListScreen(
        conversations = ChatRepository.seedConversations,
        onOpenChat = {},
    )
}
