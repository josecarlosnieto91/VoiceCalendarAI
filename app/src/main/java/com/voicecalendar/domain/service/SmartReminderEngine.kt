package com.voicecalendar.domain.service

import com.voicecalendar.domain.model.EventCategory
import com.voicecalendar.domain.model.EventPriority

/**
 * Engine that generates smart reminders based on event category, priority, and context.
 *
 * Reminder rules are defined per category as default reminder times (in minutes before event).
 * Higher-priority events get additional reminders closer to the event time.
 */
object SmartReminderEngine {

    /**
     * Default reminders per category (in minutes before event).
     * Each category defines a list of reminder offsets.
     */
    private val categoryReminders: Map<EventCategory, List<Int>> = mapOf(
        EventCategory.MEDICAL to listOf(1440, 120, 30),    // 1 day, 2 hours, 30 min
        EventCategory.WORK to listOf(30, 15),                 // 30 min, 15 min
        EventCategory.TRAVEL to listOf(2880, 1440, 120),     // 2 days, 1 day, 2 hours
        EventCategory.SOCIAL to listOf(1440, 60),             // 1 day, 1 hour
        EventCategory.BIRTHDAY to listOf(10080, 1440),        // 1 week, 1 day
        EventCategory.ADMINISTRATIVE to listOf(1440, 60),     // 1 day, 1 hour
        EventCategory.OTHER to listOf(60, 15)                 // 1 hour, 15 min
    )

    /**
     * Extra reminders to add for high-priority events.
     */
    private val priorityBoost: Map<EventPriority, List<Int>> = mapOf(
        EventPriority.HIGH to listOf(1440, 60),     // +1 day and +1 hour
        EventPriority.URGENT to listOf(120, 30, 5),  // +2 hours, +30 min, +5 min
        EventPriority.NORMAL to emptyList(),
        EventPriority.LOW to emptyList()
    )

    /**
     * Generates the optimal set of reminders for an event based on its category and priority.
     *
     * @param category The event category
     * @param priority The event priority
     * @return List of reminder times in minutes before the event (sorted descending, unique)
     */
    fun generateReminders(
        category: EventCategory = EventCategory.OTHER,
        priority: EventPriority = EventPriority.NORMAL
    ): List<Int> {
        val base = categoryReminders[category] ?: categoryReminders[EventCategory.OTHER]!!
        val boost = priorityBoost[priority] ?: emptyList()

        return (base + boost)
            .distinct()
            .sortedDescending()
    }

    /**
     * Returns a user-friendly description of when reminders will fire.
     */
    fun describeReminders(reminders: List<Int>): List<String> {
        return reminders.map { minutes ->
            when {
                minutes >= 10080 -> "${minutes / 10080} week(s) before"
                minutes >= 1440 -> "${minutes / 1440} day(s) before"
                minutes >= 60 -> "${minutes / 60} hour(s) before"
                else -> "$minutes minute(s) before"
            }
        }
    }
}
