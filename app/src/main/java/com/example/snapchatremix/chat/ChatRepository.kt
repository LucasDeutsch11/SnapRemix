package com.example.snapchatremix.chat

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color

/**
 * Simple in-memory store for the demo chat screens. Seeds the app with a
 * handful of friends and conversations so the list and thread screens look
 * alive without any backend wired up.
 *
 * This is intentionally a singleton `object` (not injected) to keep the
 * footprint small for the SnapRemix prototype. Swap for a proper data
 * source — Room, Firestore, etc. — when the feature graduates.
 *
 * Message state: because the repository is a process-scoped singleton,
 * [messagesFor] returns a Compose-observable list that survives navigating
 * away from and back to a thread. It's wiped when the process dies — the
 * prototype has no disk persistence.
 */
object ChatRepository {

    private val avatarPalette = listOf(
        Color(0xFFFFFC00), // classic Snap yellow
        Color(0xFF00D1FF),
        Color(0xFFFF5E5B),
        Color(0xFF7C4DFF),
        Color(0xFF2EC4B6),
        Color(0xFFFF8A65),
    )

    /** Seeded conversations ordered as they'd appear in the chat list. */
    val seedConversations: List<Conversation> = listOf(
        Conversation(
            friend = Friend("f1", "Alex Rivera", "AR", avatarPalette[0]),
            lastMessagePreview = "yo wanna hit the skate park after school?",
            timestamp = "2m",
            unread = true,
            messages = listOf(
                ChatMessage("m1", fromMe = false, "hey", "3:10 PM"),
                ChatMessage("m2", fromMe = true, "whats up", "3:11 PM"),
                ChatMessage(
                    "m3",
                    fromMe = false,
                    "yo wanna hit the skate park after school?",
                    "3:12 PM",
                ),
            ),
        ),
        Conversation(
            friend = Friend("f2", "Jamie Chen", "JC", avatarPalette[1]),
            lastMessagePreview = "sent a snap",
            timestamp = "14m",
            unread = true,
            messages = listOf(
                ChatMessage("m1", fromMe = true, "did you finish the essay?", "2:47 PM"),
                ChatMessage("m2", fromMe = false, "almost, 1 page left", "2:58 PM"),
                ChatMessage("m3", fromMe = false, "sent a snap", "3:00 PM"),
            ),
        ),
        Conversation(
            friend = Friend("f3", "Priya Patel", "PP", avatarPalette[2]),
            lastMessagePreview = "lol that filter is unreal",
            timestamp = "1h",
            unread = false,
            messages = listOf(
                ChatMessage("m1", fromMe = true, "check out this lens", "2:02 PM"),
                ChatMessage("m2", fromMe = false, "lol that filter is unreal", "2:05 PM"),
            ),
        ),
        Conversation(
            friend = Friend("f4", "Marcus Lee", "ML", avatarPalette[3]),
            lastMessagePreview = "bet, see you saturday",
            timestamp = "3h",
            unread = false,
            messages = listOf(
                ChatMessage("m1", fromMe = false, "you still coming to the show?", "12:01 PM"),
                ChatMessage("m2", fromMe = true, "yeah I got my ticket", "12:05 PM"),
                ChatMessage("m3", fromMe = false, "bet, see you saturday", "12:06 PM"),
            ),
        ),
        Conversation(
            friend = Friend("f5", "Sofia Reyes", "SR", avatarPalette[4]),
            lastMessagePreview = "omg the dog is wearing sunglasses",
            timestamp = "1d",
            unread = false,
            messages = listOf(
                ChatMessage("m1", fromMe = false, "look at this", "Yesterday"),
                ChatMessage("m2", fromMe = true, "send pic!!!", "Yesterday"),
                ChatMessage(
                    "m3",
                    fromMe = false,
                    "omg the dog is wearing sunglasses",
                    "Yesterday",
                ),
            ),
        ),
        Conversation(
            friend = Friend("f6", "Devon Wright", "DW", avatarPalette[5]),
            lastMessagePreview = "happy birthday!!",
            timestamp = "2d",
            unread = false,
            messages = listOf(
                ChatMessage("m1", fromMe = false, "happy birthday!!", "Wed"),
                ChatMessage("m2", fromMe = true, "thanks man 🎉", "Wed"),
            ),
        ),
    )

    /** Find a conversation by the friend's id. */
    fun findByFriendId(friendId: String): Conversation? =
        seedConversations.firstOrNull { it.friend.id == friendId }

    // --------------------------------------------------------------------
    // Process-scoped live message store
    // --------------------------------------------------------------------

    /**
     * Per-friend snapshot lists of messages. Lazily seeded on first access
     * from [seedConversations] so the UI has something to render. Mutations
     * made by [ChatThreadScreen] (sending, deleting, etc.) land here and are
     * observable across screens.
     */
    private val messageStore: MutableMap<String, SnapshotStateList<ChatMessage>> =
        mutableMapOf()

    /**
     * Returns the live message list for [friendId]. The returned list is a
     * [SnapshotStateList] — Compose observes reads and recomposes on change.
     * Safe to hand the same list to multiple screens; they'll stay in sync.
     */
    fun messagesFor(friendId: String): SnapshotStateList<ChatMessage> {
        return messageStore.getOrPut(friendId) {
            val seed = findByFriendId(friendId)?.messages.orEmpty()
            mutableStateListOf<ChatMessage>().apply { addAll(seed) }
        }
    }

    /**
     * Look up the [Friend] metadata (name, initials, color) for a bookmark
     * or other secondary view that only has the id.
     */
    fun friendFor(friendId: String): Friend? =
        findByFriendId(friendId)?.friend
}
