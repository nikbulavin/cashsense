package ru.resodostudios.cashsense.core.data.repository

import kotlinx.coroutines.flow.Flow
import ru.resodostudios.cashsense.core.model.Subscription

interface SubscriptionsRepository {

    fun getSubscription(id: String): Flow<Subscription>

    fun getSubscriptions(): Flow<List<Subscription>>

    suspend fun upsertSubscription(subscription: Subscription)

    suspend fun deleteSubscription(id: String)
}