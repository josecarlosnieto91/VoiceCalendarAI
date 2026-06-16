#!/bin/bash
# VoiceCalendarAI - Project Generator
# Generates all project files

PROJECT=/home/josecnr91/.openclaw/workspace/VoiceCalendarAI

write_file() {
    local file="$1"
    local content="$2"
    mkdir -p "$(dirname "$file")"
    echo "$content" > "$file"
    echo "  ✓ $file"
}

echo "=== VoiceCalendarAI Project Generator ==="
echo ""

# ===========================
# ROOT BUILD FILES
# ===========================
echo "--- Root build files ---"

write_file "$PROJECT/settings.gradle.kts" 'pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "VoiceCalendarAI"
include(":app")
'

write_file "$PROJECT/build.gradle.kts" 'plugins {
    id("com.android.application") version "8.5.0" apply false
    id("org.jetbrains.kotlin.android") version "2.0.20" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.20" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.20" apply false
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
    id("com.google.devtools.ksp") version "2.0.20-1.0.25" apply false
}
'

write_file "$PROJECT/gradle.properties" 'org.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8
android.useAndroidX=true
kotlin.code.style=official
android.nonTransitiveRClass=true
'

write_file "$PROJECT/gradle/wrapper/gradle-wrapper.properties" 'distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.8-bin.zip
networkTimeout=10000
validateDistributionUrl=true
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
'

write_file "$PROJECT/local.properties" '## This file must NOT be versioned.
#sdk.dir=/path/to/Android/Sdk
'

# ===========================
# APP MODULE BUILD
# ===========================
echo "--- App module build ---"

write_file "$PROJECT/app/build.gradle.kts" 'plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.voicecalendar"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.voicecalendar"
        minSdk = 33
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
        debug {
            isDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Compose BOM
    val composeBom = platform("androidx.compose:compose-bom:2024.08.00")
    implementation(composeBom)

    // Core
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
    implementation("androidx.activity:activity-compose:1.9.1")

    // Compose UI
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.animation:animation")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Hilt (DI)
    implementation("com.google.dagger:hilt-android:2.51.1")
    ksp("com.google.dagger:hilt-android-compiler:2.51.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Room (local DB)
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // Retrofit + OkHttp (LLM API calls)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-kotlinx-serialization:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Kotlinx Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")

    // DataStore Preferences
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Accompanist (permissions)
    implementation("com.google.accompanist:accompanist-permissions:0.35.0-alpha")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
    testImplementation("io.mockk:mockk:1.13.12")
    testImplementation("app.cash.turbine:turbine:1.1.0")
    testImplementation("androidx.arch.core:core-testing:2.2.0")

    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(composeBom)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
'

write_file "$PROJECT/app/proguard-rules.pro" '# VoiceCalendarAI ProGuard Rules
-keepattributes *Annotation*

# Kotlin Serialization
-keepclassmembers class kotlinx.serialization.json.** { *; }
-keep,includedescriptorclasses class com.voicecalendar.**$$serializer { *; }
-keepclassmembers class com.voicecalendar.** {
    *** Companion;
}
-keepclasseswithmembers class com.voicecalendar.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
'

# ===========================
# ANDROID MANIFEST
# ===========================
echo "--- AndroidManifest ---"

write_file "$PROJECT/app/src/main/AndroidManifest.xml" '<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <!-- Permissions -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.READ_CALENDAR" />
    <uses-permission android:name="android.permission.WRITE_CALENDAR" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

    <application
        android:name=".core.VoiceCalendarApp"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.VoiceCalendar"
        tools:targetApi="35">

        <activity
            android:name=".core.MainActivity"
            android:exported="true"
            android:theme="@style/Theme.VoiceCalendar"
            android:windowSoftInputMode="adjustResize">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

    </application>

</manifest>
'

# ===========================
# RESOURCES
# ===========================
echo "--- Resources ---"

write_file "$PROJECT/app/src/main/res/values/strings.xml" '<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">VoiceCalendar AI</string>
    <string name="mic_button_description">Tap to speak</string>
    <string name="listening">Listening…</string>
    <string name="processing">Processing…</string>
    <string name="save_event">Save Event</string>
    <string name="cancel">Cancel</string>
    <string name="confirm">Confirm</string>
    <string name="edit">Edit</string>
    <string name="delete">Delete</string>
    <string name="title">Title</string>
    <string name="description">Description</string>
    <string name="date">Date</string>
    <string name="time">Time</string>
    <string name="location">Location</string>
    <string name="reminder">Reminder</string>
    <string name="no_events">No upcoming events</string>
    <string name="event_saved">Event saved successfully</string>
    <string name="event_deleted">Event deleted</string>
    <string name="error_permission_mic">Microphone permission required</string>
    <string name="error_permission_calendar">Calendar permission required</string>
    <string name="error_speech_not_available">Speech recognition not available</string>
    <string name="error_llm_unavailable">AI service unavailable. Check your connection.</string>
    <string name="error_parse_event">Could not understand the event. Please try again.</string>
    <string name="settings">Settings</string>
    <string name="settings_llm_provider">AI Provider</string>
    <string name="settings_llm_api_key">API Key</string>
    <string name="settings_llm_model">Model</string>
    <string name="settings_llm_endpoint">Endpoint URL</string>
    <string name="settings_language">Language</string>
    <string name="settings_theme">Theme</string>
    <string name="settings_theme_system">System</string>
    <string name="settings_theme_light">Light</string>
    <string name="settings_theme_dark">Dark</string>
    <string name="settings_default_reminder">Default reminder (minutes)</string>
    <string name="settings_save">Save Settings</string>
    <string name="about">About</string>
    <string name="about_version">Version</string>
    <string name="about_license">MIT License</string>
</resources>
'

write_file "$PROJECT/app/src/main/res/values-es/strings.xml" '<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">VoiceCalendar AI</string>
    <string name="mic_button_description">Toca para hablar</string>
    <string name="listening">Escuchando…</string>
    <string name="processing">Procesando…</string>
    <string name="save_event">Guardar Evento</string>
    <string name="cancel">Cancelar</string>
    <string name="confirm">Confirmar</string>
    <string name="edit">Editar</string>
    <string name="delete">Eliminar</string>
    <string name="title">Título</string>
    <string name="description">Descripción</string>
    <string name="date">Fecha</string>
    <string name="time">Hora</string>
    <string name="location">Ubicación</string>
    <string name="reminder">Recordatorio</string>
    <string name="no_events">No hay eventos próximos</string>
    <string name="event_saved">Evento guardado correctamente</string>
    <string name="event_deleted">Evento eliminado</string>
    <string name="error_permission_mic">Permiso de micrófono requerido</string>
    <string name="error_permission_calendar">Permiso de calendario requerido</string>
    <string name="error_speech_not_available">Reconocimiento de voz no disponible</string>
    <string name="error_llm_unavailable">Servicio de IA no disponible. Verifica tu conexión.</string>
    <string name="error_parse_event">No se pudo entender el evento. Intenta de nuevo.</string>
    <string name="settings">Ajustes</string>
    <string name="settings_llm_provider">Proveedor de IA</string>
    <string name="settings_llm_api_key">Clave API</string>
    <string name="settings_llm_model">Modelo</string>
    <string name="settings_llm_endpoint">URL del endpoint</string>
    <string name="settings_language">Idioma</string>
    <string name="settings_theme">Tema</string>
    <string name="settings_theme_system">Sistema</string>
    <string name="settings_theme_light">Claro</string>
    <string name="settings_theme_dark">Oscuro</string>
    <string name="settings_default_reminder">Recordatorio por defecto (minutos)</string>
    <string name="settings_save">Guardar ajustes</string>
    <string name="about">Acerca de</string>
    <string name="about_version">Versión</string>
    <string name="about_license">Licencia MIT</string>
</resources>
'

write_file "$PROJECT/app/src/main/res/values-ca/strings.xml" '<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">VoiceCalendar AI</string>
    <string name="mic_button_description">Toca per parlar</string>
    <string name="listening">Escoltant…</string>
    <string name="processing">Processant…</string>
    <string name="save_event">Guardar Esdeveniment</string>
    <string name="cancel">Cancel·lar</string>
    <string name="confirm">Confirmar</string>
    <string name="edit">Editar</string>
    <string name="delete">Eliminar</string>
    <string name="title">Títol</string>
    <string name="description">Descripció</string>
    <string name="date">Data</string>
    <string name="time">Hora</string>
    <string name="location">Ubicació</string>
    <string name="reminder">Recordatori</string>
    <string name="no_events">No hi ha esdeveniments propers</string>
    <string name="event_saved">Esdeveniment guardat correctament</string>
    <string name="event_deleted">Esdeveniment eliminat</string>
    <string name="error_permission_mic">Permís de micròfon requerit</string>
    <string name="error_permission_calendar">Permís de calendari requerit</string>
    <string name="error_speech_not_available">Reconeixement de veu no disponible</string>
    <string name="error_llm_unavailable">Servei d\'IA no disponible. Verifica la connexió.</string>
    <string name="error_parse_event">No s\'ha pogut entendre l\'esdeveniment. Torna a intentar-ho.</string>
    <string name="settings">Configuració</string>
    <string name="settings_llm_provider">Proveïdor d\'IA</string>
    <string name="settings_llm_api_key">Clau API</string>
    <string name="settings_llm_model">Model</string>
    <string name="settings_llm_endpoint">URL del endpoint</string>
    <string name="settings_language">Idioma</string>
    <string name="settings_theme">Tema</string>
    <string name="settings_theme_system">Sistema</string>
    <string name="settings_theme_light">Clar</string>
    <string name="settings_theme_dark">Fosc</string>
    <string name="settings_default_reminder">Recordatori per defecte (minuts)</string>
    <string name="settings_save">Guardar configuració</string>
    <string name="about">Quant a</string>
    <string name="about_version">Versió</string>
    <string name="about_license">Llicència MIT</string>
</resources>
'

write_file "$PROJECT/app/src/main/res/values/themes.xml" '<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.VoiceCalendar" parent="android:Theme.Material.Light.NoActionBar">
    </style>
</resources>
'

write_file "$PROJECT/app/src/main/res/values-night/themes.xml" '<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.VoiceCalendar" parent="android:Theme.Material.NoActionBar">
    </style>
</resources>
'

write_file "$PROJECT/app/src/main/res/values/colors.xml" '<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="mic_icon">#1A1A2E</color>
    <color name="mic_icon_night">#E8E8E8</color>
    <color name="mic_background">#E94560</color>
    <color name="mic_background_listening">#0F3460</color>
</resources>
'

# ===========================
# DOMAIN LAYER
# ===========================
echo "--- Domain layer ---"

write_file "$PROJECT/app/src/main/java/com/voicecalendar/domain/model/CalendarEvent.kt" 'package com.voicecalendar.domain.model

import java.time.LocalDate
import java.time.LocalTime

/**
 * Domain entity representing a calendar event extracted from voice input.
 */
data class CalendarEvent(
    val id: String = "",
    val title: String,
    val description: String = "",
    val date: LocalDate? = null,
    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null,
    val location: String = "",
    val reminderMinutes: Int = 15,
    val allDay: Boolean = false,
    val calendarId: Long? = null,
    val eventId: Long? = null,
    val isConfirmed: Boolean = false
)
'

write_file "$PROJECT/app/src/main/java/com/voicecalendar/domain/model/VoiceInput.kt" 'package com.voicecalendar.domain.model

/**
 * Represents raw voice input transcribed to text.
 */
data class VoiceInput(
    val text: String,
    val confidence: Float = 0f,
    val isFinal: Boolean = true
)
'

write_file "$PROJECT/app/src/main/java/com/voicecalendar/domain/model/CalendarEventJson.kt" 'package com.voicecalendar.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * JSON representation of a calendar event for LLM extraction.
 */
@Serializable
data class CalendarEventJson(
    val title: String = "",
    val description: String = "",
    val date: String = "",
    @SerialName("start_time")
    val startTime: String = "",
    @SerialName("end_time")
    val endTime: String = "",
    val location: String = "",
    @SerialName("reminder_minutes")
    val reminderMinutes: Int = 15,
    @SerialName("all_day")
    val allDay: Boolean = false
)
'

write_file "$PROJECT/app/src/main/java/com/voicecalendar/domain/model/LlmConfig.kt" 'package com.voicecalendar.domain.model

/**
 * Configuration for the LLM provider.
 */
data class LlmConfig(
    val provider: String = "openai",
    val apiKey: String = "",
    val model: String = "gpt-4o-mini",
    val endpointUrl: String = "https://api.openai.com/v1/chat/completions",
    val maxTokens: Int = 512,
    val temperature: Float = 0.1f
)
'

write_file "$PROJECT/app/src/main/java/com/voicecalendar/domain/model/AppSettings.kt" 'package com.voicecalendar.domain.model

import java.util.Locale

/**
 * User application settings.
 */
data class AppSettings(
    val llmConfig: LlmConfig = LlmConfig(),
    val locale: Locale = Locale.getDefault(),
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val defaultReminderMinutes: Int = 15
)

enum class ThemeMode { SYSTEM, LIGHT, DARK }
'

write_file "$PROJECT/app/src/main/java/com/voicecalendar/domain/repository/CalendarRepository.kt" 'package com.voicecalendar.domain.repository

import com.voicecalendar.domain.model.CalendarEvent

/**
 * Repository interface for calendar operations.
 */
interface CalendarRepository {
    suspend fun saveEvent(event: CalendarEvent): Result<CalendarEvent>
    suspend fun updateEvent(event: CalendarEvent): Result<CalendarEvent>
    suspend fun deleteEvent(eventId: Long): Result<Unit>
    suspend fun getEvent(eventId: Long): Result<CalendarEvent>
    suspend fun getUpcomingEvents(limit: Int = 10): Result<List<CalendarEvent>>
    suspend fun requestCalendarPermissions(): Boolean
    suspend fun hasCalendarPermissions(): Boolean
}
'

write_file "$PROJECT/app/src/main/java/com/voicecalendar/domain/repository/SpeechRepository.kt" 'package com.voicecalendar.domain.repository

import com.voicecalendar.domain.model.VoiceInput
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for speech-to-text operations.
 */
interface SpeechRepository {
    fun startListening(): Flow<VoiceInput>
    fun stopListening()
    fun isListening(): Boolean
    suspend fun hasSpeechPermission(): Boolean
    suspend fun requestSpeechPermission(): Boolean
}
'

write_file "$PROJECT/app/src/main/java/com/voicecalendar/domain/repository/LlmRepository.kt" 'package com.voicecalendar.domain.repository

import com.voicecalendar.domain.model.CalendarEventJson
import com.voicecalendar.domain.model.LlmConfig

/**
 * Repository interface for LLM-based event extraction.
 */
interface LlmRepository {
    suspend fun extractEventFromText(text: String, config: LlmConfig): Result<CalendarEventJson>
}
'

write_file "$PROJECT/app/src/main/java/com/voicecalendar/domain/repository/SettingsRepository.kt" 'package com.voicecalendar.domain.repository

import com.voicecalendar.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for app settings persistence.
 */
interface SettingsRepository {
    fun getSettings(): Flow<AppSettings>
    suspend fun updateSettings(settings: AppSettings)
    suspend fun getSettingsSnapshot(): AppSettings
}
'

write_file "$PROJECT/app/src/main/java/com/voicecalendar/domain/usecase/ProcessVoiceInputUseCase.kt" 'package com.voicecalendar.domain.usecase

import com.voicecalendar.domain.model.CalendarEvent
import com.voicecalendar.domain.repository.LlmRepository
import com.voicecalendar.domain.repository.SettingsRepository
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * Use case to process raw voice text into a structured CalendarEvent using LLM.
 */
class ProcessVoiceInputUseCase @Inject constructor(
    private val llmRepository: LlmRepository,
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(voiceText: String): Result<CalendarEvent> {
        val settings = settingsRepository.getSettingsSnapshot()
        val llmResult = llmRepository.extractEventFromText(voiceText, settings.llmConfig)

        return llmResult.map { json ->
            CalendarEvent(
                title = json.title.ifBlank { voiceText.take(50) },
                description = json.description,
                date = parseDate(json.date),
                startTime = parseTime(json.startTime),
                endTime = parseTime(json.endTime),
                location = json.location,
                reminderMinutes = json.reminderMinutes,
                allDay = json.allDay
            )
        }
    }

    private fun parseDate(dateStr: String): LocalDate? {
        if (dateStr.isBlank()) return null
        val formats = listOf(
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy")
        )
        for (format in formats) {
            try {
                return LocalDate.parse(dateStr, format)
            } catch (_: Exception) {}
        }
        return null
    }

    private fun parseTime(timeStr: String): LocalTime? {
        if (timeStr.isBlank()) return null
        val formats = listOf(
            DateTimeFormatter.ISO_LOCAL_TIME,
            DateTimeFormatter.ofPattern("HH:mm"),
            DateTimeFormatter.ofPattern("hh:mm a"),
            DateTimeFormatter.ofPattern("h:mm a")
        )
        for (format in formats) {
            try {
                return LocalTime.parse(timeStr.uppercase(), format)
            } catch (_: Exception) {}
        }
        return null
    }
}
'

write_file "$PROJECT/app/src/main/java/com/voicecalendar/domain/usecase/SaveCalendarEventUseCase.kt" 'package com.voicecalendar.domain.usecase

import com.voicecalendar.domain.model.CalendarEvent
import com.voicecalendar.domain.repository.CalendarRepository
import javax.inject.Inject

/**
 * Use case to save a confirmed calendar event.
 */
class SaveCalendarEventUseCase @Inject constructor(
    private val calendarRepository: CalendarRepository
) {
    suspend operator fun invoke(event: CalendarEvent): Result<CalendarEvent> {
        return if (event.eventId != null) {
            calendarRepository.updateEvent(event)
        } else {
            calendarRepository.saveEvent(event)
        }
    }
}
'

write_file "$PROJECT/app/src/main/java/com/voicecalendar/domain/usecase/GetUpcomingEventsUseCase.kt" 'package com.voicecalendar.domain.usecase

import com.voicecalendar.domain.model.CalendarEvent
import com.voicecalendar.domain.repository.CalendarRepository
import javax.inject.Inject

/**
 * Use case to retrieve upcoming calendar events.
 */
class GetUpcomingEventsUseCase @Inject constructor(
    private val calendarRepository: CalendarRepository
) {
    suspend operator fun invoke(limit: Int = 10): Result<List<CalendarEvent>> {
        return calendarRepository.getUpcomingEvents(limit)
    }
}
'

# ===========================
# DATA LAYER
# ===========================
echo "--- Data layer ---"

write_file "$PROJECT/app/src/main/java/com/voicecalendar/data/local/db/VoiceCalendarDatabase.kt" 'package com.voicecalendar.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.voicecalendar.data.local.dao.EventCacheDao
import com.voicecalendar.data.local.entity.EventCacheEntity

@Database(
    entities = [EventCacheEntity::class],
    version = 1,
    exportSchema = true
)
abstract class VoiceCalendarDatabase : RoomDatabase() {
    abstract fun eventCacheDao(): EventCacheDao

    companion object {
        const val DATABASE_NAME = "voicecalendar_cache.db"
    }
}
'

write_file "$PROJECT/app/src/main/java/com/voicecalendar/data/local/entity/EventCacheEntity.kt" 'package com.voicecalendar.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event_cache")
data class EventCacheEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val date: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val location: String = "",
    val reminderMinutes: Int = 15,
    val allDay: Boolean = false,
    val calendarEventId: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
'

write_file "$PROJECT/app/src/main/java/com/voicecalendar/data/local/dao/EventCacheDao.kt" 'package com.voicecalendar.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.voicecalendar.data.local.entity.EventCacheEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventCacheDao {
    @Query("SELECT * FROM event_cache ORDER BY date ASC, startTime ASC")
    fun getAllEvents(): Flow<List<EventCacheEntity>>

    @Query("SELECT * FROM event_cache WHERE id = :id")
    suspend fun getEventById(id: Long): EventCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventCacheEntity): Long

    @Update
    suspend fun updateEvent(event: EventCacheEntity)

    @Delete
    suspend fun deleteEvent(event: EventCacheEntity)

    @Query("DELETE FROM event_cache")
    suspend fun clearAll()
}
'

write_file "$PROJECT/app/src/main/java/com/voicecalendar/data/remote/api/LlmApi.kt" 'package com.voicecalendar.data.remote.api

import com.voicecalendar.data.remote.dto.LlmChatRequest
import com.voicecalendar.data.remote.dto.LlmChatResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

/**
 * Retrofit API interface for configurable LLM providers.
 */
interface LlmApi {
    @POST
    suspend fun chatCompletion(
        @Url endpointUrl: String,
        @Body request: LlmChatRequest
    ): LlmChatResponse
}
'

write_file "$PROJECT/app/src/main/java/com/voicecalendar/data/remote/dto/LlmChatRequest.kt" 'package com.voicecalendar.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LlmChatRequest(
    val model: String,
    val messages: List<LlmMessage>,
    val temperature: Float = 0.1f,
    @SerialName("max_tokens")
    val maxTokens: Int = 512
)

@Serializable
data class LlmMessage(
    val role: String,
    val content: String
)
'

write_file "$PROJECT/app/src/main/java/com/voicecalendar/data/remote/dto/LlmChatResponse.kt" 'package com.voicecalendar.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LlmChatResponse(
    val id: String? = null,
    val choices: List<LlmChoice> = emptyList()
)

@Serializable
data class LlmChoice(
    val index: Int = 0,
    val message: LlmResponseMessage? = null,
    @SerialName("finish_reason")
    val finishReason: String? = null
)

@Serializable
data class LlmResponseMessage(
    val role: String = "assistant",
    val content: String = ""
)
'

write_file "$PROJECT/app/src/main/java/com/voicecalendar/data/repository/CalendarRepositoryImpl.kt" 'package com.voicecalendar.data.repository

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.CalendarContract
import androidx.core.content.ContextCompat
import com.voicecalendar.domain.model.CalendarEvent
import com.voicecalendar.domain.repository.CalendarRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

class CalendarRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : CalendarRepository {

    private val contentResolver: ContentResolver get() = context.contentResolver

    override suspend fun saveEvent(event: CalendarEvent): Result<CalendarEvent> {
        return runCatching {
            val values = ContentValues().apply {
                put(CalendarContract.Events.DTSTART, toEpochMillis(event))
                put(CalendarContract.Events.TITLE, event.title)
                put(CalendarContract.Events.DESCRIPTION, event.description)
                put(CalendarContract.Events.EVENT_LOCATION, event.location)
                put(CalendarContract.Events.CALENDAR_ID, getPrimaryCalendarId())
                put(CalendarContract.Events.EVENT_TIMEZONE, ZoneId.systemDefault().id)

                if (event.allDay) {
                    put(CalendarContract.Events.ALL_DAY, 1)
                }

                if (event.endTime != null) {
                    put(CalendarContract.Events.DTEND, toEndEpochMillis(event))
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_DEFAULT)
                }
            }

            val uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
            val eventId = ContentUris.parseId(uri!!)

            // Add reminder
            addReminder(eventId, event.reminderMinutes)

            event.copy(eventId = eventId, calendarId = getPrimaryCalendarId())
        }
    }

    override suspend fun updateEvent(event: CalendarEvent): Result<CalendarEvent> {
        return runCatching {
            val values = ContentValues().apply {
                put(CalendarContract.Events.DTSTART, toEpochMillis(event))
                put(CalendarContract.Events.TITLE, event.title)
                put(CalendarContract.Events.DESCRIPTION, event.description)
                put(CalendarContract.Events.EVENT_LOCATION, event.location)
                put(CalendarContract.Events.EVENT_TIMEZONE, ZoneId.systemDefault().id)

                if (event.endTime != null) {
                    put(CalendarContract.Events.DTEND, toEndEpochMillis(event))
                }
            }

            val uri = ContentUris.withAppendedId(
                CalendarContract.Events.CONTENT_URI,
                event.eventId ?: return@runCatching event
            )
            contentResolver.update(uri, values, null, null)
            event
        }
    }

    override suspend fun deleteEvent(eventId: Long): Result<Unit> {
        return runCatching {
            val uri = ContentUris.withAppendedId(
                CalendarContract.Events.CONTENT_URI,
                eventId
            )
            contentResolver.delete(uri, null, null)
        }
    }

    override suspend fun getEvent(eventId: Long): Result<CalendarEvent> {
        // Simplified - would require cursor query
        return Result.failure(UnsupportedOperationException("Not implemented"))
    }

    override suspend fun getUpcomingEvents(limit: Int): Result<List<CalendarEvent>> {
        return runCatching {
            val projection = arrayOf(
                CalendarContract.Events._ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DESCRIPTION,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND,
                CalendarContract.Events.EVENT_LOCATION
            )

            val now = System.currentTimeMillis()
            val selection = "${CalendarContract.Events.DTSTART} >= ?"
            val selectionArgs = arrayOf(now.toString())
            val sortOrder = "${CalendarContract.Events.DTSTART} ASC LIMIT $limit"

            val cursor = contentResolver.query(
                CalendarContract.Events.CONTENT_URI,
                projection, selection, selectionArgs, sortOrder
            )

            val events = mutableListOf<CalendarEvent>()
            cursor?.use {
                while (it.moveToNext()) {
                    events.add(
                        CalendarEvent(
                            eventId = it.getLong(0),
                            title = it.getString(1) ?: "",
                            description = it.getString(2) ?: "",
                            location = it.getString(5) ?: ""
                        )
                    )
                }
            }
            events
        }
    }

    override suspend fun requestCalendarPermissions(): Boolean {
        // Handled by system permission dialog in UI layer
        return hasCalendarPermissions()
    }

    override suspend fun hasCalendarPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.READ_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.WRITE_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getPrimaryCalendarId(): Long {
        val projection = arrayOf(CalendarContract.Calendars._ID)
        val cursor = contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            projection, null, null, null
        )
        cursor?.use {
            if (it.moveToFirst()) {
                return it.getLong(0)
            }
        }
        return 1L // Fallback
    }

    private fun addReminder(eventId: Long, minutes: Int) {
        val values = ContentValues().apply {
            put(CalendarContract.Reminders.EVENT_ID, eventId)
            put(CalendarContract.Reminders.MINUTES, minutes)
            put(
                CalendarContract.Reminders.METHOD,
                CalendarContract.Reminders.METHOD_ALERT
            )
        }
        contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, values)
    }

    private fun toEpochMillis(event: CalendarEvent): Long {
        val date = event.date ?: java.time.LocalDate.now()
        val time = event.startTime ?: java.time.LocalTime.of(9, 0)
        return ZonedDateTime.of(date, time, ZoneId.systemDefault()).toEpochSecond() * 1000
    }

    private fun toEndEpochMillis(event: CalendarEvent): Long {
        val date = event.date ?: java.time.LocalDate.now()
        val time = event.endTime ?: event.startTime?.plusHours(1)
            ?: java.time.LocalTime.of(10, 0)
        return ZonedDateTime.of(date, time, ZoneId.systemDefault()).toEpochSecond() * 1000
    }
}
'

write_file "$PROJECT/app/src/main/java/com/voicecalendar/data/repository/SpeechRepositoryImpl.kt" 'package com.voicecalendar.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.core.content.ContextCompat
import com.voicecalendar.domain.model.VoiceInput
import com.voicecalendar.domain.repository.SpeechRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Locale
import javax.inject.Inject

class SpeechRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SpeechRepository {

    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false

    override fun startListening(): Flow<VoiceInput> = callbackFlow {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            send(VoiceInput("", confidence = 0f, isFinal = true))
            close()
            return@callbackFlow
        }

        speechRecognizer?.destroy()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        val listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: android.os.Bundle?) {}

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                isListening = false
            }

            override fun onError(error: Int) {
                isListening = false
                val errorMsg = when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH -> "No speech detected"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Speech timeout"
                    SpeechRecognizer.ERROR_NETWORK -> "Network error"
                    else -> "Recognition error: $error"
                }
                trySend(VoiceInput(errorMsg, confidence = 0f, isFinal = true))
            }

            override fun onResults(results: android.os.Bundle?) {
                isListening = false
                val matches = results?.getStringArrayList(
                    SpeechRecognizer.RESULTS_RECOGNITION
                ) ?: return
                val scores = results?.getFloatArrayList(
                    SpeechRecognizer.CONFIDENCE_SCORES
                )
                if (matches.isNotEmpty()) {
                    trySend(
                        VoiceInput(
                            text = matches[0],
                            confidence = scores?.getOrNull(0) ?: 1f,
                            isFinal = true
                        )
                    )
                }
            }

            override fun onPartialResults(partialResults: android.os.Bundle?) {
                val matches = partialResults?.getStringArrayList(
                    SpeechRecognizer.RESULTS_RECOGNITION
                ) ?: return
                if (matches.isNotEmpty()) {
                    trySend(
                        VoiceInput(
                            text = matches[0],
                            confidence = 0f,
                            isFinal = false
                        )
                    )
                }
            }

            override fun onEvent(eventType: Int, params: android.os.Bundle?) {}
        }

        speechRecognizer?.setRecognitionListener(listener)
        speechRecognizer?.startListening(intent)
        isListening = true

        awaitClose {
            speechRecognizer?.stopListening()
            speechRecognizer?.destroy()
            speechRecognizer = null
            isListening = false
        }
    }

    override fun stopListening() {
        speechRecognizer?.stopListening()
        speechRecognizer?.destroy()
        speechRecognizer = null
        isListening = false
    }

    override fun isListening(): Boolean = isListening

    override suspend fun hasSpeechPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    override suspend fun requestSpeechPermission(): Boolean {
        // Handled by UI layer via system dialog
        return hasSpeechPermission()
    }
}
'

write_file "$PROJECT/app/src/main/java/com/voicecalendar/data/repository/LlmRepositoryImpl.kt" 'package com.voicecalendar.data.repository

import com.voicecalendar.data.remote.api.LlmApi
import com.voicecalendar.data.remote.dto.LlmChatRequest
import com.voicecalendar.data.remote.dto.LlmMessage
import com.voicecalendar.domain.model.CalendarEventJson
import com.voicecalendar.domain.model.LlmConfig
import com.voicecalendar.domain.repository.LlmRepository
import kotlinx.serialization.json.Json
import javax.inject.Inject

class LlmRepositoryImpl @Inject constructor(
    private val llmApi: LlmApi
) : LlmRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    override suspend fun extractEventFromText(
        text: String,
        config: LlmConfig
    ): Result<CalendarEventJson> {
        return runCatching {
            val systemPrompt = buildString {
                appendLine("You are a calendar event parser. Extract event details from natural language.")
                appendLine("Return ONLY valid JSON without markdown wrapping. Use this exact structure:")
                appendLine("""{"title":"","description":"","date":"YYYY-MM-DD","start_time":"HH:mm","end_time":"HH:mm","location":"","reminder_minutes":15,"all_day":false}""")
                appendLine()
                appendLine("Rules:")
                appendLine("- Title is required. If unclear, summarize in max 5 words.")
                appendLine("- Date: if relative (\"tomorrow\", \"next monday\"), use the actual date.")
                appendLine("- Time: 24h format. Infer end_time as start+1h if not specified.")
                appendLine("- If no specific time, set all_day=true and omit start_time/end_time.")
                appendLine("- reminder_minutes: 0=none, 10, 15, 30, 60, 1440 (1 day)")
                appendLine("- Location: only if explicitly mentioned.")
            }

            val request = LlmChatRequest(
                model = config.model,
                messages = listOf(
                    LlmMessage(role = "system", content = systemPrompt),
                    LlmMessage(role = "user", content = text)
                ),
                temperature = config.temperature,
                maxTokens = config.maxTokens
            )

            val response = llmApi.chatCompletion(config.endpointUrl, request)
            val content = response.choices.firstOrNull()?.message?.content
                ?: throw IllegalStateException("Empty LLM response")

            // Strip markdown code fences if present
            val cleaned = content
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            json.decodeFromString<CalendarEventJson>(cleaned)
        }
    }
}
'

write_file "$PROJECT/app/src/main/java/com/voicecalendar/data/repository/SettingsRepositoryImpl.kt" 'package com.voicecalendar.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.voicecalendar.domain.model.AppSettings
import com.voicecalendar.domain.model.LlmConfig
import com.voicecalendar.domain.model.ThemeMode
import com.voicecalendar.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "voice_calendar_settings"
)

class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {

    companion object {
        private val KEY_LLM_PROVIDER = stringPreferencesKey("llm_provider")
        private val KEY_LLM_API_KEY = stringPreferencesKey("llm_api_key")
        private val KEY_LLM_MODEL = stringPreferencesKey("llm_model")
        private val KEY_LLM_ENDPOINT = stringPreferencesKey("llm_endpoint")
        private val KEY_LLM_MAX_TOKENS = intPreferencesKey("llm_max_tokens")
        private val KEY_LLM_TEMPERATURE = floatPreferencesKey("llm_temperature")
        private val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        private val KEY_DEFAULT_REMINDER = intPreferencesKey("default_reminder")
        private val KEY_LOCALE = stringPreferencesKey("locale")
    }

    override fun getSettings(): Flow<AppSettings> {
        return context.dataStore.data.map { prefs ->
            AppSettings(
                llmConfig = LlmConfig(
                    provider = prefs[KEY_LLM_PROVIDER] ?: "openai",
                    apiKey = prefs[KEY_LLM_API_KEY] ?: "",
                    model = prefs[KEY_LLM_MODEL] ?: "gpt-4o-mini",
                    endpointUrl = prefs[KEY_LLM_ENDPOINT]
                        ?: "https://api.openai.com/v1/chat/completions",
                    maxTokens = prefs[KEY_LLM_MAX_TOKENS] ?: 512,
                    temperature = prefs[KEY_LLM_TEMPERATURE] ?: 0.1f
                ),
                themeMode = ThemeMode.valueOf(
                    prefs[KEY_THEME_MODE] ?: ThemeMode.SYSTEM.name
                ),
                defaultReminderMinutes = prefs[KEY_DEFAULT_REMINDER] ?: 15,
                locale = prefs[KEY_LOCALE]?.let { Locale.forLanguageTag(it) }
                    ?: Locale.getDefault()
            )
        }
    }

    override suspend fun updateSettings(settings: AppSettings) {
        context.dataStore.edit { prefs ->
            prefs[KEY_LLM_PROVIDER] = settings.llmConfig.provider
            prefs[KEY_LLM_API_KEY] = settings.llmConfig.apiKey
            prefs[KEY_LLM_MODEL] = settings.llmConfig.model
            prefs[KEY_LLM_ENDPOINT] = settings.llmConfig.endpointUrl
            prefs[KEY_LLM_MAX_TOKENS] = settings.llmConfig.maxTokens
            prefs[KEY_LLM_TEMPERATURE] = settings.llmConfig.temperature
            prefs[KEY_THEME_MODE] = settings.themeMode.name
            prefs[KEY_DEFAULT_REMINDER] = settings.defaultReminderMinutes
            prefs[KEY_LOCALE] = settings.locale.toLanguageTag()
        }
    }

    override suspend fun getSettingsSnapshot(): AppSettings {
        var settings = AppSettings()
        context.dataStore.data.collect { prefs ->
            settings = AppSettings(
                llmConfig = LlmConfig(
                    provider = prefs[KEY_LLM_PROVIDER] ?: "openai",
                    apiKey = prefs[KEY_LLM_API_KEY] ?: "",
                    model = prefs[KEY_LLM_MODEL] ?: "gpt-4o-mini",
                    endpointUrl = prefs[KEY_LLM_ENDPOINT]
                        ?: "https://api.openai.com/v1/chat/completions",
                    maxTokens = prefs[KEY_LLM_MAX_TOKENS] ?: 512,
                    temperature = prefs[KEY_LLM_TEMPERATURE] ?: 0.1f
                ),
                themeMode = ThemeMode.valueOf(
                    prefs[KEY_THEME_MODE] ?: ThemeMode.SYSTEM.name
                ),
                defaultReminderMinutes = prefs[KEY_DEFAULT_REMINDER] ?: 15,
                locale = prefs[KEY_LOCALE]?.let { Locale.forLanguageTag(it) }
                    ?: Locale.getDefault()
            )
            return@collect
        }
        return settings
    }
}
'

# ===========================
# CORE / DI / UI / UTIL
# ===========================
echo "--- Core / DI / UI / Util ---"

write_file "$PROJECT/app/src/main/java/com/voicecalendar/core/VoiceCalendarApp.kt" 'package com.voicecalendar.core

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VoiceCalendarApp : Application()
'

write_file "$PROJECT/app/src/main/java/com/voicecalendar/core/MainActivity.kt" 'package com.voicecalendar.core

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.voicecalendar.core.ui.VoiceCalendarTheme
import com.voicecalendar.feature.main.MainScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VoiceCalendarTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen()
                }
            }
        }
    }
}
'

write_file "$PROJECT/app/src/main/java/com/voicecalendar/core/ui/theme/Color.kt" 'package com.voicecalendar.core.ui.theme

import androidx.compose.ui.graphics.Color

// Light Theme Colors
val PrimaryLight = Color(0xFF1A73E8)
val OnPrimaryLight = Color(0xFFFFFFFF)
val PrimaryContainerLight = Color(0xFFD3E3FD)
val OnPrimaryContainerLight = Color(0xFF041E49)

val SecondaryLight = Color(0xFF5F6368)
val OnSecondaryLight = Color(0xFFFFFFFF)
val SecondaryContainerLight = Color(0xFFE8EAED)
val OnSecondaryContainerLight = Color(0xFF202124)

val TertiaryLight = Color(0xFF188038)
val OnTertiaryLight = Color(0xFFFFFFFF)

val BackgroundLight = Color(0xFFF8F9FA)
val OnBackgroundLight = Color(0xFF202124)
val SurfaceLight = Color(0xFFFFFFFF)
val OnSurfaceLight = Color(0xFF202124)
val SurfaceVariantLight = Color(0xFFF1F3F4)

val ErrorLight = Color(0xFFD93025)
val OnErrorLight = Color(0xFFFFFFFF)

// Dark Theme Colors
val PrimaryDark = Color(0xFF8AB4F8)
val OnPrimaryDark = Color(0xFF062E6F)
val PrimaryContainerDark = Color(0xFF0842A0)
val OnPrimaryContainerDark = Color(0xFFD3E3FD)

val SecondaryDark = Color(0xFFBDC1C6)
val OnSecondaryDark = Color(0xFF303134)
val SecondaryContainerDark = Color(0xFF3C4043)
val OnSecondaryContainerDark = Color(0xFFE8EAED)

val TertiaryDark = Color(0xFF81C995)
val OnTertiaryDark = Color(0xFF0D652D)

val BackgroundDark = Color(0xFF202124)
val OnBackgroundDark = Color(0xFFE8EAED)
val SurfaceDark = Color(0xFF292A2D)
val OnSurfaceDark = Color(0xFFE8EAED)
val SurfaceVariantDark = Color(0xFF303134)

val ErrorDark = Color(0xFFF28B82)
val OnErrorDark = Color(0xFF601410)

// Voice Recorder Colors
val MicButtonDefault = Color(0xFFE94560)
val MicButtonListening = Color(0xFF0F3460)
val MicButtonPulse = Color(0xFF533483)
'

write_file "$PROJECT/app/src/main/java/com/voicecalendar/core/ui/theme/Theme.kt" 'package com.voicecalendar.core.ui

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.voicecalendar.core.ui.theme.*

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = OnPrimaryContainerLight,
    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    secondaryContainer = SecondaryContainerLight,
    onSecondaryContainer = OnSecondaryContainerLight,
    tertiary = TertiaryLight,
    onTertiary = OnTertiaryLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    error = ErrorLight,
    onError = OnErrorLight
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,
    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    error = ErrorDark,
    onError = OnErrorDark
)

@Composable
fun VoiceCalendarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
'

write_file "$PROJECT/app/src/main/java/com/voicecalendar/core/ui/theme/Type.kt" 'package com.voicecalendar.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
'

write_file "$PROJECT/app/src/main/java/com/voicecalendar/core/util/DateUtils.kt" 'package com.voicecalendar.core.util

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

object DateUtils {
    fun formatDate(date: LocalDate?): String {
        if (date == null) return ""
        return date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
    }

    fun formatTime(time: LocalTime?): String {
        if (time == null) return ""
        return time.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
    }

    fun formatDateTime(date: LocalDate?, time: LocalTime?): String {
        if (date == null) return ""
        val dateStr = formatDate(date)
        val timeStr = if (time != null) " at ${formatTime(time)}" else ""
        return "$dateStr$timeStr"
    }
}
'

write_file "$PROJECT/app/src/main/java/com/voicecalendar/core/di/AppModule.kt" 'package com.voicecalendar.core.di

import android.content.Context
import com.voicecalendar.data.local.db.VoiceCalendarDatabase
import com.voicecalendar.data.remote.api.LlmApi
import com.voicecalendar.data.repository.CalendarRepositoryImpl
import com.voicecalendar.data.repository.LlmRepositoryImpl
import com.voicecalendar.data.repository.SettingsRepositoryImpl
import com.voicecalendar.data.repository.SpeechRepositoryImpl
import com.voicecalendar.domain.repository.CalendarRepository
import com.voicecalendar.domain.repository.LlmRepository
import com.voicecalendar.domain.repository.SettingsRepository
import com.voicecalendar.domain.repository.SpeechRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideLlmApi(okHttpClient: OkHttpClient): LlmApi {
        // Dynamic base URL - we use @Url parameter in the API interface
        return Retrofit.Builder()
            .baseUrl("https://api.openai.com/v1/") // Default, overridden by @Url
            .client(okHttpClient)
            .addConverterFactory(
                kotlinx.serialization.json.Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                }.asConverterFactory("application/json".toMediaType())
            )
            .build()
            .create(LlmApi::class.java)
    }

    @Provides
    @Singleton
    fun provideVoiceCalendarDatabase(
        @ApplicationContext context: Context
    ): VoiceCalendarDatabase {
        return androidx.room.Room.databaseBuilder(
            context,
            VoiceCalendarDatabase::class.java,
            VoiceCalendarDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideCalendarRepository(
        @ApplicationContext context: Context
    ): CalendarRepository {
        return CalendarRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideSpeechRepository(
        @ApplicationContext context: Context
    ): SpeechRepository {
        return SpeechRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideLlmRepository(
        llmApi: LlmApi
    ): LlmRepository {
        return LlmRepositoryImpl(llmApi)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(
        @ApplicationContext context: Context
    ): SettingsRepository {
        return SettingsRepositoryImpl(context)
    }
}
'

write_file "$PROJECT/app/src/main/java/com/voicecalendar/core/ui/components/MicButton.kt" 'package com.voicecalendar.core.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.voicecalendar.core.ui.theme.MicButtonDefault
import com.voicecalendar.core.ui.theme.MicButtonListening

@Composable
fun MicButton(
    isListening: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String = "Toggle microphone"
) {
    val scale by animateFloatAsState(
        targetValue = if (isListening) 1.15f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = "micScale"
    )

    val backgroundColor = if (isListening) MicButtonListening else MicButtonDefault

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(80.dp)
    ) {
        IconButton(
            onClick = onToggle,
            modifier = Modifier
                .size(72.dp)
                .scale(scale)
                .background(backgroundColor, CircleShape),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = backgroundColor
            )
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = contentDescription,
                tint = androidx.compose.ui.graphics.Color.White,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}
'

write_file "$PROJECT/app/src/main/java/com/voicecalendar/core/ui/components/EventCard.kt" 'package com.voicecalendar.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.voicecalendar.domain.model.CalendarEvent

@Composable
fun EventCard(
    event: CalendarEvent,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (event.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (event.date != null || event.startTime != null) {
                    InfoChip(
                        icon = if (event.date != null) Icons.Outlined.CalendarMonth
                               else Icons.Outlined.Schedule,
                        text = buildString {
                            event.date?.let { append(com.voicecalendar.core.util.DateUtils.formatDate(it)) }
                            if (event.startTime != null) {
                                append(" ")
                                append(com.voicecalendar.core.util.DateUtils.formatTime(event.startTime))
                            }
                        }
                    )
                }

                if (event.location.isNotBlank()) {
                    Spacer(modifier = Modifier.width(12.dp))
                    InfoChip(
                        icon = Icons.Outlined.LocationOn,
                        text = event.location
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoChip(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
'

write_file "$PROJECT/app/src/main/java/com/voicecalendar/core/ui/components/EditableEventDialog.kt" 'package com.voicecalendar.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.voicecalendar.domain.model.CalendarEvent
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditableEventDialog(
    event: CalendarEvent,
    onConfirm: (CalendarEvent) -> Unit,
    onDismiss: () -> Unit
) {
    var editedEvent by remember { mutableStateOf(event) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = event.date?.atStartOfDay(ZoneId.systemDefault())
                ?.toInstant()?.toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        editedEvent = editedEvent.copy(
                            date = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        )
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val startHour = event.startTime?.hour ?: 9
        val startMinute = event.startTime?.minute ?: 0
        val timeState = rememberTimePickerState(
            initialHour = startHour,
            initialMinute = startMinute,
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Select Time") },
            text = { TimePicker(state = timeState) },
            confirmButton = {
                TextButton(onClick = {
                    editedEvent = editedEvent.copy(
                        startTime = LocalTime.of(timeState.hour, timeState.minute)
                    )
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Review Event") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = editedEvent.title,
                    onValueChange = { editedEvent = editedEvent.copy(title = it) },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = editedEvent.description,
                    onValueChange = { editedEvent = editedEvent.copy(description = it) },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            editedEvent.date?.let { com.voicecalendar.core.util.DateUtils.formatDate(it) }
                                ?: "Select Date"
                        )
                    }

                    OutlinedButton(
                        onClick = { showTimePicker = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            editedEvent.startTime?.let { com.voicecalendar.core.util.DateUtils.formatTime(it) }
                                ?: "Select Time"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = editedEvent.location,
                    onValueChange = { editedEvent = editedEvent.copy(location = it) },
                    label = { Text("Location") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = editedEvent.reminderMinutes.toString(),
                    onValueChange = {
                        editedEvent = editedEvent.copy(
                            reminderMinutes = it.toIntOrNull() ?: 15
                        )
                    },
                    label = { Text("Reminder (minutes)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(editedEvent) }) {
                Text("Save Event")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
'

# ===========================
# FEATURE LAYER
# ===========================
echo "--- Feature layer ---"

write_file "$PROJECT/app/src/main/java/com/voicecalendar/feature/main/MainViewModel.kt" 'package com.voicecalendar.feature.main

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

    init {
        loadUpcomingEvents()
    }

    fun onMicToggle() {
        if (_uiState.value.isListening) {
            stopListening()
        } else {
            startListening()
        }
    }

    fun onConfirmEvent(event: CalendarEvent) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, showEditDialog = false) }
            val result = saveCalendarEventUseCase(event)
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        extractedEvent = null,
                        successMessage = "Event saved!",
                        error = null
                    )
                }
                loadUpcomingEvents()
            }.onFailure { e ->
                _uiState.update {
                    it.copy(
                        isProcessing = false,
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
        _uiState.update { it.copy(successMessage = null) }
    }

    private fun startListening() {
        if (!checkPermissions()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isListening = true, error = null) }

            speechRepository.startListening().collect { voiceInput: VoiceInput ->
                if (voiceInput.isFinal) {
                    _uiState.update {
                        it.copy(
                            isListening = false,
                            isProcessing = true,
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
        _uiState.update { it.copy(isListening = false) }
    }

    private fun processVoiceInput(text: String) {
        viewModelScope.launch {
            val result = processVoiceInputUseCase(text)
            result.onSuccess { event ->
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        extractedEvent = event,
                        showEditDialog = true
                    )
                }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        error = e.message ?: "Could not parse event"
                    )
                }
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

    private fun checkPermissions(): Boolean {
        // Permissions are handled via Accompanist at the UI layer
        return true
    }
}
'

write_file "$PROJECT/app/src/main/java/com/voicecalendar/feature/main/MainScreen.kt" 'package com.voicecalendar.feature.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.voicecalendar.core.ui.components.EditableEventDialog
import com.voicecalendar.core.ui.components.EventCard
import com.voicecalendar.core.ui.components.MicButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onDismissError()
        }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onDismissSuccess()
        }
    }

    if (uiState.showEditDialog && uiState.extractedEvent != null) {
        EditableEventDialog(
            event = uiState.extractedEvent!!,
            onConfirm = viewModel::onConfirmEvent,
            onDismiss = viewModel::onDismissEditDialog
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "VoiceCalendar AI",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    IconButton(onClick = { /* Navigate to settings */ }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Mic button section
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MicButton(
                        isListening = uiState.isListening,
                        onToggle = viewModel::onMicToggle
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AnimatedVisibility(
                        visible = uiState.isListening,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Text(
                            text = "Listening...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    AnimatedVisibility(
                        visible = uiState.isProcessing,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                            Text(
                                text = "Processing...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    if (uiState.partialText.isNotBlank() && uiState.isListening) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.partialText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                }
            }

            // Upcoming events section
            if (uiState.upcomingEvents.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Upcoming Events",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(uiState.upcomingEvents) { event ->
                        EventCard(
                            event = event,
                            onClick = { /* View/edit event detail */ }
                        )
                    }
                }
            }
        }
    }
}
'

echo ""
echo "=== Project generated successfully! ==="
echo "Total files created. Ready for git init."
