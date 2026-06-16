package com.voicecalendar.domain.usecase

import com.voicecalendar.domain.model.CalendarEvent
import com.voicecalendar.domain.model.CalendarEventJson
import com.voicecalendar.domain.model.EventCategory
import com.voicecalendar.domain.model.EventPriority
import com.voicecalendar.domain.model.LlmConfig
import com.voicecalendar.domain.repository.LlmRepository
import com.voicecalendar.domain.repository.SettingsRepository
import com.voicecalendar.domain.service.SmartReminderEngine
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * Use case to process raw voice text into a structured CalendarEvent using LLM.
 * Handles date/time resolution, category inference, and smart reminder generation.
 */
class ProcessVoiceInputUseCase @Inject constructor(
    private val llmRepository: LlmRepository,
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(voiceText: String): Result<CalendarEvent> {
        if (voiceText.isBlank()) {
            return Result.failure(IllegalArgumentException("No speech detected"))
        }

        val settings = settingsRepository.getSettingsSnapshot()
        val llmResult = llmRepository.extractEventFromText(voiceText, settings.llmConfig)

        return llmResult.map { json ->
            val category = EventCategory.fromString(json.category)
            val priority = EventPriority.fromString(json.priority)

            val date = resolveDate(json.date)
            val time = resolveTime(json.time, date)

            // Use LLM's reminders or generate smart reminders from engine
            val reminders = if (json.reminders.isNotEmpty()) {
                json.reminders.sortedDescending()
            } else {
                SmartReminderEngine.generateReminders(category, priority)
            }

            CalendarEvent(
                title = json.title.ifBlank { voiceText.take(50) },
                date = date,
                startTime = time,
                durationMinutes = json.durationMinutes.coerceIn(15, 1440),
                location = json.location,
                category = category,
                priority = priority,
                reminders = reminders
            )
        }
    }

    /**
     * Resolves date strings including relative dates.
     * Today's date is dynamically computed so the LLM receives the correct reference.
     */
    private fun resolveDate(dateStr: String): LocalDate? {
        if (dateStr.isBlank()) return null

        // Check for clarification requests from LLM
        if (dateStr.equals("CLARIFICATION_NEEDED", ignoreCase = true)) return null

        val today = LocalDate.now()

        // Handle relative dates
        val relativeDate = resolveRelativeDate(dateStr.lowercase(), today)
        if (relativeDate != null) return relativeDate

        // Try various date formats
        val formats = listOf(
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
            DateTimeFormatter.ofPattern("d/M/yyyy"),
            DateTimeFormatter.ofPattern("M/d/yyyy"),
            DateTimeFormatter.ofPattern("yyyy-M-d"),
            DateTimeFormatter.ofPattern("d-M-yyyy")
        )
        for (format in formats) {
            try {
                return LocalDate.parse(dateStr, format)
            } catch (_: Exception) {}
        }
        return null
    }

    /**
     * Resolves natural language relative date expressions.
     */
    private fun resolveRelativeDate(lower: String, today: LocalDate): LocalDate? {
        return when {
            lower.contains("today") || lower.contains("hoy") -> today
            lower.contains("tomorrow") || lower.contains("mañana") || lower.contains("manana") -> today.plusDays(1)
            lower.contains("day after tomorrow") || lower.contains("pasado mañana") -> today.plusDays(2)
            lower.contains("next week") || lower.contains("próxima semana") || lower.contains("proxima semana") -> today.plusWeeks(1)
            lower.contains("next month") || lower.contains("próximo mes") || lower.contains("proximo mes") -> today.plusMonths(1)
            lower.contains("next year") || lower.contains("próximo año") || lower.contains("proximo año") -> today.plusYears(1)
            lower.contains("next monday") || lower.contains("próximo lunes") || lower.contains("proximo lunes") -> today.with(java.time.DayOfWeek.MONDAY).takeIf { !it.isBefore(today) } ?: today.plusWeeks(1).with(java.time.DayOfWeek.MONDAY)
            lower.contains("next tuesday") || lower.contains("próximo martes") || lower.contains("proximo martes") -> today.with(java.time.DayOfWeek.TUESDAY).takeIf { !it.isBefore(today) } ?: today.plusWeeks(1).with(java.time.DayOfWeek.TUESDAY)
            lower.contains("next wednesday") || lower.contains("próximo miércoles") || lower.contains("proximo miercoles") -> today.with(java.time.DayOfWeek.WEDNESDAY).takeIf { !it.isBefore(today) } ?: today.plusWeeks(1).with(java.time.DayOfWeek.WEDNESDAY)
            lower.contains("next thursday") || lower.contains("próximo jueves") || lower.contains("proximo jueves") -> today.with(java.time.DayOfWeek.THURSDAY).takeIf { !it.isBefore(today) } ?: today.plusWeeks(1).with(java.time.DayOfWeek.THURSDAY)
            lower.contains("next friday") || lower.contains("próximo viernes") || lower.contains("proximo viernes") -> today.with(java.time.DayOfWeek.FRIDAY).takeIf { !it.isBefore(today) } ?: today.plusWeeks(1).with(java.time.DayOfWeek.FRIDAY)
            lower.contains("next saturday") || lower.contains("próximo sábado") || lower.contains("proximo sabado") -> today.with(java.time.DayOfWeek.SATURDAY).takeIf { !it.isBefore(today) } ?: today.plusWeeks(1).with(java.time.DayOfWeek.SATURDAY)
            lower.contains("next sunday") || lower.contains("próximo domingo") || lower.contains("proximo domingo") -> today.with(java.time.DayOfWeek.SUNDAY).takeIf { !it.isBefore(today) } ?: today.plusWeeks(1).with(java.time.DayOfWeek.SUNDAY)
            lower.contains("this week") || lower.contains("esta semana") -> today
            lower.contains("this weekend") || lower.contains("este fin de semana") -> today.with(java.time.DayOfWeek.SATURDAY).takeIf { !it.isBefore(today) } ?: today.plusWeeks(1).with(java.time.DayOfWeek.SATURDAY)
            else -> null
        }
    }

    /**
     * Resolves time strings including relative time expressions.
     */
    private fun resolveTime(timeStr: String, date: LocalDate?): LocalTime? {
        if (timeStr.isBlank()) return null

        val lower = timeStr.lowercase()

        // Relative time expressions
        val relativeTime = when {
            "morning" in lower || "mañana" in lower -> LocalTime.of(9, 0)
            "afternoon" in lower || "tarde" in lower -> LocalTime.of(14, 0)
            "evening" in lower || "noche" in lower -> LocalTime.of(19, 0)
            "night" in lower -> LocalTime.of(21, 0)
            "lunch" in lower || "comida" in lower || "almuerzo" in lower -> LocalTime.of(13, 0)
            "midnight" in lower || "medianoche" in lower -> LocalTime.of(0, 0)
            "noon" in lower || "mediodía" in lower || "mediodia" in lower -> LocalTime.of(12, 0)
            else -> null
        }
        if (relativeTime != null) return relativeTime

        // Try various time formats including AM/PM
        val formats = listOf(
            DateTimeFormatter.ISO_LOCAL_TIME,
            DateTimeFormatter.ofPattern("HH:mm"),
            DateTimeFormatter.ofPattern("H:mm"),
            DateTimeFormatter.ofPattern("hh:mm a"),
            DateTimeFormatter.ofPattern("h:mm a"),
            DateTimeFormatter.ofPattern("hh:mma"),
            DateTimeFormatter.ofPattern("h:mma"),
            DateTimeFormatter.ofPattern("HHmm"),
            DateTimeFormatter.ofPattern("hha"),
            DateTimeFormatter.ofPattern("ha")
        )
        for (format in formats) {
            try {
                return LocalTime.parse(timeStr.uppercase().replace(" ", ""), format)
            } catch (_: Exception) {}
        }
        return null
    }

    /**
     * Checks if the LLM response indicates clarification is needed.
     */
    fun isClarificationNeeded(eventJson: CalendarEventJson): Boolean {
        return eventJson.title == "CLARIFICATION_NEEDED"
    }
}
