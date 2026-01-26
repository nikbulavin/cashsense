package ru.resodostudios.cashsense.core.data.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import ru.resodostudios.cashsense.core.data.repository.SubscriptionsRepository
import ru.resodostudios.cashsense.core.network.di.ApplicationScope
import ru.resodostudios.cashsense.core.notifications.Notifier
import javax.inject.Inject

@AndroidEntryPoint
internal class ReminderBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var subscriptionsRepository: SubscriptionsRepository

    @Inject
    lateinit var reminderScheduler: ReminderScheduler

    @Inject
    lateinit var notifier: Notifier

    @Inject
    @ApplicationScope
    lateinit var appScope: CoroutineScope

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getIntExtra(EXTRA_REMINDER_ID, 0)

        appScope.launch {
            withTimeoutOrNull(4500L) {
                if (reminderId != 0) findSubscriptionAndPostNotification(reminderId)
                if (intent.action == Intent.ACTION_BOOT_COMPLETED) rescheduleSubscriptionReminders()
            }
        }
    }

    private suspend fun rescheduleSubscriptionReminders() {
        subscriptionsRepository.getSubscriptions().firstOrNull()
            ?.filter { it.reminder != null }
            ?.forEach { it.reminder?.let(reminderScheduler::schedule) }
    }

    private suspend fun findSubscriptionAndPostNotification(reminderId: Int) {
        subscriptionsRepository.getSubscriptions().firstOrNull()
            ?.find { it.id.hashCode() == reminderId }
            ?.let { subscription ->
                notifier.postSubscriptionNotification(subscription)

                val reminder = subscription.reminder
                val repeatingInterval = reminder?.repeatingInterval

                if (reminder != null && repeatingInterval != null && repeatingInterval > 0) {
                    val newPaymentDate = subscription.paymentDate.plus(
                        repeatingInterval,
                        DateTimeUnit.MILLISECOND,
                    )

                    val timeZone = TimeZone.currentSystemDefault()
                    val newNotificationDate = LocalDateTime(
                        date = newPaymentDate.toLocalDateTime(timeZone).date.minus(
                            1,
                            DateTimeUnit.DAY,
                        ),
                        time = LocalTime(9, 0),
                    ).toInstant(timeZone)

                    val updatedSubscription = subscription.copy(
                        paymentDate = newPaymentDate,
                        reminder = reminder.copy(
                            notificationDate = newNotificationDate,
                        ),
                    )

                    subscriptionsRepository.upsertSubscription(updatedSubscription)
                }
            }
    }
}