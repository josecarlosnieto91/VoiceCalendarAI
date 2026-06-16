package com.voicecalendar.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.voicecalendar.core.ui.theme.StatusCompleted
import com.voicecalendar.core.ui.theme.StatusError
import com.voicecalendar.core.ui.theme.StatusIdle
import com.voicecalendar.core.ui.theme.StatusListening
import com.voicecalendar.core.ui.theme.StatusProcessing
import com.voicecalendar.core.ui.theme.StatusSaving

/**
 * Represents the current state of voice input processing.
 */
enum class VoiceState {
    IDLE,
    LISTENING,
    PROCESSING,
    SAVING,
    COMPLETED,
    ERROR
}

/**
 * Composable that displays an animated status indicator with icon and text.
 * Transitions between states with fade/slide animations.
 */
@Composable
fun StatusIndicator(
    state: VoiceState,
    message: String = "",
    modifier: Modifier = Modifier
) {
    val data = when (state) {
        VoiceState.IDLE -> StatusIndicatorData(null, StatusIdle, "Tap to speak")
        VoiceState.LISTENING -> StatusIndicatorData(Icons.Default.Mic, StatusListening, message.ifBlank { "Listening..." })
        VoiceState.PROCESSING -> StatusIndicatorData(Icons.Default.Search, StatusProcessing, message.ifBlank { "Processing..." })
        VoiceState.SAVING -> StatusIndicatorData(Icons.Default.Save, StatusSaving, message.ifBlank { "Saving..." })
        VoiceState.COMPLETED -> StatusIndicatorData(Icons.Default.CheckCircle, StatusCompleted, message.ifBlank { "Event saved!" })
        VoiceState.ERROR -> StatusIndicatorData(Icons.Default.Error, StatusError, message.ifBlank { "Something went wrong" })
    }

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(300)) + slideInVertically(animationSpec = tween(300)) { it / 2 },
        exit = fadeOut(animationSpec = tween(200)) + slideOutVertically(animationSpec = tween(200)) { it / 2 }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (state == VoiceState.PROCESSING || state == VoiceState.SAVING) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = data.color,
                        strokeWidth = 2.dp
                    )
                } else if (data.icon != null) {
                    Icon(
                        imageVector = data.icon,
                        contentDescription = data.text,
                        tint = data.color,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = data.text,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = data.color
                )
            }
            if (state == VoiceState.LISTENING && message.length > 30) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = data.color.copy(alpha = 0.7f)
                )
            }
        }
    }
}

private data class StatusIndicatorData(
    val icon: ImageVector?,
    val color: Color,
    val text: String
)
