package ru.resodostudios.cashsense.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.resodostudios.cashsense.core.database.model.CurrencyExchangeRateEntity
import java.util.Currency
import kotlin.time.Instant

@Dao
interface CurrencyConversionDao {

    @Query(
        value = """
            SELECT * FROM currency_exchange_rates 
            WHERE target_currency = :targetCurrency
            AND base_currency IN (:baseCurrencies)
        """,
    )
    fun getCurrencyExchangeRateEntities(
        targetCurrency: Currency,
        baseCurrencies: Set<Currency>,
    ): Flow<List<CurrencyExchangeRateEntity>>

    @Upsert
    suspend fun upsertCurrencyExchangeRates(entities: List<CurrencyExchangeRateEntity>)

    @Query("DELETE FROM currency_exchange_rates WHERE timestamp < :cutoff")
    suspend fun deleteOutdatedCurrencyExchangeRates(cutoff: Instant)
}