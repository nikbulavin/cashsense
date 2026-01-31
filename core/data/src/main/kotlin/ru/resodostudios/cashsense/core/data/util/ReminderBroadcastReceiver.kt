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
import kotlinx.datetime.daysUntil
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import ru.resodostudios.cashsense.core.data.repository.SubscriptionsRepository
import ru.resodostudios.cashsense.core.network.di.ApplicationScope
import ru.resodostudios.cashsense.core.notifications.Notifier
import javax.inject.Inject
import kotlin.time.Clock

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
            ?.filter { it.reminder != null && it.reminder?.notificationDate != null }
            ?.forEach { subscription ->
                subscription.reminder?.notificationDate?.let { date ->
                    reminderScheduler.schedule(subscription.id.hashCode(), date)
                }
            }
    }

    private suspend fun findSubscriptionAndPostNotification(reminderId: Int) {
        subscriptionsRepository.getSubscriptions().firstOrNull()
            ?.find { it.id.hashCode() == reminderId }
            ?.let { subscription ->
                val timeZone = TimeZone.currentSystemDefault()
                val paymentDate = subscription.paymentDate.toLocalDateTime(timeZone).date
                val reminder = subscription.reminder ?: return@let
                val reminderDate = reminder.notificationDate?.toLocalDateTime(timeZone)?.date ?: return@let
                val daysUntil = reminderDate.daysUntil(paymentDate)

                if (daysUntil > 1) {
                    notifier.postSubscriptionNotification(subscription)
                    val nextNotificationDate = LocalDateTime(
                        paymentDate.minus(1, DateTimeUnit.DAY),
                        LocalTime(9, 0),
                    ).toInstant(timeZone)

                    val updatedSubscription = subscription.copy(
                        reminder = reminder.copy(notificationDate = nextNotificationDate),
                    )
                    subscriptionsRepository.upsertSubscription(updatedSubscription)
                } else if (daysUntil == 1) {
                    notifier.postSubscriptionNotification(subscription)
                    val nextNotificationDate = LocalDateTime(
                        paymentDate,
                        LocalTime(9, 0),
                    ).toInstant(timeZone)

                    val updatedSubscription = subscription.copy(
                        reminder = reminder.copy(notificationDate = nextNotificationDate),
                    )
                    subscriptionsRepository.upsertSubscription(updatedSubscription)
                } else {
                    val repeatingInterval = reminder.repeatingInterval
                    if (repeatingInterval != null && repeatingInterval > 0) {
                        val newPaymentDate = subscription.paymentDate.plus(
                            repeatingInterval,
                            DateTimeUnit.MILLISECOND,
                        )
                        val newPaymentDateLocal = newPaymentDate.toLocalDateTime(timeZone).date
                        
                        val notificationDateMinus7 = LocalDateTime(
                            newPaymentDateLocal.minus(7, DateTimeUnit.DAY),
                            LocalTime(9, 0),
                        ).toInstant(timeZone)

                        val notificationDateMinus1 = LocalDateTime(
                            newPaymentDateLocal.minus(1, DateTimeUnit.DAY),
                            LocalTime(9, 0),
                        ).toInstant(timeZone)

                        val now = Clock.System.now()
                        val newNotificationDate = if (now > notificationDateMinus7) notificationDateMinus1 else notificationDateMinus7

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
}