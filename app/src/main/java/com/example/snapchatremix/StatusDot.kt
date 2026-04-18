package com.yourapp.snapchatremix.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.snapchatremix.UserStatus

// 🟢 ACTIVE  = user is in the app and interacting
// 🔵 IDLE    = open but no interaction for 3+ minutes
// 🔴 OFFLINE = left the app or connection dropped
@Composable
fun StatusDot(
    status: UserStatus,
    modifier: Modifier = Modifier
) {
    val color = when (status) {
        UserStatus.ACTIVE  -> Color(0xFF4CAF50)   // Green
        UserStatus.IDLE    -> Color(0xFF2196F3)   // Blue
        UserStatus.OFFLINE -> Color(0xFFF44336)   // Red
    }

    Box(
        modifier = modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(color)
            .border(1.5.dp, Color.White, CircleShape)
    )
}