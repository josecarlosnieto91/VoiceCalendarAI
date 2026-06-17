package com.voicecalendar.core.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.voicecalendar.core.ui.theme.MicButtonDefault
import com.voicecalendar.core.ui.theme.MicButtonError
import com.voicecalendar.core.ui.theme.MicButtonListening
import com.voicecalendar.core.ui.theme.MicButtonProcessing
import com.voicecalendar.core.ui.theme.MicButtonSuccess

/**
 * Animated microphone button with state-dependent colors, scale animation, and shadow glow.
 * Sizes: 80dp container, 72dp circle, 36dp icon.
 */
@Composable
fun MicButton(
    state: VoiceState,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String = "Toggle microphone"
) {
    val isActive = state == VoiceState.LISTENING || state == VoiceState.PROCESSING

    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.15f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = "micScale"
    )

    val backgroundColor = when (state) {
        VoiceState.IDLE -> MicButtonDefault
        VoiceState.LISTENING -> MicButtonListening
        VoiceState.PROCESSING -> MicButtonProcessing
        VoiceState.SAVING -> MicButtonSuccess
        VoiceState.COMPLETED -> MicButtonSuccess
        VoiceState.ERROR -> MicButtonError
    }

    val elevation = if (isActive) 12.dp else 4.dp

    Box(contentAlignment = Alignment.Center, modifier = modifier.size(80.dp)) {
        IconButton(
            onClick = onToggle,
            modifier = Modifier
                .size(72.dp)
                .scale(scale)
                .shadow(elevation, CircleShape)
                .background(backgroundColor, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = contentDescription,
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}
