package ru.resodostudios.cashsense.core.data.model

import ru.resodostudios.cashsense.core.database.model.CurrencyExchangeRateEntity
import ru.resodostudios.cashsense.core.network.model.NetworkCurrencyExchangeRate
import java.math.BigDecimal
import java.util.Currency
import kotlin.time.Clock
import kotlin.time.Instant

fun NetworkCurrencyExchangeRate.asEntity() =
    CurrencyExchangeRateEntity(
        id = base + target,
        baseCurrency = Currency.getInstance(base),
        targetCurrency = Currency.getInstance(target),
        exchangeRate = exchangeRate?.toBigDecimal() ?: BigDecimal.ZERO,
        timestamp = Instant.parse(timestamp ?: Clock.System.now().toString()),
    )