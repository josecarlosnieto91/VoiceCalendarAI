package com.voicecalendar.data.repository

import com.voicecalendar.data.remote.api.LlmApi
import com.voicecalendar.data.remote.dto.LlmChatResponse
import com.voicecalendar.data.remote.dto.LlmChoice
import com.voicecalendar.data.remote.dto.LlmResponseMessage
import com.voicecalendar.domain.model.LlmConfig
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class LlmRepositoryImplTest {

    private val llmApi: LlmApi = mockk()
    private lateinit var repository: LlmRepositoryImpl

    @Before
    fun setup() {
        repository = LlmRepositoryImpl(llmApi)
    }

    @Test
    fun `given valid LLM response with new schema, when extracting event, returns parsed JSON`() = runTest {
        val jsonResponse = """{"title":"Team Standup","date":"2026-06-18","time":"09:00","duration_minutes":30,"location":"Room A","category":"work","priority":"normal","reminders":[30,15]}"""

        coEvery { llmApi.chatCompletion(any(), any()) } returns LlmChatResponse(
            choices = listOf(LlmChoice(message = LlmResponseMessage(content = jsonResponse)))
        )

        val result = repository.extractEventFromText(
            "Team standup tomorrow at 9 in Room A",
            LlmConfig()
        )

        assertTrue(result.isSuccess)
        val event = result.getOrNull()
        assertEquals("Team Standup", event?.title)
        assertEquals("2026-06-18", event?.date)
        assertEquals("09:00", event?.time)
        assertEquals(30, event?.durationMinutes)
        assertEquals("work", event?.category)
        assertTrue(event?.reminders?.isNotEmpty() == true)
    }

    @Test
    fun `given medical appointment response, extracts medical category correctly`() = runTest {
        val jsonResponse = """{"title":"Dentist Checkup","date":"2026-06-20","time":"14:00","duration_minutes":60,"location":"Dental Clinic","category":"medical","priority":"normal","reminders":[1440,120,30]}"""

        coEvery { llmApi.chatCompletion(any(), any()) } returns LlmChatResponse(
            choices = listOf(LlmChoice(message = LlmResponseMessage(content = jsonResponse)))
        )

        val result = repository.extractEventFromText(
            "Dentist checkup on Saturday at 2pm",
            LlmConfig()
        )

        assertTrue(result.isSuccess)
        assertEquals("medical", result.getOrNull()?.category)
    }

    @Test
    fun `given response without reminders, auto-generates default reminders`() = runTest {
        val jsonResponse = """{"title":"Doctor Visit","date":"2026-06-20","time":"10:00","duration_minutes":30,"location":"","category":"medical","priority":"","reminders":[]}"""

        coEvery { llmApi.chatCompletion(any(), any()) } returns LlmChatResponse(
            choices = listOf(LlmChoice(message = LlmResponseMessage(content = jsonResponse)))
        )

        val result = repository.extractEventFromText("Doctor visit on Saturday", LlmConfig())

        assertTrue(result.isSuccess)
        val event = result.getOrNull()
        assertTrue(event?.reminders?.isNotEmpty() == true)
        assertEquals(listOf(1440, 120, 30), event?.reminders)
    }

    @Test
    fun `given empty LLM response, when extracting event, returns failure`() = runTest {
        coEvery { llmApi.chatCompletion(any(), any()) } returns LlmChatResponse()
        val result = repository.extractEventFromText("test", LlmConfig())
        assertTrue(result.isFailure)
    }
}
