# VoiceCalendar AI 🎙️📅

**Create calendar events using voice and AI context.**

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
┌──────────────────────────────────────────────────────┐
│                    UI Layer (Compose)                 │
│  ┌──────────┐  ┌──────────┐  ┌───────────────────┐ │
│  │ MainScreen│  │MicButton │  │EditableEventDialog│ │
│  └────┬─────┘  └──────────┘  └───────────────────┘ │
│       │        ViewModel                            │
│  ┌────┴─────────────────────────────────────────┐   │
│  │           MainViewModel                       │   │
│  └────┬─────────────────────────────────────────┘   │
├───────┼──────────────────────────────────────────────┤
│   Domain Layer (Use Cases + Repositories Interfaces) │
│  ┌────┴─────────────────────────────────────────┐   │
│  │  ProcessVoiceInputUseCase                    │   │
│  │  SaveCalendarEventUseCase                    │   │
│  │  GetUpcomingEventsUseCase                    │   │
│  └────┬─────────────────────────────────────────┘   │
├───────┼──────────────────────────────────────────────┤
│   Data Layer (Implementations)                       │
│  ┌────┴─────────────────────────────────────────┐   │
│  │  SpeechRepositoryImpl  │ CalendarRepoImpl    │   │
│  │  LlmRepositoryImpl     │ SettingsRepoImpl    │   │
│  │  Room DB (Cache)       │ DataStore (Prefs)   │   │
│  └──────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────┘
```

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | **Kotlin 2.0** |
| UI | **Jetpack Compose** + Material 3 |
| DI | **Hilt** (Dagger) |
| Architecture | **Clean Architecture** + MVVM |
| Local Storage | **Room** + **DataStore** |
| Networking | **Retrofit** + **OkHttp** |
| Serialization | **Kotlinx Serialization** |
| Speech | **Android SpeechRecognizer API** |
| Calendar | **Android Calendar Provider** |
| Tests | **JUnit 5** + **MockK** + **Turbine** |
| CI/CD | **GitHub Actions** |

## Getting Started

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
├── app/
│   └── src/
│       ├── main/
│       │   ├── java/com/voicecalendar/
│       │   │   ├── core/          # App, DI, Theme, Components, Utils
│       │   │   ├── data/          # Repository implementations, API, DB
│       │   │   ├── domain/        # Models, Use Cases, Repository interfaces
│       │   │   └── feature/       # Feature screens & ViewModels
│       │   └── res/               # Resources (strings, themes, colors)
│       ├── test/                  # Unit tests
│       └── androidTest/           # Instrumentation tests
├── .github/workflows/            # CI/CD pipelines
└── docs/                          # Documentation
```

## License

[MIT License](LICENSE)

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.
