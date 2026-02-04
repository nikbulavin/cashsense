package ru.resodostudios.cashsense.core.data.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.content.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.time.Instant

internal class ReminderSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : ReminderScheduler {

    private val alarmManager: AlarmManager = checkNotNull(context.getSystemService())

    private fun createPendingIntent(reminderId: Int): PendingIntent {
        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            putExtra(EXTRA_REMINDER_ID, reminderId)
        }

        return PendingIntent.getBroadcast(
            context,
            reminderId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    override fun schedule(reminderId: Int, time: Instant) {
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            time.toEpochMilliseconds(),
            createPendingIntent(reminderId),
        )
    }

    override fun cancel(reminderId: Int) = alarmManager.cancel(createPendingIntent(reminderId))
}

internal const val EXTRA_REMINDER_ID = "reminder-id"