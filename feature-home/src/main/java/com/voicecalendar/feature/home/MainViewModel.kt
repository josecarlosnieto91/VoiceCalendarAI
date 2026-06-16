package com.voicecalendar.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voicecalendar.core.ui.components.VoiceState
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

@HiltViewModel
class MainViewModel @Inject constructor(
    private val speechRepository: SpeechRepository,
    private val processVoiceInputUseCase: ProcessVoiceInputUseCase,
    private val saveCalendarEventUseCase: SaveCalendarEventUseCase,
    private val getUpcomingEventsUseCase: GetUpcomingEventsUseCase,
    private val calendarRepository: CalendarRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init { loadUpcomingEvents() }

    fun onMicToggle() {
        val currentState = _uiState.value.state
        if (currentState == VoiceState.LISTENING) {
            stopListening()
        } else {
            startListening()
        }
    }

    fun onConfirmEvent(event: CalendarEvent) {
        viewModelScope.launch {
            _uiState.update { it.copy(state = VoiceState.SAVING, showEditDialog = false) }
            saveCalendarEventUseCase(event)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            state = VoiceState.COMPLETED,
                            extractedEvent = null,
                            successMessage = "Event saved!",
                            error = null
                        )
                    }
                    loadUpcomingEvents()
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            state = VoiceState.ERROR,
                            error = e.message ?: "Failed to save event"
                        )
                    }
                }
        }
    }

    fun onDismissEditDialog() {
        _uiState.update { it.copy(showEditDialog = false, extractedEvent = null) }
    }

    fun onDismissError() {
        _uiState.update { it.copy(error = null) }
    }

    fun onDismissSuccess() {
        _uiState.update { it.copy(successMessage = null, state = VoiceState.IDLE) }
    }

    fun resetState() {
        _uiState.update { HomeUiState(upcomingEvents = it.upcomingEvents) }
        loadUpcomingEvents()
    }

    private fun startListening() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(state = VoiceState.LISTENING, error = null, successMessage = null)
            }
            speechRepository.startListening().collect { voiceInput: VoiceInput ->
                if (voiceInput.isFinal) {
                    _uiState.update {
                        it.copy(
                            state = VoiceState.PROCESSING,
                            partialText = voiceInput.text
                        )
                    }
                    processVoiceInput(voiceInput.text)
                } else {
                    _uiState.update { it.copy(partialText = voiceInput.text) }
                }
            }
        }
    }

    private fun stopListening() {
        speechRepository.stopListening()
        _uiState.update { it.copy(state = VoiceState.IDLE) }
    }

    private fun processVoiceInput(text: String) {
        viewModelScope.launch {
            processVoiceInputUseCase(text)
                .onSuccess { event ->
                    _uiState.update {
                        it.copy(
                            state = VoiceState.IDLE,
                            extractedEvent = event,
                            showEditDialog = true
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            state = VoiceState.ERROR,
                            error = e.message ?: "Could not parse event"
                        )
                    }
                }
        }
    }

    private fun loadUpcomingEvents() {
        viewModelScope.launch {
            getUpcomingEventsUseCase(5).onSuccess { events ->
                _uiState.update { it.copy(upcomingEvents = events) }
            }
        }
    }
}
