package com.example.snapchatremix.chat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snapchatremix.bookmarks.BookmarkRepository
import kotlinx.coroutines.launch

/**
 * Thread view for an individual chat. Shows the message history scrolled to
 * the bottom, with a top bar (friend's name + back button) and an input row
 * for composing new messages.
 *
 * Messages are persisted in [ChatRepository.messagesFor] — a process-scoped
 * SnapshotStateList — so sent messages survive navigating away from and
 * back to the thread. They're lost when the process dies (no disk).
 *
 * Long-pressing a bubble opens an action sheet with Bookmark, Reply, Copy,
 * and Delete. Reply surfaces a preview row above the composer until sent
 * or cancelled.
 *
 * [onBack] is called when the user taps the back arrow. The host composable
 * should pop back to the list view.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChatThreadScreen(
    conversation: Conversation,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Persistent message list for this friend — survives navigation because
    // it's backed by ChatRepository.messageStore, not by remember { ... }.
    val messages = remember(conversation.friend.id) {
        ChatRepository.messagesFor(conversation.friend.id)
    }
    var draft by remember(conversation.friend.id) { mutableStateOf("") }
    var replyingTo: ChatMessage? by remember(conversation.friend.id) {
        mutableStateOf(null)
    }
    // Which message (if any) the user long-pressed — drives the action sheet.
    var actionTarget: ChatMessage? by remember { mutableStateOf(null) }
    val listState = rememberLazyListState()
    val clipboard = LocalClipboardManager.current

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
                replyingTo = replyingTo,
                friendName = conversation.friend.name,
                onCancelReply = { replyingTo = null },
                onSend = {
                    val trimmed = draft.trim()
                    if (trimmed.isNotEmpty()) {
                        val replySnippet = replyingTo?.let { original ->
                            ReplySnippet(
                                authorLabel = if (original.fromMe) "You" else conversation.friend.name,
                                preview = original.text,
                            )
                        }
                        messages.add(
                            ChatMessage(
                                id = "local-${System.nanoTime()}",
                                fromMe = true,
                                text = trimmed,
                                timestamp = "now",
                                replyTo = replySnippet,
                            ),
                        )
                        draft = ""
                        replyingTo = null
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
                MessageBubble(
                    message = msg,
                    friend = conversation.friend,
                    onLongPress = { actionTarget = msg },
                )
            }
        }
    }

    // Bottom-sheet action menu for the long-pressed message.
    actionTarget?.let { target ->
        MessageActionsSheet(
            message = target,
            isBookmarked = BookmarkRepository.isBookmarked(target.id, conversation.friend.id),
            onBookmark = {
                BookmarkRepository.add(conversation.friend, target)
                actionTarget = null
            },
            onReply = {
                replyingTo = target
                actionTarget = null
            },
            onCopy = {
                clipboard.setText(AnnotatedString(target.text))
                actionTarget = null
            },
            onDelete = {
                messages.removeAll { it.id == target.id }
                actionTarget = null
            },
            onDismiss = { actionTarget = null },
        )
    }
}

/**
 * The bottom input row: optional reply preview, text field, send button.
 * Send is enabled only while the draft has non-blank content.
 */
@Composable
private fun MessageComposer(
    draft: String,
    onDraftChange: (String) -> Unit,
    replyingTo: ChatMessage?,
    friendName: String,
    onCancelReply: () -> Unit,
    onSend: () -> Unit,
) {
    Surface(tonalElevation = 2.dp) {
        Column {
            HorizontalDivider()
            if (replyingTo != null) {
                ReplyPreviewBar(
                    authorLabel = if (replyingTo.fromMe) "You" else friendName,
                    preview = replyingTo.text,
                    onCancel = onCancelReply,
                )
                HorizontalDivider()
            }
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

/** The "replying to X" banner that sits above the composer. */
@Composable
private fun ReplyPreviewBar(
    authorLabel: String,
    preview: String,
    onCancel: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 4.dp, top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Vertical accent stripe — same idea as iMessage/WhatsApp reply chip.
        Box(
            modifier = Modifier
                .size(width = 3.dp, height = 28.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(2.dp),
                ),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Replying to $authorLabel",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = preview,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )
        }
        IconButton(onClick = onCancel) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Cancel reply",
            )
        }
    }
}

/**
 * Bottom-sheet shown when a message bubble is long-pressed. Four actions:
 * Bookmark (toggles icon if already saved), Reply, Copy, Delete.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MessageActionsSheet(
    message: ChatMessage,
    isBookmarked: Boolean,
    onBookmark: () -> Unit,
    onReply: () -> Unit,
    onCopy: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    fun closeThen(action: () -> Unit) {
        scope.launch {
            sheetState.hide()
        }.invokeOnCompletion {
            action()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(modifier = Modifier.padding(bottom = 12.dp)) {
            // Preview of the target message so the user remembers what
            // they're acting on.
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            )
            HorizontalDivider()
            // Icon set is limited to what ships in material-icons-core so
            // the build doesn't need the extended icons artifact. Favorite
            // (heart) stands in for Bookmark; ArrowBack is used as a reply
            // glyph; Share is the closest semantic match for "Copy".
            ActionRow(
                icon = if (isBookmarked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                label = if (isBookmarked) "Already bookmarked" else "Bookmark",
                enabled = !isBookmarked,
                onClick = { closeThen(onBookmark) },
            )
            ActionRow(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                label = "Reply",
                onClick = { closeThen(onReply) },
            )
            ActionRow(
                icon = Icons.Filled.Share,
                label = "Copy",
                onClick = { closeThen(onCopy) },
            )
            ActionRow(
                icon = Icons.Filled.Delete,
                label = "Delete",
                destructive = true,
                onClick = { closeThen(onDelete) },
            )
        }
    }
}

@Composable
private fun ActionRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    destructive: Boolean = false,
) {
    val tint = when {
        !enabled -> MaterialTheme.colorScheme.onSurfaceVariant
        destructive -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurface
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = tint)
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = tint,
        )
    }
}

/**
 * A single message bubble. Right-aligned and tinted primary for messages
 * you sent; left-aligned and surface-variant for messages from the friend.
 *
 * Long-pressing the bubble invokes [onLongPress], which the parent uses to
 * show the action sheet.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MessageBubble(
    message: ChatMessage,
    friend: Friend,
    onLongPress: () -> Unit,
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
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .combinedClickable(
                        onClick = { /* no-op; reserved for future tap */ },
                        onLongClick = onLongPress,
                    ),
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    message.replyTo?.let { snippet ->
                        ReplyQuote(snippet = snippet, fromMe = message.fromMe)
                        Spacer(modifier = Modifier.size(6.dp))
                    }
                    Text(
                        text = message.text,
                        color = textColor,
                    )
                }
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

/**
 * Inline quote shown at the top of a reply bubble. Tint is softened vs. the
 * main bubble so the quote reads as secondary content.
 */
@Composable
private fun ReplyQuote(snippet: ReplySnippet, fromMe: Boolean) {
    val onBubble = if (fromMe) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    val quoteBg = onBubble.copy(alpha = 0.18f)
    Row(
        modifier = Modifier
            .background(color = quoteBg, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(width = 3.dp, height = 28.dp)
                .background(color = onBubble.copy(alpha = 0.8f), shape = RoundedCornerShape(2.dp)),
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(
                text = snippet.authorLabel,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = onBubble,
            )
            Text(
                text = snippet.preview,
                style = MaterialTheme.typography.bodySmall,
                color = onBubble.copy(alpha = 0.85f),
                maxLines = 2,
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
