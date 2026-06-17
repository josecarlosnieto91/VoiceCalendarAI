# EventExtractionService - Especificación para feature-ai/

## Interfaz pública

```kotlin
interface EventExtractionService {
    suspend fun extractEvent(text: String): Result<CalendarEventJson>
    fun supportedProviders(): List<AiProvider>
}

data class AiProvider(
    val id: String,          // "openai", "openrouter", "ollama", "local"
    val displayName: String,
    val requiresApiKey: Boolean,
    val defaultEndpoint: String,
    val defaultModel: String
)

// Configurable per provider
data class ExtractionConfig(
    val provider: AiProvider,
    val apiKey: String = "",
    val model: String = "",
    val endpoint: String = "",
    val temperature: Float = 0.1f,
    val maxRetries: Int = 3
)
```

## Validación de esquema

```kotlin
object EventSchemaValidator {
    private val requiredFields = setOf("title")
    private val validCategories = setOf("medical", "work", "travel", "social", "birthday", "administrative", "other")
    private val validPriorities = setOf("low", "normal", "high", "urgent")

    fun validate(json: CalendarEventJson): ValidationResult {
        val errors = mutableListOf<String>()

        if (json.title.isBlank()) errors.add("title is required")
        if (json.title.length > 100) errors.add("title too long")
        if (json.category.isNotBlank() && json.category !in validCategories) {
            errors.add("invalid category: ${json.category}")
        }
        if (json.priority.isNotBlank() && json.priority !in validPriorities) {
            errors.add("invalid priority: ${json.priority}")
        }
        if (json.durationMinutes !in 15..1440) {
            errors.add("duration must be 15-1440 minutes")
        }
        if (json.reminders.any { it !in 0..10080 }) {
            errors.add("reminders must be 0-10080 minutes")
        }

        return if (errors.isEmpty()) ValidationResult.Valid
        else ValidationResult.Invalid(errors)
    }
}
```

## Estrategia plug-in

Cada proveedor implementa:

```kotlin
interface ExtractionStrategy {
    val provider: AiProvider
    suspend fun extract(text: String, config: ExtractionConfig): Result<CalendarEventJson>
}

class OpenAIStrategy : ExtractionStrategy { ... }
class OpenRouterStrategy : ExtractionStrategy { ... }
class OllamaStrategy : ExtractionStrategy { ... }
class LocalModelStrategy : ExtractionStrategy { ... }
```

## Reintentos con backoff

```kotlin
class RetryingExtractionService(
    private val delegate: EventExtractionService,
    private val maxRetries: Int = 3
) : EventExtractionService {
    override suspend fun extractEvent(text: String): Result<CalendarEventJson> {
        repeat(maxRetries) { attempt ->
            val result = delegate.extractEvent(text)
            if (result.isSuccess) {
                val json = result.getOrThrow()
                val validation = EventSchemaValidator.validate(json)
                if (validation is ValidationResult.Valid) {
                    return Result.success(json)
                }
            }
            if (attempt < maxRetries - 1) {
                delay(1000L * (attempt + 1)) // Exponential-ish backoff
            }
        }
        return Result.failure(EventExtractionException("Failed after $maxRetries attempts"))
    }
}
```

## Tests unitarios
- EventSchemaValidatorTest: pruebas de validación
- OpenAIStrategyTest: mock HTTP response
- RetryingExtractionServiceTest: reintentos en fallo
- Integration: proveedor → validación → CalendarEventJson
