package com.voicecalendar.data.repository

import com.voicecalendar.domain.model.LlmConfig
import com.voicecalendar.domain.repository.SettingsRepository
import com.voicecalendar.domain.repository.SettingsSnapshot
import com.voicecalendar.domain.repository.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * In-memory implementation of SettingsRepository for development.
 * In production, this should persist via DataStore Preferences.
 */
@Singleton
class SettingsRepositoryImpl @Inject constructor() : SettingsRepository {

    private val _settings = MutableStateFlow(SettingsSnapshot())
    override fun observeSettings(): Flow<SettingsSnapshot> = _settings.asStateFlow()

    override suspend fun getSettingsSnapshot(): SettingsSnapshot = _settings.value

    override suspend fun updateLlmConfig(config: LlmConfig) {
        _settings.value = _settings.value.copy(llmConfig = config)
    }

    override suspend fun updateThemeMode(mode: ThemeMode) {
        _settings.value = _settings.value.copy(themeMode = mode)
    }

    override suspend fun updateLanguage(language: String) {
        _settings.value = _settings.value.copy(language = language)
    }
}
