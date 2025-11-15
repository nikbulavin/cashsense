package ru.resodostudios.cashsense.core.model.data

data class ExtendedWallet(
    val wallet: Wallet,
    val transactions: List<Transaction>,
)