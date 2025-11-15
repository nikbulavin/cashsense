package ru.resodostudios.cashsense.core.model.data

data class FilterableTransactions(
    val transactions: List<Transaction>,
    val availableCategories: List<Category>,
)