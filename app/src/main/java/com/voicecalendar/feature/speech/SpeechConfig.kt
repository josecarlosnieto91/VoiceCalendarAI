package com.voicecalendar.feature.speech

/**
 * Speech recognition configuration options.
 */
data class SpeechConfig(
    val language: String = "en-US",
    val maxResults: Int = 1,
    val useOffline: Boolean = false,
    val timeoutSeconds: Int = 10
)

enum class SpeechProvider {
    ANDROID_SPEECH_RECOGNIZER,
    WHISPER_LOCAL
}
