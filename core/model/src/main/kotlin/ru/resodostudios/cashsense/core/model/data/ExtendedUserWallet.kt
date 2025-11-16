package ru.resodostudios.cashsense.core.model.data

import java.math.BigDecimal

data class ExtendedUserWallet(
    val wallet: Wallet,
    val transactions: List<Transaction>,
    val currentBalance: BigDecimal,
    val isPrimary: Boolean,
)