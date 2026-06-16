package com.voicecalendar.feature.confirmation

import com.voicecalendar.domain.model.CalendarEvent

/**
 * Validates calendar events before saving.
 */
object EventValidator {

    sealed class ValidationResult {
        data object Valid : ValidationResult()
        data class Invalid(val errors: List<String>) : ValidationResult()
    }

    fun validate(event: CalendarEvent): ValidationResult {
        val errors = mutableListOf<String>()

        if (event.title.isBlank()) {
            errors.add("Title is required")
        }
        if (event.title.length > 100) {
            errors.add("Title is too long (max 100 characters)")
        }
        if (event.date == null) {
            errors.add("Date is required")
        }
        if (event.startTime != null && event.date == null) {
            errors.add("Cannot set time without a date")
        }
        if (event.reminderMinutes < 0) {
            errors.add("Reminder time cannot be negative")
        }

        return if (errors.isEmpty()) ValidationResult.Valid
        else ValidationResult.Invalid(errors)
    }
}
