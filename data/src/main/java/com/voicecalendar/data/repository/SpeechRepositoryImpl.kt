package com.voicecalendar.data.repository

import com.voicecalendar.domain.model.VoiceInput
import com.voicecalendar.domain.repository.SpeechRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Placeholder speech repository implementation.
 * In production, this should integrate with Android's SpeechRecognizer.
 */
@Singleton
class SpeechRepositoryImpl @Inject constructor() : SpeechRepository {

    private var isListening = false

    override suspend fun startListening(): Flow<VoiceInput> = callbackFlow {
        isListening = true
        // In production, start SpeechRecognizer here
        trySend(VoiceInput(text = "Listening enabled", isFinal = false))
        awaitClose { isListening = false }
    }

    override fun stopListening() {
        isListening = false
    }

    override suspend fun hasPermission(): Boolean = false

    override suspend fun requestPermission(): Boolean = false
}
