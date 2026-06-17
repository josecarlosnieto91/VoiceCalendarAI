package com.voicecalendar.domain.model

/**
 * Configuration for the LLM (Large Language Model) used for event extraction.
 */
data class LlmConfig(
    val endpointUrl: String = "https://api.openai.com/v1/chat/completions",
    val model: String = "gpt-4o-mini",
    val temperature: Double = 0.1,
    val maxTokens: Int = 500,
    val apiKey: String = ""
)
