package ru.resodostudios.cashsense.core.data.repository.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.resodostudios.cashsense.core.data.model.asEntity
import ru.resodostudios.cashsense.core.data.repository.SubscriptionsRepository
import ru.resodostudios.cashsense.core.data.util.ReminderScheduler
import ru.resodostudios.cashsense.core.database.dao.SubscriptionDao
import ru.resodostudios.cashsense.core.database.model.SubscriptionEntity
import ru.resodostudios.cashsense.core.database.model.asExternalModel
import ru.resodostudios.cashsense.core.model.data.Subscription
import javax.inject.Inject

internal class OfflineSubscriptionsRepository @Inject constructor(
    private val subscriptionDao: SubscriptionDao,
    private val reminderScheduler: ReminderScheduler,
) : SubscriptionsRepository {

    override fun getSubscription(id: String): Flow<Subscription> {
        return subscriptionDao.getSubscriptionEntity(id).map { it.asExternalModel() }
    }

    override fun getSubscriptions(): Flow<List<Subscription>> {
        return subscriptionDao.getSubscriptionEntities()
            .map { it.map(SubscriptionEntity::asExternalModel) }
    }

    override suspend fun upsertSubscription(subscription: Subscription) {
        subscriptionDao.upsertSubscription(subscription.asEntity())
        if (subscription.reminder != null) {
            reminderScheduler.schedule(subscription.reminder!!)
        } else {
            reminderScheduler.cancel(subscription.id.hashCode())
        }
    }

    override suspend fun deleteSubscription(id: String) {
        subscriptionDao.deleteSubscription(id)
        reminderScheduler.cancel(id.hashCode())
    }
}