package com.voicecalendar.domain.repository

import com.voicecalendar.domain.model.LlmConfig

/**
 * Repository interface for app settings and user preferences.
 */
interface SettingsRepository {
    suspend fun getSettingsSnapshot(): SettingsSnapshot
    suspend fun updateLlmConfig(config: LlmConfig)
    suspend fun updateThemeMode(mode: ThemeMode)
    suspend fun updateLanguage(language: String)
    fun observeSettings(): kotlinx.coroutines.flow.Flow<SettingsSnapshot>
}

data class SettingsSnapshot(
    val llmConfig: LlmConfig = LlmConfig(),
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val language: String = "en",
    val calendarId: Long? = null
)

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}
