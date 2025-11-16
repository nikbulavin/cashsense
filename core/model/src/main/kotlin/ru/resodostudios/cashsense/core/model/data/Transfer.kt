package ru.resodostudios.cashsense.core.model.data

data class Transfer(
    val withdrawalTransaction: Transaction,
    val depositTransaction: Transaction,
)