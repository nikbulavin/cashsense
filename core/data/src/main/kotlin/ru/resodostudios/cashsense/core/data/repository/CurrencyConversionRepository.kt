package ru.resodostudios.cashsense.core.data.repository

import kotlinx.coroutines.flow.Flow
import ru.resodostudios.cashsense.core.database.model.CurrencyExchangeRateEntity
import java.math.BigDecimal
import java.util.Currency

interface CurrencyConversionRepository {

    fun getConvertedCurrencies(
        baseCurrencies: Set<Currency>,
        targetCurrency: Currency,
    ): Flow<Map<Currency, BigDecimal>>

    suspend fun deleteOutdatedCurrencyExchangeRates()

    suspend fun getCurrencyExchangeRates(
        baseCurrencies: Set<Currency>,
        targetCurrency: Currency,
    ): List<CurrencyExchangeRateEntity>
}