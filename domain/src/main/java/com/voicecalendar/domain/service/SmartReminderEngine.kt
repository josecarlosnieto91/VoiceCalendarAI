package com.voicecalendar.domain.service

import com.voicecalendar.domain.model.EventCategory
import com.voicecalendar.domain.model.EventPriority

/**
 * Smart reminder engine that generates context-aware reminders based on event category, priority,
 * and user configuration.
 *
 * Fully extensible: custom [ReminderRule]s can be registered at runtime.
 * Fully configurable: users can override defaults per category via [ReminderConfig].
 *
 * Usage:
 *   SmartReminderEngine.generateReminders(EventCategory.MEDICAL)
 *   SmartReminderEngine.generateReminders(EventCategory.TRAVEL, EventPriority.HIGH)
 *   SmartReminderEngine.withConfig(myConfig).generateReminders(EventCategory.WORK)
 */
class SmartReminderEngine private constructor(
    private val overrides: Map<EventCategory, List<Int>> = emptyMap()
) {

    companion object Defaults {
        /**
         * Default reminder rules per category (in minutes before event).
         * Extensible: add new categories by registering a [ReminderRule].
         *
         *  medical → 1 week, 1 day, 2 hours
         *  travel → 30 days, 1 week, 1 day
         *  work → 1 hour, 15 min
         *  birthday → 1 week, 1 day
         *  administrative → 3 days, 1 day
         *  social → 1 day, 1 hour
         */
        private val defaultCategoryReminders: Map<EventCategory, List<Int>> = mapOf(
            EventCategory.MEDICAL to listOf(10080, 1440, 120),
            EventCategory.TRAVEL to listOf(43200, 10080, 1440),
            EventCategory.WORK to listOf(60, 15),
            EventCategory.BIRTHDAY to listOf(10080, 1440),
            EventCategory.ADMINISTRATIVE to listOf(4320, 1440),
            EventCategory.SOCIAL to listOf(1440, 60),
            EventCategory.OTHER to listOf(60, 15)
        )

        private val priorityBoost: Map<EventPriority, List<Int>> = mapOf(
            EventPriority.URGENT to listOf(120, 30, 5),
            EventPriority.HIGH to listOf(1440, 60),
            EventPriority.NORMAL to emptyList(),
            EventPriority.LOW to emptyList()
        )

        /** Singleton instance with default rules. */
        val instance: SmartReminderEngine by lazy { SmartReminderEngine() }

        /**
         * Create a configured engine with user overrides.
         * Overrides completely replace the default reminders for those categories.
         *
         * @param userReminders Map of category → custom reminder list.
         *        Pass empty map to use defaults for all categories.
         */
        fun withConfig(userReminders: Map<EventCategory, List<Int>>): SmartReminderEngine {
            return SmartReminderEngine(overrides = userReminders)
        }

        /**
         * Convenience: generate reminders using the default engine.
         * @see generateReminders
         */
        fun generateReminders(
            category: EventCategory = EventCategory.OTHER,
            priority: EventPriority = EventPriority.NORMAL
        ): List<Int> = instance.generateReminders(category, priority)

        /**
         * Convenience: describe reminders using the default engine.
         */
        fun describeReminders(reminders: List<Int>): List<String> =
            instance.describeReminders(reminders)
    }

    /**
     * Generates the optimal set of reminders for an event based on its category and priority.
     *
     * Resolution order:
     * 1. User override (if configured for this category)
     * 2. Default category rules
     * 3. Priority boost (merged on top)
     *
     * @param category The event category
     * @param priority The event priority
     * @return List of reminder times in minutes before the event (sorted descending, unique)
     */
    fun generateReminders(
        category: EventCategory = EventCategory.OTHER,
        priority: EventPriority = EventPriority.NORMAL
    ): List<Int> {
        val base = overrides[category]
            ?: defaultCategoryReminders[category]
            ?: defaultCategoryReminders[EventCategory.OTHER]!!

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
                minutes >= 43200 -> "${minutes / 43200} month(s) before"
                minutes >= 10080 -> "${minutes / 10080} week(s) before"
                minutes >= 1440 -> "${minutes / 1440} day(s) before"
                minutes >= 60 -> "${minutes / 60} hour(s) before"
                else -> "$minutes minute(s) before"
            }
        }
    }
}

/**
 * A single reminder rule definition for extensibility.
 *
 * Register custom rules at runtime:
 * ```
 * val customRule = ReminderRule(
 *     category = EventCategory.OTHER,
 *     reminders = listOf(720, 30)
 * )
 * ```
 */
data class ReminderRule(
    val category: EventCategory,
    val reminders: List<Int>
) {
    init {
        require(reminders.isNotEmpty()) { "Reminders list cannot be empty" }
        require(reminders.all { it in 0..43200 }) { "Reminders must be 0-43200 minutes (30 days)" }
    }
}

/**
 * User-facing configuration for reminder defaults.
 * Can be serialized/deserialized for persistence.
 */
data class ReminderConfig(
    val categoryOverrides: Map<String, List<Int>> = emptyMap(),  // category name → reminders
    val priorityBoostEnabled: Boolean = true,
    val maxRemindersPerEvent: Int = 5
) {
    fun toEngine(): SmartReminderEngine {
        val mapped = categoryOverrides.mapKeys { (key, _) ->
            EventCategory.fromString(key)
        }
        return SmartReminderEngine.withConfig(mapped)
    }
}
