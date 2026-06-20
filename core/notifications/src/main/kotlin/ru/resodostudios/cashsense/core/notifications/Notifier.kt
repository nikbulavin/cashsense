package ru.resodostudios.cashsense.core.notifications

import ru.resodostudios.cashsense.core.model.Subscription

/**
 * Interface for creating notifications in the app
 */
interface Notifier {

    fun postSubscriptionNotification(subscription: Subscription)
}