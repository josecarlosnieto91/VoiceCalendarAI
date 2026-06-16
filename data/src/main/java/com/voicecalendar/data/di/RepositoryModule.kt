package com.voicecalendar.data.di

import com.voicecalendar.data.repository.CalendarRepositoryImpl
import com.voicecalendar.data.repository.LlmRepositoryImpl
import com.voicecalendar.data.repository.SettingsRepositoryImpl
import com.voicecalendar.data.repository.SpeechRepositoryImpl
import com.voicecalendar.domain.repository.CalendarRepository
import com.voicecalendar.domain.repository.LlmRepository
import com.voicecalendar.domain.repository.SettingsRepository
import com.voicecalendar.domain.repository.SpeechRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCalendarRepository(
        impl: CalendarRepositoryImpl
    ): CalendarRepository

    @Binds
    @Singleton
    abstract fun bindLlmRepository(
        impl: LlmRepositoryImpl
    ): LlmRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        impl: SettingsRepositoryImpl
    ): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindSpeechRepository(
        impl: SpeechRepositoryImpl
    ): SpeechRepository
}
