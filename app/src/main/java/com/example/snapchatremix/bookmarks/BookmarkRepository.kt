package com.example.snapchatremix.bookmarks

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.snapchatremix.chat.ChatMessage
import com.example.snapchatremix.chat.Friend
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Process-scoped store of bookmarks. Same shape as ChatRepository: a
 * singleton object backed by a [SnapshotStateList] so Compose UIs observe
 * changes automatically.
 *
 * No disk persistence — bookmarks disappear when the process dies. Promote
 * to DataStore/Room when this feature graduates from prototype.
 */
object BookmarkRepository {

    /** Newest saves first. Exposed as a snapshot list for Compose. */
    val bookmarks: SnapshotStateList<BookmarkedMessage> = mutableStateListOf()

    private val savedAtFormatter = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())

    /**
     * Add a bookmark for [message] in the thread with [friend]. Idempotent —
     * bookmarking the same message twice is a no-op so the UI doesn't end
     * up with duplicates.
     */
    fun add(friend: Friend, message: ChatMessage) {
        if (isBookmarked(message.id, friend.id)) return
        bookmarks.add(
            0,
            BookmarkedMessage(
                id = "bm-${System.nanoTime()}",
                sourceFriendId = friend.id,
                sourceMessageId = message.id,
                friendName = friend.name,
                friendInitials = friend.initials,
                friendColor = friend.avatarColor,
                fromMe = message.fromMe,
                text = message.text,
                timestamp = message.timestamp,
                savedAt = savedAtFormatter.format(Date()),
            ),
        )
    }

    /** Remove a bookmark by its bookmark id (not the source message id). */
    fun remove(bookmarkId: String) {
        bookmarks.removeAll { it.id == bookmarkId }
    }

    /**
     * True if the given source message is already saved. Checked against
     * both the message and friend id because message ids are only unique
     * within a conversation ("m1", "m2", ...).
     */
    fun isBookmarked(sourceMessageId: String, sourceFriendId: String): Boolean =
        bookmarks.any {
            it.sourceMessageId == sourceMessageId && it.sourceFriendId == sourceFriendId
        }
}
