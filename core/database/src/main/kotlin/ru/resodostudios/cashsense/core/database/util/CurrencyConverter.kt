package ru.resodostudios.cashsense.core.database.util

import androidx.room3.ColumnTypeConverter
import java.util.Currency

internal class CurrencyConverter {

    @ColumnTypeConverter
    fun currencyToString(currency: Currency?): String? = currency?.currencyCode

    @ColumnTypeConverter
    fun stringToCurrency(currencyCode: String?): Currency? = currencyCode?.let(Currency::getInstance)
}