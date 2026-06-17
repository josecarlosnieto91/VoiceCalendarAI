package com.voicecalendar.data.remote.api

import com.voicecalendar.data.remote.dto.LlmChatRequest
import com.voicecalendar.data.remote.dto.LlmChatResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

/**
 * Retrofit API interface for LLM chat completions.
 */
interface LlmApi {
    @POST
    suspend fun chatCompletion(
        @Url url: String,
        @Body request: LlmChatRequest
    ): LlmChatResponse
}
