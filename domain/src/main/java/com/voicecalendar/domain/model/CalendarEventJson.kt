package com.voicecalendar.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * JSON representation of a calendar event for LLM extraction.
 * Matches the exact schema required by the AI engine.
 */
@Serializable
data class CalendarEventJson(
    val title: String = "",
    val date: String = "",
    val time: String = "",
    @SerialName("duration_minutes")
    val durationMinutes: Int = 60,
    val location: String = "",
    val category: String = "",
    val priority: String = "",
    val reminders: List<Int> = emptyList()
)
