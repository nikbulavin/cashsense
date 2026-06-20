package ru.resodostudios.cashsense.core.model

data class Transfer(
    val withdrawalTransaction: Transaction,
    val depositTransaction: Transaction,
)