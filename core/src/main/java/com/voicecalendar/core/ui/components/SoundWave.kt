package com.voicecalendar.core.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * Animated sound wave visualization for the LISTENING state.
 * Draws 4 concentric circles that expand outward and fade away, plus a subtle pulse line.
 */
@Composable
fun SoundWave(
    isActive: Boolean,
    color: Color = Color(0xFF1565C0),
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "soundWave")

    val wave1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave1"
    )

    val wave2 by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave2"
    )

    val wave3 by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave3"
    )

    val wave4 by infiniteTransition.animateFloat(
        initialValue = 0.75f,
        targetValue = 1.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave4"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        if (!isActive) return@Canvas

        val centerX = size.width / 2
        val centerY = size.height / 2
        val maxRadius = minOf(size.width, size.height) * 0.45f

        // Draw 4 concentric expanding circles
        listOf(wave1, wave2, wave3, wave4).forEach { progress ->
            val adjustedProgress = (progress % 1f).coerceIn(0f, 1f)
            val radius = maxRadius * adjustedProgress
            val alpha = (1f - adjustedProgress).coerceIn(0f, 1f) * 0.6f

            drawCircle(
                color = color.copy(alpha = alpha),
                radius = radius,
                center = Offset(centerX, centerY),
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        // Inner pulsing glow circle
        val pulseRadius = maxRadius * 0.25f
        val pulseAlpha = 0.15f + ((wave1 % 1f) * 0.2f)
        drawCircle(
            color = color.copy(alpha = pulseAlpha),
            radius = pulseRadius,
            center = Offset(centerX, centerY)
        )
    }
}
