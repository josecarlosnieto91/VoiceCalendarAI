package com.voicecalendar.domain.repository

import com.voicecalendar.domain.model.VoiceInput

/**
 * Repository interface for speech recognition.
 */
interface SpeechRepository {
    suspend fun startListening(): kotlinx.coroutines.flow.Flow<VoiceInput>
    fun stopListening()
    suspend fun hasPermission(): Boolean
    suspend fun requestPermission(): Boolean
}
