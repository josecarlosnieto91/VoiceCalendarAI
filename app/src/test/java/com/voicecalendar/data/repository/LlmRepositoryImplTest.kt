package com.voicecalendar.data.repository

import com.voicecalendar.data.remote.api.LlmApi
import com.voicecalendar.data.remote.dto.LlmChatResponse
import com.voicecalendar.data.remote.dto.LlmChoice
import com.voicecalendar.data.remote.dto.LlmResponseMessage
import com.voicecalendar.domain.model.LlmConfig
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
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
    fun `given valid LLM response, when extracting event, then returns parsed JSON`() = runTest {
        val jsonResponse = """{"title":"Team Standup","date":"2026-06-18","start_time":"09:00","end_time":"09:30","location":"Room A","reminder_minutes":10,"all_day":false}"""

        coEvery { llmApi.chatCompletion(any(), any()) } returns LlmChatResponse(
            choices = listOf(LlmChoice(message = LlmResponseMessage(content = jsonResponse)))
        )

        val result = repository.extractEventFromText(
            "Team standup tomorrow at 9 in Room A",
            LlmConfig()
        )

        assertTrue(result.isSuccess)
        val event = result.getOrNull()
        assertTrue(event?.title == "Team Standup")
        assertTrue(event?.date == "2026-06-18")
    }

    @Test
    fun `given empty LLM response, when extracting event, then returns failure`() = runTest {
        coEvery { llmApi.chatCompletion(any(), any()) } returns LlmChatResponse()

        val result = repository.extractEventFromText("test", LlmConfig())
        assertTrue(result.isFailure)
    }
}
