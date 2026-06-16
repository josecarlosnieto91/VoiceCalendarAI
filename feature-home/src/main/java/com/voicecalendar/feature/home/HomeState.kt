package com.voicecalendar.feature.home

import com.voicecalendar.core.ui.components.VoiceState
import com.voicecalendar.domain.model.CalendarEvent

/**
 * UI state for the Home screen.
 */
data class HomeUiState(
    val state: VoiceState = VoiceState.IDLE,
    val partialText: String = "",
    val extractedEvent: CalendarEvent? = null,
    val upcomingEvents: List<CalendarEvent> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null,
    val showEditDialog: Boolean = false
)
