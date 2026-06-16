package com.voicecalendar.domain.usecase

import com.voicecalendar.domain.model.CalendarEventJson
import com.voicecalendar.domain.model.LlmConfig
import com.voicecalendar.domain.repository.LlmRepository
import com.voicecalendar.domain.repository.SettingsRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
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
    fun `given valid voice text, when processed, then returns CalendarEvent`() = runTest {
        val voiceText = "Meeting with John tomorrow at 3pm"
        val json = CalendarEventJson(
            title = "Meeting with John",
            date = "2026-06-18",
            startTime = "15:00",
            endTime = "16:00",
            reminderMinutes = 15
        )

        coEvery { llmRepository.extractEventFromText(any(), any()) } returns Result.success(json)

        val result = useCase(voiceText)

        assertTrue(result.isSuccess)
        val event = result.getOrNull()
        assertNotNull(event)
        assertTrue(event?.title?.contains("Meeting") == true)
    }

    @Test
    fun `given empty voice text, when processed, then returns failure`() = runTest {
        coEvery { llmRepository.extractEventFromText(any(), any()) } returns
            Result.failure(IllegalArgumentException("Empty input"))

        val result = useCase("")

        assertTrue(result.isFailure)
    }
}
