# VoiceRecognizer Interface - Especificación para feature-voice/

## Interfaz pública

```kotlin
interface VoiceRecognizer {
    suspend fun startListening(config: VoiceConfig = VoiceConfig())
    suspend fun stopListening()
    fun currentState(): StateFlow<VoiceState>
}

data class VoiceConfig(
    val language: String = "es-ES",
    val useOffline: Boolean = false,
    val timeoutSeconds: Int = 15
)

sealed class VoiceState {
    data object Idle : VoiceState()
    data object RequestingPermission : VoiceState()
    data class Ready(val engine: VoiceEngine) : VoiceState()
    data object Listening : VoiceState()
    data class PartialResult(val text: String, val confidence: Float) : VoiceState()
    data class FinalResult(val text: String, val confidence: Float) : VoiceState()
    data class Error(val message: String, val recoverable: Boolean) : VoiceState()
}

enum class VoiceEngine { ANDROID_SPEECH, WHISPER_LOCAL }
```

## Dos implementaciones

### 1. AndroidSpeechRecognizerVoiceRecognizer
- Usa android.speech.SpeechRecognizer
- Flow via RecognitionListener (partial + final results)
- Fallback a offline si no hay conexión
- Soporte español (es-ES) e inglés (en-US)

### 2. WhisperLocalVoiceRecognizer
- Ejecuta whisper.cpp vía proceso nativo
- Modelo pequeño (~75MB) para Android
- Funciona 100% offline
- Mayor precisión que SpeechRecognizer

## Selección automática del mejor motor

```kotlin
class AutoVoiceEngineSelector @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun selectEngine(config: VoiceConfig): VoiceEngine {
        return if (config.useOffline || !isNetworkAvailable()) {
            if (isWhisperAvailable()) VoiceEngine.WHISPER_LOCAL
            else VoiceEngine.ANDROID_SPEECH
        } else {
            VoiceEngine.ANDROID_SPEECH
        }
    }
}
```

## Eliminación de ruido y normalización
- Limpiar muletillas ("eh", "mmm", "vale", "entonces")
- Normalizar mayúsculas/minúsculas
- Detectar silencios largos como fin de frase

## Tests unitarios
- VoiceRecognizerTest: test de estados (Idle → Listening → FinalResult)
- AutoVoiceEngineSelectorTest: selección según conectividad
- WhisperLocalVoiceRecognizerTest: procesamiento offline

## Integración con Flow
VoiceRecognizer.currentState() emite StateFlow<VoiceState> que el ViewModel observa para actualizar la UI.
