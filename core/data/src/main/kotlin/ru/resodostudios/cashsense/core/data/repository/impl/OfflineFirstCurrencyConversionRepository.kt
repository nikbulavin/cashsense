package ru.resodostudios.cashsense.core.data.repository.impl

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import ru.resodostudios.cashsense.core.data.model.asEntity
import ru.resodostudios.cashsense.core.data.repository.CurrencyConversionRepository
import ru.resodostudios.cashsense.core.database.dao.CurrencyConversionDao
import ru.resodostudios.cashsense.core.network.CsNetworkDataSource
import java.math.BigDecimal
import java.util.Currency
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days

internal class OfflineFirstCurrencyConversionRepository @Inject constructor(
    private val dao: CurrencyConversionDao,
    private val network: CsNetworkDataSource,
) : CurrencyConversionRepository {

    override fun getConvertedCurrencies(
        baseCurrencies: Set<Currency>,
        targetCurrency: Currency,
    ): Flow<Map<Currency, BigDecimal>> {
        return dao.getCurrencyExchangeRateEntities(
            targetCurrency = targetCurrency,
            baseCurrencies = baseCurrencies,
        )
            .map { cachedRates ->
                cachedRates.associate { it.baseCurrency to it.exchangeRate }
            }
            .onEach { currencyExchangeRates ->
                val missingBaseCurrencies = buildSet {
                    addAll(baseCurrencies)
                    remove(targetCurrency)
                    removeAll(currencyExchangeRates.keys)
                }
                if (missingBaseCurrencies.isNotEmpty()) {
                    dao.upsertCurrencyExchangeRates(
                        getCurrencyExchangeRates(missingBaseCurrencies, targetCurrency)
                    )
                }
            }
            .catch { emit(emptyMap()) }
    }

    override suspend fun deleteOutdatedCurrencyExchangeRates() {
        val cutoff = Clock.System.now().minus(3.days)
        dao.deleteOutdatedCurrencyExchangeRates(cutoff)
    }

    override suspend fun getCurrencyExchangeRates(
        baseCurrencies: Set<Currency>,
        targetCurrency: Currency,
    ) = coroutineScope {
        baseCurrencies.map { baseCurrency ->
            async {
                network.getCurrencyExchangeRate(
                    baseCurrencyCode = baseCurrency.currencyCode,
                    targetCurrencyCode = targetCurrency.currencyCode,
                ).asEntity()
            }
        }.awaitAll()
    }
}