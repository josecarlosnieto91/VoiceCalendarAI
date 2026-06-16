# ContextInferenceEngine - Especificación para feature-ai/

## Interfaz

```kotlin
interface ContextInferenceEngine {
    fun resolveDate(expression: String, locale: Locale): ResolvedDate?
    fun inferDefaultTime(context: String): LocalTime?
    fun suggestTimeFromHistory(expression: String, history: List<CalendarEvent>): LocalTime?
    fun needsConfirmation(expression: String): Boolean
}

data class ResolvedDate(
    val date: LocalDate,
    val confidence: Confidence,
    val originalExpression: String
)

enum class Confidence { HIGH, MEDIUM, LOW }
```

## Resolución de fechas relativas

| Expresión | Resultado | Confianza |
|-----------|-----------|-----------|
| "mañana" | today + 1 | HIGH |
| "pasado mañana" | today + 2 | HIGH |
| "el próximo viernes" | next Friday from today | HIGH |
| "este finde" | next Saturday | MEDIUM |
| "la semana que viene" | today + 7 | MEDIUM |
| "en 3 días" | today + 3 | HIGH |
| "el 15" (sin mes) | 15th of current or next month | LOW → confirmar |
| "el martes" (sin especificar) | next Tuesday | MEDIUM |
| "dentro de dos semanas" | today + 14 | HIGH |

Soporte multi-locale: español, inglés, catalán.

## Inferencia de horas por defecto

```kotlin
private val timeMapping: Map<String, LocalTime> = mapOf(
    "desayuno" to LocalTime.of(9, 0),
    "breakfast" to LocalTime.of(9, 0),
    "comida" to LocalTime.of(14, 0),
    "lunch" to LocalTime.of(13, 0),
    "almuerzo" to LocalTime.of(14, 0),
    "cena" to LocalTime.of(21, 0),
    "dinner" to LocalTime.of(20, 0),
    "merienda" to LocalTime.of(17, 0),
    "snack" to LocalTime.of(16, 0),
    "mañana" to LocalTime.of(9, 0),
    "morning" to LocalTime.of(9, 0),
    "tarde" to LocalTime.of(14, 0),
    "afternoon" to LocalTime.of(14, 0),
    "noche" to LocalTime.of(21, 0),
    "evening" to LocalTime.of(19, 0),
    "mediodía" to LocalTime.of(12, 0),
    "noon" to LocalTime.of(12, 0),
    "medianoche" to LocalTime.of(0, 0),
    "midnight" to LocalTime.of(0, 0)
)
```

## Sugerencia por historial

```kotlin
fun suggestTimeFromHistory(
    expression: String,
    history: List<CalendarEvent>
): LocalTime? {
    // 1. Extraer palabras clave del texto (ej: "dentista", "reunión", "gimnasio")
    // 2. Buscar eventos previos con palabras similares
    // 3. Si hay 3+ ocurrencias, calcular hora modal
    // 4. Devolver LocalTime o null si no hay suficiente histórico
    val keywords = extractKeywords(expression)
    val matches = history.filter { event ->
        keywords.any { event.title.contains(it, ignoreCase = true) }
    }
    if (matches.size < 3) return null
    
    val modalHour = matches.groupBy { it.startTime?.hour }
        .maxByOrNull { it.value.size }
        ?.key
    return modalHour?.let { LocalTime.of(it, 0) }
}
```

## Reglas de confirmación

Una fecha necesita confirmación explícita si:
- Solo se menciona el día del mes sin mes ("el 15")
- La expresión es vaga ("pronto", "en unos días")
- Hay múltiples interpretaciones posibles
- Se refiere a un día ya pasado en el contexto actual

## Tests

- ContextInferenceEngineTest:
  - resolveDate: mañana, pasado mañana, próximo viernes, este finde
  - resolveDate: locale español vs inglés
  - inferDefaultTime: desayuno → 09:00, comida → 14:00, cena → 21:00
  - suggestTimeFromHistory: con 3+ eventos similares
  - suggestTimeFromHistory: con menos de 3 eventos → null
  - needsConfirmation: "el 15" → true, "mañana" → false
