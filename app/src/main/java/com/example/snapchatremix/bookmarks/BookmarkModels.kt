package com.example.snapchatremix.bookmarks

import androidx.compose.ui.graphics.Color

/**
 * A message the user has saved. We snapshot the sender/thread context at
 * bookmark time so the Bookmarks screen can render each entry standalone —
 * and so deleting the original message from its thread doesn't remove the
 * bookmark.
 *
 * [id] is unique to the bookmark record; [sourceMessageId] is the id of the
 * original [com.example.snapchatremix.chat.ChatMessage] (useful if we ever
 * want to jump back to the exact message).
 */
data class BookmarkedMessage(
    val id: String,
    val sourceFriendId: String,
    val sourceMessageId: String,
    val friendName: String,
    val friendInitials: String,
    val friendColor: Color,
    val fromMe: Boolean,
    val text: String,
    val timestamp: String,
    /** When the bookmark was saved (human-readable, for the list row). */
    val savedAt: String,
)
