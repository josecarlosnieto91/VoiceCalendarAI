# Quick Start Guide

## 15-minute setup

1. **Clone & open** in Android Studio
2. **Sync Gradle** (auto-downloads dependencies)
3. **Get an API key** from your LLM provider
4. **Run** on emulator or device (API 33+)
5. **Configure** API key in Settings
6. **Tap mic** and say: "Meeting with Ana next Friday at 4pm"

## Build from command line

```bash
# Debug
./gradlew assembleDebug

# Install on connected device
adb install app/build/outputs/apk/debug/app-debug.apk

# Release (requires keystore)
./gradlew assembleRelease
```

## Running tests

```bash
# Unit tests
./gradlew testDebugUnitTest

# Instrumented tests (emulator/device required)
./gradlew connectedDebugAndroidTest

# All tests
./gradlew test
```
