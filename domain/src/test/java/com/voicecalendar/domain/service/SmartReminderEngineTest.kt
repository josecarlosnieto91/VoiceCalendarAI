package com.voicecalendar.domain.service

import com.voicecalendar.domain.model.EventCategory
import com.voicecalendar.domain.model.EventPriority
import org.junit.Assert.*
import org.junit.Test

class SmartReminderEngineTest {

    // === Default category rules ===

    @Test
    fun `medical category generates 1 week, 1 day, 2 hours reminders`() {
        val reminders = SmartReminderEngine.generateReminders(EventCategory.MEDICAL)
        assertEquals(listOf(10080, 1440, 120), reminders)
    }

    @Test
    fun `travel category generates 30 days, 1 week, 1 day reminders`() {
        val reminders = SmartReminderEngine.generateReminders(EventCategory.TRAVEL)
        assertEquals(listOf(43200, 10080, 1440), reminders)
    }

    @Test
    fun `work category generates 1 hour and 15 min reminders`() {
        val reminders = SmartReminderEngine.generateReminders(EventCategory.WORK)
        assertEquals(listOf(60, 15), reminders)
    }

    @Test
    fun `birthday category generates 1 week and 1 day reminders`() {
        val reminders = SmartReminderEngine.generateReminders(EventCategory.BIRTHDAY)
        assertEquals(listOf(10080, 1440), reminders)
    }

    @Test
    fun `administrative category generates 3 days and 1 day reminders`() {
        val reminders = SmartReminderEngine.generateReminders(EventCategory.ADMINISTRATIVE)
        assertEquals(listOf(4320, 1440), reminders)
    }

    @Test
    fun `social category generates 1 day and 1 hour reminders`() {
        val reminders = SmartReminderEngine.generateReminders(EventCategory.SOCIAL)
        assertEquals(listOf(1440, 60), reminders)
    }

    // === Priority boost ===

    @Test
    fun `urgent priority adds extra reminders`() {
        val reminders = SmartReminderEngine.generateReminders(EventCategory.SOCIAL, EventPriority.URGENT)
        assertTrue("should contain 5 min reminder", reminders.contains(5))
        assertTrue("should contain 30 min reminder", reminders.contains(30))
        assertTrue("should contain 2 hour reminder", reminders.contains(120))
    }

    @Test
    fun `high priority adds day and hour reminders`() {
        val reminders = SmartReminderEngine.generateReminders(EventCategory.WORK, EventPriority.HIGH)
        assertTrue("should contain 1 day reminder", reminders.contains(1440))
        assertTrue("should contain 1 hour reminder", reminders.contains(60))
    }

    @Test
    fun `low priority does not add boost reminders`() {
        val normal = SmartReminderEngine.generateReminders(EventCategory.OTHER, EventPriority.NORMAL)
        val low = SmartReminderEngine.generateReminders(EventCategory.OTHER, EventPriority.LOW)
        assertEquals(normal, low)
    }

    // === User configurability ===

    @Test
    fun `withConfig overrides default reminders for a category`() {
        val customReminders = listOf(10, 5)
        val engine = SmartReminderEngine.withConfig(
            mapOf(EventCategory.WORK to customReminders)
        )
        val reminders = engine.generateReminders(EventCategory.WORK)
        assertEquals(customReminders, reminders)
    }

    @Test
    fun `withConfig does not affect non-overridden categories`() {
        val engine = SmartReminderEngine.withConfig(
            mapOf(EventCategory.WORK to listOf(10, 5))
        )
        val medical = engine.generateReminders(EventCategory.MEDICAL)
        assertEquals(listOf(10080, 1440, 120), medical)
    }

    @Test
    fun `empty config uses defaults for all categories`() {
        val engine = SmartReminderEngine.withConfig(emptyMap())
        val social = engine.generateReminders(EventCategory.SOCIAL)
        assertEquals(listOf(1440, 60), social)
    }

    // === extensibility ===

    @Test
    fun `ReminderRule validates constructor`() {
        val rule = ReminderRule(EventCategory.MEDICAL, listOf(1440, 120))
        assertEquals(EventCategory.MEDICAL, rule.category)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `ReminderRule rejects empty list`() {
        ReminderRule(EventCategory.OTHER, emptyList())
    }

    @Test(expected = IllegalArgumentException::class)
    fun `ReminderRule rejects out-of-range reminders`() {
        ReminderRule(EventCategory.OTHER, listOf(99999))
    }

    // === ReminderConfig ===

    @Test
    fun `ReminderConfig toEngine creates configured engine`() {
        val config = ReminderConfig(
            categoryOverrides = mapOf("work" to listOf(30, 15)),
            priorityBoostEnabled = true
        )
        val engine = config.toEngine()
        val work = engine.generateReminders(EventCategory.WORK)
        assertEquals(listOf(30, 15), work)
    }

    @Test
    fun `empty ReminderConfig uses defaults`() {
        val config = ReminderConfig()
        val engine = config.toEngine()
        val travel = engine.generateReminders(EventCategory.TRAVEL)
        assertEquals(listOf(43200, 10080, 1440), travel)
    }

    // === describeReminders ===

    @Test
    fun `describeReminders returns correct strings`() {
        val descriptions = SmartReminderEngine.describeReminders(listOf(43200, 10080, 1440, 60, 15))
        assertEquals(5, descriptions.size)
        assertTrue("month", descriptions[0].contains("month"))
        assertTrue("week", descriptions[1].contains("week"))
        assertTrue("day", descriptions[2].contains("day"))
        assertTrue("hour", descriptions[3].contains("hour"))
        assertTrue("minute", descriptions[4].contains("minute"))
    }

    @Test
    fun `descriptions are sorted in the order passed`() {
        val descriptions = SmartReminderEngine.describeReminders(listOf(15, 60))
        assertEquals(2, descriptions.size)
        assertTrue(descriptions[0].contains("15"))
        assertTrue(descriptions[1].contains("60"))
    }
}
