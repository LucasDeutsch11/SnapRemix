package com.example.snapchatremix.stories

import androidx.compose.ui.graphics.Color

/**
 * A single story preview card shown on the Stories screen.
 *
 * No actual media — [gradientStart] and [gradientEnd] are used to paint a
 * colorful placeholder so the feed looks alive without any image assets.
 *
 * [viewed] dims the outer ring, mimicking Snapchat's "already watched"
 * affordance.
 */
data class Story(
    val id: String,
    val author: String,
    val authorInitials: String,
    val avatarColor: Color,
    val headline: String,
    val timestamp: String,
    val gradientStart: Color,
    val gradientEnd: Color,
    val viewed: Boolean,
)
