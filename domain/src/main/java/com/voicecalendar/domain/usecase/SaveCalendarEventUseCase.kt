package com.voicecalendar.domain.usecase

import com.voicecalendar.domain.model.CalendarEvent
import com.voicecalendar.domain.repository.CalendarRepository
import javax.inject.Inject

/**
 * Use case to save or update a calendar event via the Calendar Provider.
 */
class SaveCalendarEventUseCase @Inject constructor(
    private val calendarRepository: CalendarRepository
) {
    suspend operator fun invoke(event: CalendarEvent): Result<CalendarEvent> {
        return if (event.eventId != null) {
            calendarRepository.updateEvent(event)
        } else {
            calendarRepository.saveEvent(event)
        }
    }
}
