package ru.resodostudios.cashsense.core.data.model

import ru.resodostudios.cashsense.core.database.model.SubscriptionEntity
import ru.resodostudios.cashsense.core.model.data.Subscription

fun Subscription.asEntity(): SubscriptionEntity {
    return SubscriptionEntity(
        id = id,
        title = title,
        amount = amount,
        currency = currency,
        paymentDate = paymentDate,
        notificationDate = notificationDate,
        repeatingInterval = repeatingInterval,
        fixedInterval = fixedInterval,
    )
}