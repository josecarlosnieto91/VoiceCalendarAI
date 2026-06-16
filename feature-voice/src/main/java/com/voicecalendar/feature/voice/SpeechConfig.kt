package com.voicecalendar.feature.voice

/**
 * Configuration for speech recognition.
 */
data class SpeechConfig(
    val language: String = "en-US",
    val enablePartialResults: Boolean = true,
    val silenceTimeoutMs: Long = 3000
)

/**
 * Defines supported speech recognition engines.
 */
enum class SpeechEngine {
    GOOGLE,
    SYSTEM_DEFAULT
}
