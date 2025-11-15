package ru.resodostudios.cashsense.core.ui.util

import kotlinx.datetime.number
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

fun List<Transaction>.applyTransactionFilter(transactionFilter: TransactionFilter): FilterableTransactions {
    val filteredTransactions = this
        .filter {
            when (transactionFilter.financeType) {
                NOT_SET -> true
                EXPENSES -> it.amount.signum() < 0
                INCOME -> it.amount.signum() > 0
            }
        }
        .filter {
            when (transactionFilter.dateType) {
                ALL -> true
                WEEK -> it.timestamp.getZonedWeek() == getCurrentWeek()
                MONTH -> matchesSelectedDate(it, transactionFilter)
                YEAR -> it.timestamp.getZonedYear() == transactionFilter.selectedDate.year
            }
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

private fun matchesSelectedDate(
    transaction: Transaction,
    transactionFilter: TransactionFilter,
): Boolean {
    return transaction.timestamp.getZonedYear() == transactionFilter.selectedDate.year
            && transaction.timestamp.getZonedMonth() == transactionFilter.selectedDate.month.number
}