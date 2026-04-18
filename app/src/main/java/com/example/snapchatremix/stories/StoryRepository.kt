package com.example.snapchatremix.stories

import androidx.compose.ui.graphics.Color

/**
 * Seed data for the Stories screen. The feed has two sections:
 *
 *  - [friends]: stories from people you chat with. Uses the same palette as
 *    ChatRepository so faces feel consistent across the app.
 *  - [discover]: bigger "publisher-style" cards, mimicking the Discover row
 *    in Snapchat.
 */
object StoryRepository {

    val friends: List<Story> = listOf(
        Story(
            id = "s1",
            author = "Alex Rivera",
            authorInitials = "AR",
            avatarColor = Color(0xFFFFFC00),
            headline = "Skate park sesh \ud83d\udef9",
            timestamp = "12m",
            gradientStart = Color(0xFFFFB347),
            gradientEnd = Color(0xFFFF5E5B),
            viewed = false,
        ),
        Story(
            id = "s2",
            author = "Jamie Chen",
            authorInitials = "JC",
            avatarColor = Color(0xFF00D1FF),
            headline = "study grind",
            timestamp = "34m",
            gradientStart = Color(0xFF00D1FF),
            gradientEnd = Color(0xFF7C4DFF),
            viewed = false,
        ),
        Story(
            id = "s3",
            author = "Priya Patel",
            authorInitials = "PP",
            avatarColor = Color(0xFFFF5E5B),
            headline = "new lens drop",
            timestamp = "1h",
            gradientStart = Color(0xFFFF8A65),
            gradientEnd = Color(0xFFFFFC00),
            viewed = true,
        ),
        Story(
            id = "s4",
            author = "Marcus Lee",
            authorInitials = "ML",
            avatarColor = Color(0xFF7C4DFF),
            headline = "concert hype",
            timestamp = "3h",
            gradientStart = Color(0xFF7C4DFF),
            gradientEnd = Color(0xFF2EC4B6),
            viewed = true,
        ),
        Story(
            id = "s5",
            author = "Sofia Reyes",
            authorInitials = "SR",
            avatarColor = Color(0xFF2EC4B6),
            headline = "cooking disaster",
            timestamp = "5h",
            gradientStart = Color(0xFF2EC4B6),
            gradientEnd = Color(0xFFFFB347),
            viewed = false,
        ),
        Story(
            id = "s6",
            author = "Devon Wright",
            authorInitials = "DW",
            avatarColor = Color(0xFFFF8A65),
            headline = "bday recap",
            timestamp = "1d",
            gradientStart = Color(0xFFFF5E5B),
            gradientEnd = Color(0xFF7C4DFF),
            viewed = true,
        ),
    )

    val discover: List<Story> = listOf(
        Story(
            id = "d1",
            author = "ESPN",
            authorInitials = "ES",
            avatarColor = Color(0xFFFF0033),
            headline = "Playoffs: every buzzer-beater",
            timestamp = "Today",
            gradientStart = Color(0xFFFF0033),
            gradientEnd = Color(0xFF111111),
            viewed = false,
        ),
        Story(
            id = "d2",
            author = "The Daily",
            authorInitials = "TD",
            avatarColor = Color(0xFF222222),
            headline = "What's happening in 5 minutes",
            timestamp = "Today",
            gradientStart = Color(0xFF222222),
            gradientEnd = Color(0xFF555555),
            viewed = false,
        ),
        Story(
            id = "d3",
            author = "Cosmo",
            authorInitials = "CO",
            avatarColor = Color(0xFFFF4FA0),
            headline = "17 looks for spring",
            timestamp = "Today",
            gradientStart = Color(0xFFFF4FA0),
            gradientEnd = Color(0xFFFFC0CB),
            viewed = true,
        ),
        Story(
            id = "d4",
            author = "National Geographic",
            authorInitials = "NG",
            avatarColor = Color(0xFFFFD300),
            headline = "Deep-sea creatures you\u2019ve never seen",
            timestamp = "Today",
            gradientStart = Color(0xFF003366),
            gradientEnd = Color(0xFF00D1FF),
            viewed = false,
        ),
    )
}
