package com.voicecalendar.domain.model

/**
 * Represents voice input data from speech recognition.
 */
data class VoiceInput(
    val text: String,
    val isFinal: Boolean = false,
    val confidence: Float = 0f
)
