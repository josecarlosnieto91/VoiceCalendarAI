# Calendar Integration - Especificación para feature-calendar/

## CalendarService Interface

```kotlin
interface CalendarService {
    suspend fun createEvent(event: CalendarEvent): Result<CalendarEvent>
    suspend fun updateEvent(event: CalendarEvent): Result<CalendarEvent>
    suspend fun deleteEvent(eventId: Long): Result<Unit>
    suspend fun getAvailableCalendars(): Result<List<CalendarInfo>>
    suspend fun getEvents(from: Long, to: Long): Result<List<CalendarEvent>>
    fun hasPermissions(): Boolean
    suspend fun requestPermissions(): Boolean
}

data class CalendarInfo(
    val id: Long,
    val displayName: String,
    val accountName: String,
    val isPrimary: Boolean = false,
    val isWritable: Boolean = true,
    val color: Int = 0
)
```

## Permisos dinámicos

```kotlin
class CalendarPermissionHandler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val permissions = arrayOf(
        android.Manifest.permission.READ_CALENDAR,
        android.Manifest.permission.WRITE_CALENDAR
    )

    fun hasPermissions(): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun requestPermissions(activity: Activity) {
        ActivityCompat.requestPermissions(activity, permissions, REQUEST_CALENDAR_PERMS)
    }
}
```

## Creación de evento con IA

Al guardar evento, incluir automáticamente en la descripción:

```
--- VoiceCalendar AI ---
Category: medical
Priority: normal
Duration: 60 min
Reminders: 1 week, 1 day, 2 hours before
```

## Recordatorios automáticos (CalendarContract.Reminders)

Añadir cada reminder de la lista de SmartReminderEngine como un `CalendarContract.Reminders` independiente.

## Manejo de errores

| Escenario | Comportamiento |
|-----------|---------------|
| Sin permisos | Mostrar diálogo de solicitud + snackbar explicativo |
| Calendar Provider no disponible | Result.failure con CalendarUnavailableException |
| Calendario de solo lectura | Seleccionar automáticamente el primer calendario editable |
| Google Account no configurada | Usar calendario local por defecto |

## Tests

- CalendarPermissionHandlerTest: verificar permisos
- CalendarRepositoryImplTest: mock ContentResolver
- CalendarServiceTest: flujo crear/actualizar/eliminar
- UI: test de solicitud de permisos con Accompanist
