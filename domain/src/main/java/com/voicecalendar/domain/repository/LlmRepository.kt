package com.voicecalendar.domain.repository

import com.voicecalendar.domain.model.CalendarEventJson
import com.voicecalendar.domain.model.LlmConfig

/**
 * Repository interface for LLM-based event extraction from natural language.
 */
interface LlmRepository {
    suspend fun extractEventFromText(text: String, config: LlmConfig): Result<CalendarEventJson>
}
