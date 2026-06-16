# Architecture Documentation

## Overview

VoiceCalendarAI follows **Clean Architecture** with **MVVM** in the presentation layer.

### Layers

1. **Domain Layer**: Business logic (Use Cases), domain models, and repository interfaces.
   - Pure Kotlin, no Android dependencies
   - Contains: `model/`, `repository/`, `usecase/`

2. **Data Layer**: Implementations of repository interfaces.
   - Android-specific (ContentResolver, SpeechRecognizer, Room, DataStore, Retrofit)
   - Contains: `local/`, `remote/`, `repository/`

3. **Presentation Layer (UI)**: Jetpack Compose screens with ViewModels.
   - `feature/main/` — Main screen with mic button and event list
   - ViewModel holds StateFlow<UiState> for reactive UI updates

### Data Flow

```
Voice Input
    ↓
SpeechRecognizer → SpeechRepository → Flow<VoiceInput>
    ↓
ProcessVoiceInputUseCase → LlmRepository → CalendarEventJson
    ↓
EditableEventDialog (confirmation) → CalendarEvent
    ↓
SaveCalendarEventUseCase → CalendarRepository → Calendar Provider
```

## Dependency Injection

All dependencies are provided via Hilt modules in `core/di/AppModule.kt` using `@Singleton` scope.

## State Management

Each feature has a ViewModel with a single `StateFlow<UiState>` data class. The Compose UI collects this state and reacts to changes.

## Testing Strategy

- **Unit Tests**: Use cases and repository implementations with MockK
- **Integration Tests**: Room DAOs with in-memory database
- **UI Tests**: Compose UI tests with Compose Test Framework
