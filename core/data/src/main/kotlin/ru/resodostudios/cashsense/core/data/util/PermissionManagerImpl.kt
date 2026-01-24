package ru.resodostudios.cashsense.core.data.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import ru.resodostudios.cashsense.core.data.repository.SubscriptionsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class PermissionManagerImpl @Inject constructor(
    subscriptionsRepository: SubscriptionsRepository,
) : PermissionManager {

    override val shouldRequestNotifications: Flow<Boolean> =
        subscriptionsRepository.getSubscriptions()
            .map { subscriptions ->
                subscriptions.any { it.reminder != null }
            }
            .distinctUntilChanged()
}
