# HabitLearningEngine - Especificación para feature-ai/

## Interfaz

```kotlin
interface HabitLearningEngine {
    /** Register that a reminder was dismissed (user ignored it) */
    fun recordDismissedReminder(event: CalendarEvent, reminderMinutes: Int)
    
    /** Register that a reminder was attended (user acted on it) */
    fun recordAttendedReminder(event: CalendarEvent, reminderMinutes: Int)
    
    /** Register that an event was created */
    fun recordEventCreated(event: CalendarEvent)
    
    /** Get recommended reminder time offset for a category, based on user behavior */
    fun getRecommendedReminderTime(category: EventCategory): List<Int>
    
    /** Get user's most frequent event hours */
    fun getFrequentHours(): List<Int>
    
    /** Get user's typical event duration for a category */
    fun getTypicalDuration(category: EventCategory): Int?
    
    /** Get habit-based recommendations for a new event */
    fun getRecommendations(text: String, category: EventCategory): HabitRecommendation
}

data class HabitRecommendation(
    val suggestedTime: LocalTime? = null,
    val suggestedDuration: Int? = null,
    val suggestedReminders: List<Int>? = null,
    val confidence: Float = 0f  // 0.0 to 1.0
)
```

## Estructura de datos locales (Room)

```kotlin
@Entity(tableName = "habit_events")
data class HabitEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String,
    val hour: Int,           // 0-23
    val dayOfWeek: Int,      // 1=Monday, 7=Sunday
    val durationMinutes: Int,
    val location: String,
    val keywords: String,    // comma-separated extracted keywords
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "habit_reminders")
data class HabitReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val eventId: Long,
    val reminderMinutes: Int,
    val wasAttended: Boolean,  // true = user attended, false = dismissed
    val createdAt: Long = System.currentTimeMillis()
)
```

## Lógica de recomendación

```kotlin
class HabitLearningEngineImpl(
    private val eventDao: HabitEventDao,
    private val reminderDao: HabitReminderDao
) : HabitLearningEngine {

    /** 
     * Recommended reminder time = 
     *   modal of attended reminders for this category,
     *   adjusted by dismissing rate
     */
    override fun getRecommendedReminderTime(category: EventCategory): List<Int> {
        val attended = reminderDao.getAttendedByCategory(category.name)
        val dismissed = reminderDao.getDismissedByCategory(category.name)
        
        // Calculate effectiveness ratio per reminder offset
        val effectiveness = attended.groupBy { it.reminderMinutes }.map { (minutes, list) ->
            val dismissedCount = dismissed.count { it.reminderMinutes == minutes }
            val ratio = list.size.toFloat() / (list.size + dismissedCount).coerceAtLeast(1)
            minutes to ratio
        }
        
        // Return top 3 most effective reminders
        return effectiveness.sortedByDescending { it.second }
            .take(3)
            .map { it.first }
            .ifEmpty { SmartReminderEngine.generateReminders(category) }
    }

    /**
     * Frequent hours = hours where user has created 3+ events
     */
    override fun getFrequentHours(): List<Int> {
        return eventDao.getAll()
            .groupBy { it.hour }
            .filter { it.value.size >= 3 }
            .map { it.key }
            .sorted()
    }

    /**
     * Typical duration = median of user's events in this category
     */
    override fun getTypicalDuration(category: EventCategory): Int? {
        val durations = eventDao.getByCategory(category.name)
            .map { it.durationMinutes }
            .sorted()
        if (durations.isEmpty()) return null
        val mid = durations.size / 2
        return if (durations.size % 2 == 0) 
            (durations[mid - 1] + durations[mid]) / 2
        else 
            durations[mid]
    }

    /**
     * Full recommendation for a new event based on text and category
     */
    override fun getRecommendations(text: String, category: EventCategory): HabitRecommendation {
        val keywords = extractKeywords(text)
        val similarEvents = eventDao.findByKeywords(keywords)
        
        val suggestedTime = similarEvents
            .groupBy { it.hour }
            .maxByOrNull { it.value.size }
            ?.key?.let { LocalTime.of(it, 0) }
        
        val suggestedDuration = getTypicalDuration(category)
        val suggestedReminders = getRecommendedReminderTime(category)
        
        val confidence = (similarEvents.size.toFloat() / 10f).coerceAtMost(1f)
        
        return HabitRecommendation(
            suggestedTime = suggestedTime,
            suggestedDuration = suggestedDuration,
            suggestedReminders = suggestedReminders,
            confidence = confidence
        )
    }
}
```

## Privacidad y almacenamiento local

- Todo el aprendizaje se almacena en Room (base de datos local)
- NO se envían datos a servidores externos
- El usuario puede borrar su historial de aprendizaje en Ajustes
- Los datos se eliminan al desinstalar la app
- Los datos están cifrados en reposo (Room con SQLCipher o similar)

## Tests

- HabitLearningEngineTest:
  - recordAttendedReminder + getRecommendedReminderTime
  - recordDismissedReminder: descartados no aparecen en recomendación
  - getFrequentHours: con 3+ eventos en misma hora
  - getTypicalDuration: mediana de duraciones
  - getRecommendations: con eventos similares previos
  - getRecommendations: sin histórico → devuelve defaults
  - Privacidad: datos no salen del dispositivo
