package ru.resodostudios.cashsense.core.ui.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import kotlinx.datetime.toJavaLocalDate
import ru.resodostudios.cashsense.core.model.data.DateType.ALL
import ru.resodostudios.cashsense.core.model.data.DateType.MONTH
import ru.resodostudios.cashsense.core.model.data.DateType.WEEK
import ru.resodostudios.cashsense.core.model.data.DateType.YEAR
import ru.resodostudios.cashsense.core.model.data.FilterableTransactions
import ru.resodostudios.cashsense.core.model.data.FinanceType.EXPENSES
import ru.resodostudios.cashsense.core.model.data.FinanceType.INCOME
import ru.resodostudios.cashsense.core.model.data.FinanceType.NOT_SET
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.model.data.TransactionFilter
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale

fun List<Transaction>.filterTransactions(transactionFilter: TransactionFilter): FilterableTransactions {
    val filteredTransactions = this.filter { transaction ->
        val financeTypeMatches = when (transactionFilter.financeType) {
            NOT_SET -> true
            EXPENSES -> transaction.amount.signum() < 0
            INCOME -> transaction.amount.signum() > 0
        }
        val dateTypeMatches = when (transactionFilter.dateType) {
            ALL -> true
            WEEK -> matchesSelectedWeek(transaction, transactionFilter.selectedDate)
            MONTH -> matchesSelectedMonth(transaction, transactionFilter.selectedDate)
            YEAR -> transaction.timestamp.getZonedYear() == transactionFilter.selectedDate.year
        }
        financeTypeMatches && dateTypeMatches
    }

    val availableCategories = filteredTransactions
        .asSequence()
        .mapNotNull { it.category }
        .distinct()
        .sortedBy { category ->
            filteredTransactions
                .filter { it.category == category }
                .sumOf { it.amount }
        }
        .toList()

    val filteredByCategories = if (transactionFilter.selectedCategories.isNotEmpty()) {
        filteredTransactions.filter { it.category in transactionFilter.selectedCategories }
    } else {
        filteredTransactions
    }

    return FilterableTransactions(
        transactions = filteredByCategories,
        availableCategories = availableCategories,
    )
}

private fun matchesSelectedWeek(
    transaction: Transaction,
    selectedDate: LocalDate,
): Boolean {
    val transactionDate = transaction.timestamp.getZonedDateTime().date.toJavaLocalDate()
    val selectedJavaDate = selectedDate.toJavaLocalDate()
    val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
    val weekStart = selectedJavaDate.with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
    val weekEnd = weekStart.plusDays(6)

    return !transactionDate.isBefore(weekStart) && !transactionDate.isAfter(weekEnd)
}

private fun matchesSelectedMonth(
    transaction: Transaction,
    selectedDate: LocalDate,
): Boolean {
    return transaction.timestamp.getZonedYear() == selectedDate.year &&
            transaction.timestamp.getZonedMonth() == selectedDate.month.number
}
