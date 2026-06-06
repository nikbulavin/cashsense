package ru.resodostudios.cashsense.core.common

import ru.resodostudios.cashsense.core.common.Constants.USD_CURRENCY_CODE
import java.util.Currency
import java.util.Locale

fun getValidCurrencies(): List<Currency> {
    return Currency.getAvailableCurrencies()
        .filterNot { it.displayName.contains("""\d+""".toRegex()) }
        .sortedBy { it.currencyCode }
}

fun getDefaultCurrency(): Currency {
    val currencyByLocale = runCatching {
        Currency.getInstance(Locale.getDefault())
    }.getOrDefault(getUsdCurrency())
    return getValidCurrencies().find { it == currencyByLocale } ?: getUsdCurrency()
}

fun getUsdCurrency(): Currency = Currency.getInstance(USD_CURRENCY_CODE)