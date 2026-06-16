package com.voicecalendar.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LlmChatRequest(
    val model: String,
    val messages: List<LlmMessage>,
    val temperature: Double = 0.1,
    @SerialName("max_tokens")
    val maxTokens: Int = 500
)

@Serializable
data class LlmMessage(
    val role: String,
    val content: String
)

@Serializable
data class LlmChatResponse(
    val choices: List<LlmChoice> = emptyList()
)

@Serializable
data class LlmChoice(
    val message: LlmResponseMessage = LlmResponseMessage()
)

@Serializable
data class LlmResponseMessage(
    val content: String = ""
)
