package com.voicecalendar.feature.confirmation

import com.voicecalendar.domain.model.CalendarEvent
import com.voicecalendar.domain.model.EventCategory

/**
 * Validates calendar events before saving, including category-specific rules.
 */
object EventValidator {

    sealed class ValidationResult {
        data object Valid : ValidationResult()
        data class Invalid(val errors: List<String>) : ValidationResult()
    }

    fun validate(event: CalendarEvent): ValidationResult {
        val errors = mutableListOf<String>()

        if (event.title.isBlank()) errors.add("Title is required")
        if (event.title.length > 100) errors.add("Title is too long (max 100 characters)")
        if (event.date == null && event.category != EventCategory.OTHER) {
            errors.add("Date is required for ${event.category.displayName} events")
        }
        if (event.startTime != null && event.date == null) {
            errors.add("Cannot set time without a date")
        }
        if (event.durationMinutes < 15) errors.add("Duration must be at least 15 minutes")
        if (event.durationMinutes > 1440) errors.add("Duration cannot exceed 24 hours")
        if (event.reminders.any { it < 0 }) errors.add("Reminder times cannot be negative")
        if (event.reminders.any { it > 10080 }) errors.add("Reminder times cannot exceed 1 week")

        when (event.category) {
            EventCategory.MEDICAL -> {
                if (event.location.isBlank()) errors.add("Consider adding a location for medical appointments")
            }
            EventCategory.TRAVEL -> {
                if (event.location.isBlank()) errors.add("Location is recommended for travel events")
                if (event.durationMinutes < 120) errors.add("Travel events typically last 2+ hours")
            }
            else -> {}
        }

        return if (errors.isEmpty()) ValidationResult.Valid
        else ValidationResult.Invalid(errors)
    }
}
