package com.voicecalendar.domain.service

import com.voicecalendar.domain.model.EventCategory
import com.voicecalendar.domain.model.EventPriority
import org.junit.Assert.*
import org.junit.Test

class SmartReminderEngineTest {

    @Test
    fun `given medical category, generates three reminders`() {
        val reminders = SmartReminderEngine.generateReminders(EventCategory.MEDICAL)
        assertEquals(listOf(1440, 120, 30), reminders)
    }

    @Test
    fun `given work category, generates work-appropriate reminders`() {
        val reminders = SmartReminderEngine.generateReminders(EventCategory.WORK)
        assertEquals(listOf(30, 15), reminders)
    }

    @Test
    fun `given travel category, generates travel-appropriate reminders`() {
        val reminders = SmartReminderEngine.generateReminders(EventCategory.TRAVEL)
        assertEquals(listOf(2880, 1440, 120), reminders)
    }

    @Test
    fun `given urgent priority, adds boost reminders`() {
        val reminders = SmartReminderEngine.generateReminders(EventCategory.SOCIAL, EventPriority.URGENT)
        assertTrue(reminders.contains(5))
        assertTrue(reminders.contains(30))
    }

    @Test
    fun `given low priority, does not add extra reminders`() {
        val base = SmartReminderEngine.generateReminders(EventCategory.OTHER, EventPriority.NORMAL)
        val low = SmartReminderEngine.generateReminders(EventCategory.OTHER, EventPriority.LOW)
        assertEquals(base, low)
    }

    @Test
    fun `describeReminders returns human-readable strings`() {
        val descriptions = SmartReminderEngine.describeReminders(listOf(1440, 60, 15))
        assertEquals(3, descriptions.size)
        assertTrue(descriptions[0].contains("day"))
        assertTrue(descriptions[2].contains("minute"))
    }

    @Test
    fun `birthday category generates one week and one day reminders`() {
        val reminders = SmartReminderEngine.generateReminders(EventCategory.BIRTHDAY)
        assertTrue(reminders.contains(10080))
        assertTrue(reminders.contains(1440))
    }
}
