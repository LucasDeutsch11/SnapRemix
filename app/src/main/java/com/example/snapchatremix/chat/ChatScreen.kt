package com.example.snapchatremix.chat

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

/**
 * Top-level entry point for the chat feature.
 *
 * Owns the "which screen is visible" state — either the list of
 * conversations or a single thread with a specific friend. Written as a
 * single stateful composable so it can be dropped into MainActivity (or any
 * host) without pulling in a navigation dependency.
 *
 * State it manages:
 *  - `openFriendId`: the id of the friend whose thread is open, or `null`
 *    when showing the list. Survives rotation via [rememberSaveable].
 *
 * Navigation behavior:
 *  - Tapping a conversation row sets `openFriendId`, which swaps the list
 *    for [ChatThreadScreen].
 *  - The thread's back button — and the system back gesture via
 *    [BackHandler] — clears `openFriendId`, returning to the list.
 *
 * [conversations] defaults to [ChatRepository.seedConversations] so this
 * screen can be previewed and used standalone. Pass in your own list once a
 * real data source exists.
 */
@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    conversations: List<Conversation> = ChatRepository.seedConversations,
    onOpenSettings: () -> Unit = {},
) {
    var openFriendId: String? by rememberSaveable { mutableStateOf(null) }

    val openConversation = openFriendId?.let { id ->
        conversations.firstOrNull { it.friend.id == id }
    }

    // Let the hardware / gesture back close the thread first, rather than
    // bubbling up to the host's back handling.
    BackHandler(enabled = openConversation != null) {
        openFriendId = null
    }

    AnimatedContent(
        targetState = openConversation,
        transitionSpec = {
            if (targetState != null) {
                // Opening a thread: slide in from the right.
                (slideInHorizontally(tween(220)) { it } + fadeIn(tween(220))) togetherWith
                    (slideOutHorizontally(tween(220)) { -it / 4 } + fadeOut(tween(220)))
            } else {
                // Closing a thread: slide back out to the right.
                (slideInHorizontally(tween(220)) { -it / 4 } + fadeIn(tween(220))) togetherWith
                    (slideOutHorizontally(tween(220)) { it } + fadeOut(tween(220)))
            }
        },
        label = "chat-screen-transition",
        modifier = modifier,
    ) { current ->
        if (current == null) {
            ChatListScreen(
                conversations = conversations,
                onOpenChat = { openFriendId = it.friend.id },
                onOpenSettings = onOpenSettings,
            )
        } else {
            ChatThreadScreen(
                conversation = current,
                onBack = { openFriendId = null },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewChatScreen() {
    ChatScreen()
}
