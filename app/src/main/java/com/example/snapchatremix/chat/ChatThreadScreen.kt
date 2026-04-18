package com.example.snapchatremix.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Thread view for an individual chat. Shows the message history scrolled to
 * the bottom, with a top bar (friend's name + back button) and an input row
 * for composing new messages.
 *
 * Messages sent through this screen are appended to local state only — the
 * prototype has no backend. Hook up a repository write here when ready.
 *
 * [onBack] is called when the user taps the back arrow. The host composable
 * should pop back to the list view.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatThreadScreen(
    conversation: Conversation,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Local mutable copy of the message list so typing "send" visibly adds a
    // bubble. Seeded from the conversation, keyed by friend id so switching
    // threads resets correctly.
    val messages = remember(conversation.friend.id) {
        mutableStateListOf<ChatMessage>().apply { addAll(conversation.messages) }
    }
    var draft by remember(conversation.friend.id) { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto-scroll to the latest message whenever the list grows.
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FriendAvatarSmall(friend = conversation.friend)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = conversation.friend.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to chat list",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(),
            )
        },
        bottomBar = {
            MessageComposer(
                draft = draft,
                onDraftChange = { draft = it },
                onSend = {
                    val trimmed = draft.trim()
                    if (trimmed.isNotEmpty()) {
                        messages.add(
                            ChatMessage(
                                id = "local-${messages.size}",
                                fromMe = true,
                                text = trimmed,
                                timestamp = "now",
                            ),
                        )
                        draft = ""
                    }
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
        ) {
            items(messages, key = { it.id }) { msg ->
                MessageBubble(message = msg, friend = conversation.friend)
            }
        }
    }
}

/**
 * The bottom input row: text field + send button. Send is enabled only
 * while the draft has non-blank content to avoid empty messages.
 */
@Composable
private fun MessageComposer(
    draft: String,
    onDraftChange: (String) -> Unit,
    onSend: () -> Unit,
) {
    Surface(tonalElevation = 2.dp) {
        Column {
            HorizontalDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = draft,
                    onValueChange = onDraftChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Send a chat") },
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 4,
                )
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(
                    onClick = onSend,
                    enabled = draft.isNotBlank(),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (draft.isNotBlank()) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                }
            }
        }
    }
}

/**
 * A single message bubble. Right-aligned and tinted primary for messages
 * you sent; left-aligned and surface-variant for messages from the friend.
 */
@Composable
private fun MessageBubble(
    message: ChatMessage,
    friend: Friend,
    modifier: Modifier = Modifier,
) {
    val arrangement = if (message.fromMe) Arrangement.End else Arrangement.Start
    val bubbleColor = if (message.fromMe) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = if (message.fromMe) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = arrangement,
    ) {
        if (!message.fromMe) {
            FriendAvatarSmall(friend = friend)
            Spacer(modifier = Modifier.width(8.dp))
        }
        Column(
            horizontalAlignment = if (message.fromMe) Alignment.End else Alignment.Start,
        ) {
            Surface(
                color = bubbleColor,
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (message.fromMe) 16.dp else 4.dp,
                    bottomEnd = if (message.fromMe) 4.dp else 16.dp,
                ),
                modifier = Modifier.widthIn(max = 280.dp),
            ) {
                Text(
                    text = message.text,
                    color = textColor,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                )
            }
            Text(
                text = message.timestamp,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
            )
        }
    }
}

/** Compact avatar for the top bar and received-message rows. */
@Composable
private fun FriendAvatarSmall(friend: Friend) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .background(color = friend.avatarColor, shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = friend.initials,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewChatThreadScreen() {
    ChatThreadScreen(
        conversation = ChatRepository.seedConversations.first(),
        onBack = {},
    )
}
