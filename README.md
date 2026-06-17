# VoiceCalendar AI 🎙️📅

**Create calendar events using voice and AI context.**

[![Android Build](https://github.com/josecarlosnieto91/VoiceCalendarAI/actions/workflows/android-build.yml/badge.svg)](https://github.com/josecarlosnieto91/VoiceCalendarAI/actions/workflows/android-build.yml)
[![Lint](https://github.com/josecarlosnieto91/VoiceCalendarAI/actions/workflows/lint.yml/badge.svg)](https://github.com/josecarlosnieto91/VoiceCalendarAI/actions/workflows/lint.yml)
[![Release](https://github.com/josecarlosnieto91/VoiceCalendarAI/actions/workflows/release.yml/badge.svg)](https://github.com/josecarlosnieto91/VoiceCalendarAI/actions/workflows/release.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-purple.svg)](https://kotlinlang.org)
[![API](https://img.shields.io/badge/API-33%2B-brightgreen.svg)](https://developer.android.com)
[![semantic-release](https://img.shields.io/badge/semver-2.0.0-blue)](https://semver.org)
[![Conventional Commits](https://img.shields.io/badge/Conventional%20Commits-1.0.0-yellow.svg)](https://conventionalcommits.org)


VoiceCalendar AI is an Android 13+ native application that converts natural speech into structured calendar events using configurable Large Language Models (LLMs) for intelligent event extraction.

## Features

- 🎤 **Voice-First Interface**: Single-button microphone UI — tap, speak, confirm
- 🤖 **AI-Powered Extraction**: Configurable LLM backend (OpenAI, Anthropic, Ollama, etc.)
- 📋 **Editable Confirmation**: Review and edit parsed events before saving
- 📅 **Native Calendar Integration**: Saves directly to Android Calendar Provider and Google Calendar
- 🔔 **Smart Reminders**: Automatic reminder configuration based on event context
- 🌙 **Material 3 Design**: Modern UI with dynamic colors and dark mode support
- 🌐 **Internationalization**: English, Spanish, Catalan (extensible)
- ⚙️ **Fully Configurable**: API endpoint, model, provider — all configurable in-app

## Architecture

```
┌───────────────────────────────────────────────────────────┐
│                     Presentation Layer                     │
│  ┌────────────┐  ┌────────────┐  ┌────────────────────┐  │
│  │ feature-home│  │feature-voice│ │  feature-settings  │  │
│  │ (MainScreen)│  │(Speech UI) │ │  (Config screen)   │  │
│  └──────┬─────┘  └──────┬─────┘  └────────┬───────────┘  │
│         │               │                  │              │
│  ┌──────┴───────────────┴──────────────────┴───────────┐  │
│  │              feature-ai / feature-calendar          │  │
│  │  (EventExtraction, ContextInference, Calendar CRUD) │  │
│  └──────────────────────┬──────────────────────────────┘  │
├─────────────────────────┼─────────────────────────────────┤
│               Domain Layer (Pure Kotlin)                  │
│  ┌──────────────────────┴──────────────────────────────┐  │
│  │  UseCases · Repositories Interfaces · Models         │  │
│  │  SmartReminderEngine · HabitLearningEngine          │  │
│  └──────────────────────┬──────────────────────────────┘  │
├─────────────────────────┼─────────────────────────────────┤
│                 Data Layer (Android)                      │
│  ┌──────────────────────┴──────────────────────────────┐  │
│  │  Room DB · DataStore · Retrofit · Calendar Provider │  │
│  │  SpeechRecognizer · WorkManager                    │  │
│  └────────────────────────────────────────────────────┘  │
├──────────────────────────────────────────────────────────┤
│                    Core Layer                             │
│  ┌────────────────────────────────────────────────────┐  │
│  │  DI (Hilt) · Theme (Material 3) · Common Comps     │  │
│  │  Navigation · Utils                                │  │
│  └────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────┘
```

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Layer | Technology |
|-------|-----------|
| Language | **Kotlin 2.0** |
| UI | **Jetpack Compose** + **Material 3** |
| DI | **Hilt** (Dagger) |
| Architecture | **Clean Architecture** + **MVVM** + **Repository Pattern** |
| Local Storage | **Room** + **DataStore** |
| Networking | **Retrofit** + **OkHttp** |
| Serialization | **Kotlinx Serialization** |
| Speech | **Android SpeechRecognizer API** + **Whisper.cpp** (local) |
| Calendar | **Android Calendar Provider** + **Google Calendar Sync** |
| Background | **WorkManager** |
| Navigation | **Navigation Compose** |
| Lint | **ktlint** + **detekt** |
| Tests | **JUnit 4** + **MockK** + **Turbine** |
| CI/CD | **GitHub Actions** (lint → build → test → release) |

## Getting Started

## CI Status

[![Android Build](https://github.com/josecarlosnieto91/VoiceCalendarAI/actions/workflows/android-build.yml/badge.svg)](https://github.com/josecarlosnieto91/VoiceCalendarAI/actions/workflows/android-build.yml)
[![Lint](https://github.com/josecarlosnieto91/VoiceCalendarAI/actions/workflows/lint.yml/badge.svg)](https://github.com/josecarlosnieto91/VoiceCalendarAI/actions/workflows/lint.yml)
[![Release](https://github.com/josecarlosnieto91/VoiceCalendarAI/actions/workflows/release.yml/badge.svg)](https://github.com/josecarlosnieto91/VoiceCalendarAI/actions/workflows/release.yml)

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17+
- Android SDK 35+
- A valid API key for your LLM provider

### Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/VoiceCalendarAI.git
   cd VoiceCalendarAI
   ```

2. Open in Android Studio and let Gradle sync.

3. Add your API key in the app settings (first launch) or via DataStore:
   - Settings > AI Provider > API Key

### Build

```bash
# Debug
./gradlew assembleDebug

# Release
./gradlew assembleRelease
```

## Configuration

VoiceCalendarAI supports any OpenAI-compatible API endpoint:

- **OpenAI**: `https://api.openai.com/v1/chat/completions`
- **Ollama** (local): `http://192.168.1.xxx:11434/v1/chat/completions`
- **LM Studio**: `http://localhost:1234/v1/chat/completions`
- **Anthropic** (via proxy): Configurable endpoint

## Project Structure

```
VoiceCalendarAI/
├── app/                    → Application entry point (MainActivity, Manifest)
├── core/                   → Hilt DI, Material 3 Theme, Common Composable Components
├── domain/                 → Pure Kotlin: Models, Use Cases, Repository Interfaces
├── data/                   → Room, Retrofit, DataStore, Calendar Provider
├── feature-home/           → Main screen with mic button, event list, animations
├── feature-voice/          → Speech-to-text (Android API + Whisper), VoiceRecognizer
├── feature-ai/             → EventExtraction, ContextInference, HabitLearning
├── feature-calendar/       → CalendarService, Calendar Provider CRUD, permissions
├── feature-settings/       → LLM config, theme, reminder defaults
├── .github/workflows/      → android-build.yml, lint.yml, release.yml
├── docs/                   → Architecture, diagrams, quickstart guide
├── gradle/                 → Gradle wrapper
└── gradlew                 → Gradle wrapper script
```

## License

[MIT License](LICENSE)

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.
