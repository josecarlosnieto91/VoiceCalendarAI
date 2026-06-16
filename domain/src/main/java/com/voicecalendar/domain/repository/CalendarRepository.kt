package com.voicecalendar.domain.repository

import com.voicecalendar.domain.model.CalendarEvent

/**
 * Repository interface for calendar event operations via Android Calendar Provider.
 */
interface CalendarRepository {
    suspend fun saveEvent(event: CalendarEvent): Result<CalendarEvent>
    suspend fun updateEvent(event: CalendarEvent): Result<CalendarEvent>
    suspend fun deleteEvent(eventId: Long): Result<Unit>
    suspend fun getEvent(eventId: Long): Result<CalendarEvent>
    suspend fun getUpcomingEvents(limit: Int): Result<List<CalendarEvent>>
    suspend fun requestCalendarPermissions(): Boolean
    suspend fun hasCalendarPermissions(): Boolean
}
