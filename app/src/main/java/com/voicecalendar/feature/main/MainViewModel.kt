package com.voicecalendar.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voicecalendar.domain.model.CalendarEvent
import com.voicecalendar.domain.model.VoiceInput
import com.voicecalendar.domain.repository.CalendarRepository
import com.voicecalendar.domain.repository.SettingsRepository
import com.voicecalendar.domain.repository.SpeechRepository
import com.voicecalendar.domain.usecase.GetUpcomingEventsUseCase
import com.voicecalendar.domain.usecase.ProcessVoiceInputUseCase
import com.voicecalendar.domain.usecase.SaveCalendarEventUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainUiState(
    val isListening: Boolean = false,
    val isProcessing: Boolean = false,
    val partialText: String = "",
    val extractedEvent: CalendarEvent? = null,
    val upcomingEvents: List<CalendarEvent> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null,
    val showEditDialog: Boolean = false
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val speechRepository: SpeechRepository,
    private val processVoiceInputUseCase: ProcessVoiceInputUseCase,
    private val saveCalendarEventUseCase: SaveCalendarEventUseCase,
    private val getUpcomingEventsUseCase: GetUpcomingEventsUseCase,
    private val calendarRepository: CalendarRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init { loadUpcomingEvents() }

    fun onMicToggle() {
        if (_uiState.value.isListening) stopListening() else startListening()
    }

    fun onConfirmEvent(event: CalendarEvent) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, showEditDialog = false) }
            saveCalendarEventUseCase(event).onSuccess {
                _uiState.update { it.copy(isProcessing = false, extractedEvent = null, successMessage = "Event saved!", error = null) }
                loadUpcomingEvents()
            }.onFailure { e ->
                _uiState.update { it.copy(isProcessing = false, error = e.message ?: "Failed to save event") }
            }
        }
    }

    fun onDismissEditDialog() { _uiState.update { it.copy(showEditDialog = false, extractedEvent = null) } }
    fun onDismissError() { _uiState.update { it.copy(error = null) } }
    fun onDismissSuccess() { _uiState.update { it.copy(successMessage = null) } }

    private fun startListening() {
        viewModelScope.launch {
            _uiState.update { it.copy(isListening = true, error = null) }
            speechRepository.startListening().collect { voiceInput: VoiceInput ->
                if (voiceInput.isFinal) {
                    _uiState.update { it.copy(isListening = false, isProcessing = true, partialText = voiceInput.text) }
                    processVoiceInput(voiceInput.text)
                } else {
                    _uiState.update { it.copy(partialText = voiceInput.text) }
                }
            }
        }
    }

    private fun stopListening() {
        speechRepository.stopListening()
        _uiState.update { it.copy(isListening = false) }
    }

    private fun processVoiceInput(text: String) {
        viewModelScope.launch {
            processVoiceInputUseCase(text).onSuccess { event ->
                _uiState.update { it.copy(isProcessing = false, extractedEvent = event, showEditDialog = true) }
            }.onFailure { e ->
                _uiState.update { it.copy(isProcessing = false, error = e.message ?: "Could not parse event") }
            }
        }
    }

    private fun loadUpcomingEvents() {
        viewModelScope.launch {
            getUpcomingEventsUseCase(10).onSuccess { events ->
                _uiState.update { it.copy(upcomingEvents = events) }
            }
        }
    }
}
