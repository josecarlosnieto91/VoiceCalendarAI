package com.voicecalendar.data.repository

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.CalendarContract
import androidx.core.content.ContextCompat
import com.voicecalendar.domain.model.CalendarEvent
import com.voicecalendar.domain.repository.CalendarRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

class CalendarRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : CalendarRepository {

    private val contentResolver: ContentResolver get() = context.contentResolver

    override suspend fun saveEvent(event: CalendarEvent): Result<CalendarEvent> {
        return runCatching {
            val values = ContentValues().apply {
                put(CalendarContract.Events.DTSTART, toEpochMillis(event))
                put(CalendarContract.Events.TITLE, event.title)
                put(CalendarContract.Events.DESCRIPTION, event.description)
                put(CalendarContract.Events.EVENT_LOCATION, event.location)
                put(CalendarContract.Events.CALENDAR_ID, getPrimaryCalendarId())
                put(CalendarContract.Events.EVENT_TIMEZONE, ZoneId.systemDefault().id)

                if (event.allDay) put(CalendarContract.Events.ALL_DAY, 1)

                if (event.endTime != null) {
                    put(CalendarContract.Events.DTEND, toEndEpochMillis(event))
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_DEFAULT)
                }
            }

            val uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
            val eventId = ContentUris.parseId(uri!!)
            addReminder(eventId, event.reminderMinutes)
            event.copy(eventId = eventId, calendarId = getPrimaryCalendarId())
        }
    }

    override suspend fun updateEvent(event: CalendarEvent): Result<CalendarEvent> {
        return runCatching {
            val values = ContentValues().apply {
                put(CalendarContract.Events.DTSTART, toEpochMillis(event))
                put(CalendarContract.Events.TITLE, event.title)
                put(CalendarContract.Events.DESCRIPTION, event.description)
                put(CalendarContract.Events.EVENT_LOCATION, event.location)
                put(CalendarContract.Events.EVENT_TIMEZONE, ZoneId.systemDefault().id)
                if (event.endTime != null) {
                    put(CalendarContract.Events.DTEND, toEndEpochMillis(event))
                }
            }
            val uri = ContentUris.withAppendedId(
                CalendarContract.Events.CONTENT_URI,
                event.eventId ?: return@runCatching event
            )
            contentResolver.update(uri, values, null, null)
            event
        }
    }

    override suspend fun deleteEvent(eventId: Long): Result<Unit> = runCatching {
        val uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId)
        contentResolver.delete(uri, null, null)
    }

    override suspend fun getEvent(eventId: Long): Result<CalendarEvent> =
        Result.failure(UnsupportedOperationException("Not implemented"))

    override suspend fun getUpcomingEvents(limit: Int): Result<List<CalendarEvent>> {
        return runCatching {
            val projection = arrayOf(
                CalendarContract.Events._ID, CalendarContract.Events.TITLE,
                CalendarContract.Events.DESCRIPTION, CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND, CalendarContract.Events.EVENT_LOCATION
            )
            val now = System.currentTimeMillis()
            val cursor = contentResolver.query(
                CalendarContract.Events.CONTENT_URI, projection,
                "${CalendarContract.Events.DTSTART} >= ?",
                arrayOf(now.toString()),
                "${CalendarContract.Events.DTSTART} ASC LIMIT $limit"
            )
            val events = mutableListOf<CalendarEvent>()
            cursor?.use {
                while (it.moveToNext()) {
                    events.add(CalendarEvent(
                        eventId = it.getLong(0),
                        title = it.getString(1) ?: "",
                        description = it.getString(2) ?: "",
                        location = it.getString(5) ?: ""
                    ))
                }
            }
            events
        }
    }

    override suspend fun requestCalendarPermissions(): Boolean = hasCalendarPermissions()

    override suspend fun hasCalendarPermissions(): Boolean =
        ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED

    private fun getPrimaryCalendarId(): Long {
        val cursor = contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            arrayOf(CalendarContract.Calendars._ID), null, null, null
        )
        cursor?.use { if (it.moveToFirst()) return it.getLong(0) }
        return 1L
    }

    private fun addReminder(eventId: Long, minutes: Int) {
        val values = ContentValues().apply {
            put(CalendarContract.Reminders.EVENT_ID, eventId)
            put(CalendarContract.Reminders.MINUTES, minutes)
            put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
        }
        contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, values)
    }

    private fun toEpochMillis(event: CalendarEvent): Long {
        val date = event.date ?: java.time.LocalDate.now()
        val time = event.startTime ?: java.time.LocalTime.of(9, 0)
        return ZonedDateTime.of(date, time, ZoneId.systemDefault()).toEpochSecond() * 1000
    }

    private fun toEndEpochMillis(event: CalendarEvent): Long {
        val date = event.date ?: java.time.LocalDate.now()
        val time = event.endTime ?: event.startTime?.plusHours(1) ?: java.time.LocalTime.of(10, 0)
        return ZonedDateTime.of(date, time, ZoneId.systemDefault()).toEpochSecond() * 1000
    }
}
