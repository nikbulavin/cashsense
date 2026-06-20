package ru.resodostudios.cashsense.core.database.util

import androidx.room3.ColumnTypeConverter
import java.math.BigDecimal

internal class BigDecimalConverter {

    @ColumnTypeConverter
    fun bigDecimalToString(value: BigDecimal?): String? = value?.toString()

    @ColumnTypeConverter
    fun stringToBigDecimal(value: String?): BigDecimal? = value?.let(::BigDecimal)
}