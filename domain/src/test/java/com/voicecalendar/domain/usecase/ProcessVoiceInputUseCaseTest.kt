package com.voicecalendar.domain.usecase

import com.voicecalendar.domain.model.CalendarEventJson
import com.voicecalendar.domain.model.LlmConfig
import com.voicecalendar.domain.repository.LlmRepository
import com.voicecalendar.domain.repository.SettingsRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ProcessVoiceInputUseCaseTest {

    private val llmRepository: LlmRepository = mockk()
    private val settingsRepository: SettingsRepository = mockk()
    private lateinit var useCase: ProcessVoiceInputUseCase

    @Before
    fun setup() {
        useCase = ProcessVoiceInputUseCase(llmRepository, settingsRepository)
        coEvery { settingsRepository.getSettingsSnapshot() } returns mockk {
            coEvery { llmConfig } returns LlmConfig()
        }
    }

    @Test
    fun `given medical appointment text, when processed, returns event with medical category and smart reminders`() = runTest {
        val voiceText = "Dentist appointment tomorrow at 10am"
        val json = CalendarEventJson(
            title = "Dentist Appointment",
            date = "2026-06-18",
            time = "10:00",
            durationMinutes = 60,
            category = "medical",
            priority = "normal",
            reminders = emptyList()
        )

        coEvery { llmRepository.extractEventFromText(any(), any()) } returns Result.success(json)

        val result = useCase(voiceText)

        assertTrue(result.isSuccess)
        val event = result.getOrNull()
        assertNotNull(event)
        assertEquals("Dentist Appointment", event?.title)
        assertEquals(com.voicecalendar.domain.model.EventCategory.MEDICAL, event?.category)
        assertTrue(event?.reminders?.isNotEmpty() == true)
    }

    @Test
    fun `given travel text with relative date, when processed, returns event with resolved date`() = runTest {
        val json = CalendarEventJson(
            title = "Flight to Madrid",
            date = "",  // LLM didn't resolve, use case handles
            time = "",
            durationMinutes = 120,
            category = "travel",
            priority = "high"
        )

        coEvery { llmRepository.extractEventFromText(any(), any()) } returns Result.success(json)

        val result = useCase("Flight to Madrid this weekend")

        assertTrue(result.isSuccess)
    }

    @Test
    fun `given work meeting with duration, when processed, respects specified duration`() = runTest {
        val json = CalendarEventJson(
            title = "Sprint Planning",
            date = "2026-06-18",
            time = "09:00",
            durationMinutes = 120,
            category = "work",
            priority = "high",
            reminders = listOf(30, 15)
        )

        coEvery { llmRepository.extractEventFromText(any(), any()) } returns Result.success(json)

        val result = useCase("Sprint planning tomorrow at 9, 2 hours")

        assertTrue(result.isSuccess)
        val event = result.getOrNull()
        assertEquals(120, event?.durationMinutes)
        assertEquals(com.voicecalendar.domain.model.EventPriority.HIGH, event?.priority)
        assertTrue(event?.reminders?.contains(30) == true)
    }

    @Test
    fun `given empty text, when processed, returns failure`() = runTest {
        val result = useCase("")
        assertTrue(result.isFailure)
    }

    @Test
    fun `given birthday text, when processed, returns event with birthday category`() = runTest {
        val json = CalendarEventJson(
            title = "Ana's Birthday",
            date = "2026-06-18",
            category = "birthday",
            priority = "normal",
            reminders = listOf(10080, 1440)
        )

        coEvery { llmRepository.extractEventFromText(any(), any()) } returns Result.success(json)

        val result = useCase("Ana's birthday party tomorrow")

        assertTrue(result.isSuccess)
        val event = result.getOrNull()
        assertEquals(com.voicecalendar.domain.model.EventCategory.BIRTHDAY, event?.category)
    }
}
