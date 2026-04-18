package com.example.snapchatremix.chat

import androidx.compose.ui.graphics.Color

/**
 * A friend you can message. [initials] is a 1-2 character string shown inside
 * the avatar circle, and [avatarColor] is the background of that circle so
 * we can render nice-looking placeholders without needing image assets.
 */
data class Friend(
    val id: String,
    val name: String,
    val initials: String,
    val avatarColor: Color,
)

/**
 * A single message inside a conversation.
 *
 * [fromMe] is `true` for messages the local user sent, `false` for messages
 * received from [Conversation.friend]. The UI aligns the two sides
 * differently based on this flag.
 *
 * [replyTo] is non-null when this message was sent as a reply to a previous
 * one — it holds a snapshot of the quoted content so deleting the original
 * message doesn't break the reply's preview.
 */
data class ChatMessage(
    val id: String,
    val fromMe: Boolean,
    val text: String,
    val timestamp: String,
    val replyTo: ReplySnippet? = null,
)

/**
 * A frozen snippet of an older message, shown above a reply so you can see
 * what the sender was responding to. Kept separate from [ChatMessage] so the
 * quoted preview survives edits/deletes of the original.
 */
data class ReplySnippet(
    /** Who sent the original message — "You" or the friend's name. */
    val authorLabel: String,
    /** Short excerpt of the original message body. */
    val preview: String,
)

/**
 * A chat thread with one friend. Holds the full message history so opening
 * the thread shows the conversation instantly.
 *
 * [lastMessagePreview] and [timestamp] are denormalized for the list view —
 * they're effectively the tail of [messages] pre-formatted for compact
 * display, which keeps the row renderer simple.
 *
 * [unread] drives the bold-text + dot treatment on the list row.
 */
data class Conversation(
    val friend: Friend,
    val lastMessagePreview: String,
    val timestamp: String,
    val unread: Boolean,
    val messages: List<ChatMessage>,
) {
    /**
     * Convenience helper for search: checks whether [query] appears (case
     * insensitive) in the friend's name or the last-message preview. Keeping
     * this on the model so screens don't scatter matching logic around.
     */
    fun matches(query: String): Boolean {
        if (query.isBlank()) return true
        val needle = query.trim().lowercase()
        return friend.name.lowercase().contains(needle) ||
            lastMessagePreview.lowercase().contains(needle)
    }
}
