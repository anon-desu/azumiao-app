package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import com.example.ui.theme.LavenderMist
import com.example.ui.theme.PeachBlush
import com.example.ui.theme.SakuraPink

@Composable
fun AnimatedGradientBackground(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "global_gradient")
    val shiftFraction by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient_shift"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                val spanX = size.width * 2.4f
                val spanY = size.height * 2.4f
                val offsetX = -shiftFraction * (spanX - size.width)
                val offsetY = -shiftFraction * (spanY - size.height)

                val brush = Brush.linearGradient(
                    colors = listOf(
                        SakuraPink,
                        PeachBlush,
                        PeachBlush,
                        LavenderMist
                    ),
                    start = Offset(x = offsetX, y = offsetY + spanY * 0.15f),
                    end = Offset(x = offsetX + spanX, y = offsetY + spanY * 0.85f)
                )
                drawRect(brush = brush)
            }
    )
}
