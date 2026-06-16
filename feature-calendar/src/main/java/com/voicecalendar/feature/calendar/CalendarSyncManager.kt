package com.voicecalendar.feature.calendar

import android.content.ContentResolver
import android.content.Context
import android.provider.CalendarContract
import com.voicecalendar.domain.model.CalendarEvent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages synchronisation with the Android Calendar Provider.
 */
@Singleton
class CalendarSyncManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val contentResolver: ContentResolver get() = context.contentResolver

    fun getAvailableCalendars(): List<CalendarInfo> {
        val cursor = contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            arrayOf(
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.NAME,
                CalendarContract.Calendars.ACCOUNT_NAME
            ),
            null, null, null
        )

        val calendars = mutableListOf<CalendarInfo>()
        cursor?.use {
            while (it.moveToNext()) {
                calendars.add(
                    CalendarInfo(
                        id = it.getLong(0),
                        name = it.getString(1) ?: "Unknown",
                        accountName = it.getString(2) ?: ""
                    )
                )
            }
        }
        return calendars
    }
}

data class CalendarInfo(
    val id: Long,
    val name: String,
    val accountName: String
)
