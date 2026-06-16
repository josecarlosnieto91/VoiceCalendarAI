package com.voicecalendar.domain.usecase

import com.voicecalendar.domain.model.CalendarEvent
import com.voicecalendar.domain.repository.CalendarRepository
import javax.inject.Inject

/**
 * Use case to retrieve upcoming calendar events.
 */
class GetUpcomingEventsUseCase @Inject constructor(
    private val calendarRepository: CalendarRepository
) {
    suspend operator fun invoke(limit: Int = 10): Result<List<CalendarEvent>> {
        return calendarRepository.getUpcomingEvents(limit)
    }
}
