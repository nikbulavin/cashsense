package ru.resodostudios.cashsense.core.ui.util

import kotlinx.datetime.number
import ru.resodostudios.cashsense.core.model.data.DateType
import ru.resodostudios.cashsense.core.model.data.DateType.ALL
import ru.resodostudios.cashsense.core.model.data.DateType.MONTH
import ru.resodostudios.cashsense.core.model.data.DateType.WEEK
import ru.resodostudios.cashsense.core.model.data.DateType.YEAR
import ru.resodostudios.cashsense.core.model.data.TransactionWithCategory
import java.math.BigDecimal

fun List<TransactionWithCategory>.getGraphData(dateType: DateType): Map<Int, BigDecimal> {
    return this
        .groupBy {
            val zonedDateTime = it.transaction.timestamp.getZonedDateTime()
            when (dateType) {
                YEAR -> zonedDateTime.month.number
                ALL, MONTH -> zonedDateTime.day
                WEEK -> zonedDateTime.dayOfWeek.ordinal
            }
        }
        .run {
            if (this.isNotEmpty()) {
                val minKey = this.keys.minOrNull() ?: 0
                val maxKey = this.keys.maxOrNull() ?: 0
                (minKey..maxKey).associateWith { key ->
                    this[key]?.sumOf { it.transaction.amount }?.abs() ?: BigDecimal.ZERO
                }
            } else {
                emptyMap()
            }
        }
}