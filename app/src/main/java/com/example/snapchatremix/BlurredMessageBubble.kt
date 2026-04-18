package com.example.snapchatremix.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BlurredMessageBubble(
    text: String,
    isScreenshotActive: Boolean,
    isSentByMe: Boolean,
    modifier: Modifier = Modifier
) {
    val blurRadius by animateDpAsState(
        targetValue = if (isScreenshotActive) 20.dp else 0.dp,
        animationSpec = tween(durationMillis = 300),
        label = "blur_radius"
    )

    val bubbleColor = if (isSentByMe) Color(0xFF0A84FF) else Color(0xFF2C2C2E)
    val alignment = if (isSentByMe) Alignment.End else Alignment.Start

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(bubbleColor, RoundedCornerShape(18.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.blur(blurRadius)
            )

            if (isScreenshotActive) {
                Box(
                    modifier = Modifier.matchParentSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🔒", fontSize = 18.sp)
                }
            }
        }
    }
}