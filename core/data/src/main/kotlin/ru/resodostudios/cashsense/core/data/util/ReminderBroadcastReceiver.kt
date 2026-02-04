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
import ru.resodostudios.cashsense.core.model.data.RepeatingIntervalType
import ru.resodostudios.cashsense.core.model.data.Subscription
import ru.resodostudios.cashsense.core.network.di.ApplicationScope
import ru.resodostudios.cashsense.core.notifications.Notifier
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

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
            ?.filter { it.notificationDate != null }
            ?.forEach { subscription ->
                subscription.notificationDate?.let { date ->
                    reminderScheduler.schedule(subscription.id.hashCode(), date)
                }
            }
    }

    private suspend fun findSubscriptionAndPostNotification(reminderId: Int) {
        val subscription = subscriptionsRepository.getSubscriptions().firstOrNull()
            ?.find { it.id.hashCode() == reminderId } ?: return

        val timeZone = TimeZone.currentSystemDefault()
        val paymentDate = subscription.paymentDate.toLocalDateTime(timeZone).date
        val notificationDate = subscription.notificationDate ?: return
        val reminderDate = notificationDate.toLocalDateTime(timeZone).date
        val daysUntil = reminderDate.daysUntil(paymentDate)

        if (daysUntil > 0) {
            notifier.postSubscriptionNotification(subscription)
            val daysToSubtract = if (daysUntil > 1) 1 else 0
            val nextNotificationDate = LocalDateTime(
                paymentDate.minus(daysToSubtract, DateTimeUnit.DAY),
                LocalTime(9, 0)
            ).toInstant(timeZone)

            subscriptionsRepository.upsertSubscription(
                subscription.copy(notificationDate = nextNotificationDate)
            )
        } else {
            handleRepeatingSubscription(subscription, timeZone)
        }
    }

    private suspend fun handleRepeatingSubscription(subscription: Subscription, timeZone: TimeZone) {
        if (subscription.repeatingInterval == RepeatingIntervalType.NONE) return

        val newPaymentDate = calculateNewPaymentDate(subscription, timeZone)
        val newNotificationDate = calculateNewNotificationDate(newPaymentDate, timeZone)

        subscriptionsRepository.upsertSubscription(
            subscription.copy(
                paymentDate = newPaymentDate,
                notificationDate = newNotificationDate
            )
        )
    }

    private fun calculateNewPaymentDate(subscription: Subscription, timeZone: TimeZone): Instant {
        return if (subscription.fixedInterval) {
            val millis = when (subscription.repeatingInterval) {
                RepeatingIntervalType.MONTHLY -> 30.days.inWholeMilliseconds
                else -> 0L
            }
            subscription.paymentDate.plus(millis, DateTimeUnit.MILLISECOND)
        } else {
            val (quantity, unit) = when (subscription.repeatingInterval) {
                RepeatingIntervalType.MONTHLY -> 1 to DateTimeUnit.MONTH
                else -> 0 to DateTimeUnit.DAY
            }
            subscription.paymentDate.plus(quantity, unit, timeZone)
        }
    }

    private fun calculateNewNotificationDate(newPaymentDate: Instant, timeZone: TimeZone): Instant {
        val newPaymentDateLocal = newPaymentDate.toLocalDateTime(timeZone).date

        val notificationDateMinus7 = LocalDateTime(
            newPaymentDateLocal.minus(7, DateTimeUnit.DAY),
            LocalTime(9, 0),
        ).toInstant(timeZone)

        val notificationDateMinus1 = LocalDateTime(
            newPaymentDateLocal.minus(1, DateTimeUnit.DAY),
            LocalTime(9, 0),
        ).toInstant(timeZone)

        return if (Clock.System.now() > notificationDateMinus7) {
            notificationDateMinus1
        } else {
            notificationDateMinus7
        }
    }
}