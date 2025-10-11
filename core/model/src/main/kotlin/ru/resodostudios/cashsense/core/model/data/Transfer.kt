package ru.resodostudios.cashsense.core.model.data

data class Transfer(
    val withdrawalTransaction: TransactionWithCategory,
    val depositTransaction: TransactionWithCategory,
)