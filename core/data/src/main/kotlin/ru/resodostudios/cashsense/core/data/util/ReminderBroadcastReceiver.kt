package ru.resodostudios.cashsense.core.data.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import ru.resodostudios.cashsense.core.data.repository.SubscriptionsRepository
import ru.resodostudios.cashsense.core.network.di.ApplicationScope
import ru.resodostudios.cashsense.core.notifications.Notifier
import javax.inject.Inject

@AndroidEntryPoint
internal class ReminderBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var subscriptionsRepository: SubscriptionsRepository

    @Inject
    lateinit var reminderSchedulerImpl: ReminderSchedulerImpl

    @Inject
    lateinit var notifier: Notifier

    @Inject @ApplicationScope
    lateinit var appScope: CoroutineScope

    override fun onReceive(context: Context, intent: Intent) {

        val reminderId = intent.getIntExtra(EXTRA_REMINDER_ID, 0)

        appScope.launch {
            withTimeoutOrNull(4500L)  {
                if (reminderId != 0) {
                    findSubscriptionAndPostNotification(reminderId)
                }
                if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
                    rescheduleSubscriptionReminders()
                }
            }
        }
    }

    private suspend fun rescheduleSubscriptionReminders() {
        subscriptionsRepository.getSubscriptions().firstOrNull()
            ?.filter { it.reminder != null }
            ?.forEach { subscription ->
                subscription.reminder?.let {
                    reminderSchedulerImpl.schedule(it)
                }
            }
    }

    private suspend fun findSubscriptionAndPostNotification(reminderId: Int) {
        subscriptionsRepository.getSubscriptions().firstOrNull()
            ?.find { it.id.hashCode() == reminderId }
            ?.let { notifier.postSubscriptionNotification(it) }
    }
}