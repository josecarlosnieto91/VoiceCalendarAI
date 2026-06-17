package com.voicecalendar.core.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * Utility functions for date/time formatting and relative time calculations.
 */
object DateUtils {

    private val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val shortDateFormatter = DateTimeFormatter.ofPattern("MMM d")

    fun formatDate(date: LocalDate): String {
        return date.format(dateFormatter)
    }

    fun formatTime(time: LocalTime): String {
        return time.format(timeFormatter)
    }

    fun formatShortDate(date: LocalDate): String {
        return date.format(shortDateFormatter)
    }

    /**
     * Returns a human-readable relative time string.
     */
    fun getRelativeTimeString(date: LocalDate): String {
        val today = LocalDate.now()
        val daysBetween = ChronoUnit.DAYS.between(today, date)

        return when {
            daysBetween < 0 -> "${-daysBetween} day(s) ago"
            daysBetween == 0L -> "Today"
            daysBetween == 1L -> "Tomorrow"
            daysBetween <= 7L -> "In $daysBetween days"
            daysBetween <= 14L -> "Next week"
            daysBetween <= 30L -> "In ${daysBetween / 7} week(s)"
            daysBetween <= 60L -> "Next month"
            else -> "In ${daysBetween / 30} month(s)"
        }
    }

    fun isToday(date: LocalDate): Boolean = date == LocalDate.now()

    fun isTomorrow(date: LocalDate): Boolean = date == LocalDate.now().plusDays(1)

    fun getDayOfWeek(date: LocalDate): DayOfWeek = date.dayOfWeek
}
