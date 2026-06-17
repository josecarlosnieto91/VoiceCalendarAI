package com.voicecalendar.app.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.voicecalendar.app.MainActivity
import java.util.concurrent.TimeUnit

/**
 * WorkManager Worker for scheduling calendar event reminders.
 * Displays a notification when the reminder fires.
 */
class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val CHANNEL_ID = "voicecalendar_reminders"
        const val NOTIFICATION_ID_BASE = 1000
        const val KEY_EVENT_TITLE = "event_title"
        const val KEY_EVENT_ID = "event_id"
        const val KEY_MINUTES_BEFORE = "minutes_before"
        const val WORK_NAME_PREFIX = "reminder_"

        fun scheduleReminder(
            context: Context,
            eventId: Long,
            eventTitle: String,
            minutesBefore: Int,
            startTimeMillis: Long
        ) {
            val delayMillis = startTimeMillis - System.currentTimeMillis() - (minutesBefore * 60_000L)
            if (delayMillis <= 0) return

            val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                .setInputData(
                    workDataOf(
                        KEY_EVENT_TITLE to eventTitle,
                        KEY_EVENT_ID to eventId,
                        KEY_MINUTES_BEFORE to minutesBefore
                    )
                )
                .addTag("${WORK_NAME_PREFIX}$eventId")
                .build()

            val workName = "${WORK_NAME_PREFIX}${eventId}_${minutesBefore}"

            WorkManager.getInstance(context).enqueueUniqueWork(
                workName,
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }

        fun cancelAllReminders(context: Context, eventId: Long) {
            WorkManager.getInstance(context).cancelAllWorkByTag("${WORK_NAME_PREFIX}$eventId")
        }

        fun createNotificationChannel(context: Context) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Calendar Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for scheduled calendar events"
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override suspend fun doWork(): Result {
        val eventTitle = inputData.getString(KEY_EVENT_TITLE) ?: return Result.failure()
        val minutesBefore = inputData.getInt(KEY_MINUTES_BEFORE, 0)

        showNotification(eventTitle, minutesBefore)
        return Result.success()
    }

    private fun showNotification(eventTitle: String, minutesBefore: Int) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val timeText = when {
            minutesBefore >= 1440 -> "${minutesBefore / 1440} day(s)"
            minutesBefore >= 60 -> "${minutesBefore / 60} hour(s)"
            else -> "$minutesBefore minute(s)"
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Upcoming: $eventTitle")
            .setContentText("Starts in $timeText")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(
            NOTIFICATION_ID_BASE + minutesBefore,
            notification
        )
    }
}
