package ru.resodostudios.cashsense.core.model.data

import java.math.BigDecimal

data class ExtendedWallet(
    val wallet: Wallet,
    val transactions: List<Transaction>,
    val currentBalance: BigDecimal,
)