package ru.resodostudios.cashsense.core.ui.util

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
import ru.resodostudios.cashsense.core.model.data.TransactionWithCategory

fun List<TransactionWithCategory>.applyTransactionFilter(transactionFilter: TransactionFilter): FilterableTransactions {
    val filteredTransactions = this
        .filter {
            when (transactionFilter.financeType) {
                NOT_SET -> true
                EXPENSES -> it.transaction.amount.signum() < 0
                INCOME -> it.transaction.amount.signum() > 0
            }
        }
        .filter {
            when (transactionFilter.dateType) {
                ALL -> true
                WEEK -> it.transaction.timestamp.getZonedWeek() == getCurrentWeek()
                MONTH -> matchesSelectedYearMonth(it.transaction, transactionFilter)
                YEAR -> it.transaction.timestamp.getZonedYear() == transactionFilter.selectedDate.year
            }
        }

    val availableCategories = filteredTransactions
        .mapNotNull { it.category }
        .distinct()

    val filteredByCategories = if (transactionFilter.selectedCategories.isNotEmpty()) {
        filteredTransactions.filter { it.category in transactionFilter.selectedCategories }
    } else {
        filteredTransactions
    }

    return FilterableTransactions(
        transactionsCategories = filteredByCategories,
        availableCategories = availableCategories,
    )
}

private fun matchesSelectedYearMonth(
    transaction: Transaction,
    transactionFilter: TransactionFilter,
) = transaction.timestamp.getZonedYear() == transactionFilter.selectedDate.year &&
        transaction.timestamp.getZonedMonth() == transactionFilter.selectedDate.monthNumber