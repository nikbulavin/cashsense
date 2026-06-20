package ru.resodostudios.cashsense.core.model

import kotlinx.datetime.LocalDate

data class TransactionFilter(
    val selectedCategories: Set<Category>,
    val financeType: FinanceType,
    val dateType: DateType,
    val selectedDate: LocalDate,
)