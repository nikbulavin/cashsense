package ru.resodostudios.cashsense.core.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow
import ru.resodostudios.cashsense.core.database.model.SubscriptionEntity

@Dao
interface SubscriptionDao {

    @Query("SELECT * FROM subscriptions WHERE id = :id")
    fun getSubscriptionEntity(id: String): Flow<SubscriptionEntity>

    @Query("SELECT * FROM subscriptions ORDER BY payment_date DESC")
    fun getSubscriptionEntities(): Flow<List<SubscriptionEntity>>

    @Upsert
    suspend fun upsertSubscription(subscription: SubscriptionEntity)

    @Query("DELETE FROM subscriptions WHERE id = :id")
    suspend fun deleteSubscription(id: String)
}