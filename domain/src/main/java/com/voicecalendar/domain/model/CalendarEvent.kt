package com.voicecalendar.domain.model

import java.time.LocalDate
import java.time.LocalTime

/**
 * Domain entity representing a calendar event extracted from voice input.
 */
data class CalendarEvent(
    val id: String = "",
    val title: String,
    val description: String = "",
    val date: LocalDate? = null,
    val startTime: LocalTime? = null,
    val durationMinutes: Int = 60,
    val location: String = "",
    val category: EventCategory = EventCategory.OTHER,
    val priority: EventPriority = EventPriority.NORMAL,
    val reminders: List<Int> = emptyList(),
    val calendarId: Long? = null,
    val eventId: Long? = null,
    val isConfirmed: Boolean = false
) {
    val endTime: LocalTime?
        get() = if (startTime != null && durationMinutes > 0) {
            startTime!!.plusMinutes(durationMinutes.toLong())
        } else null
}

enum class EventCategory(val displayName: String) {
    MEDICAL("Medical"),
    WORK("Work"),
    TRAVEL("Travel"),
    SOCIAL("Social"),
    BIRTHDAY("Birthday"),
    ADMINISTRATIVE("Administrative"),
    OTHER("Other");

    companion object {
        fun fromString(value: String): EventCategory {
            return entries.firstOrNull {
                it.name.equals(value, ignoreCase = true) ||
                it.displayName.equals(value, ignoreCase = true)
            } ?: OTHER
        }
    }
}

enum class EventPriority(val displayName: String) {
    LOW("Low"),
    NORMAL("Normal"),
    HIGH("High"),
    URGENT("Urgent");

    companion object {
        fun fromString(value: String): EventPriority {
            return entries.firstOrNull {
                it.name.equals(value, ignoreCase = true) ||
                it.displayName.equals(value, ignoreCase = true)
            } ?: NORMAL
        }
    }
}
