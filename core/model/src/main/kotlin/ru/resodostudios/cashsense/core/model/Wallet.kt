package ru.resodostudios.cashsense.core.model

import java.math.BigDecimal
import java.util.Currency

data class Wallet(
    val id: String,
    val title: String,
    val initialBalance: BigDecimal,
    val currency: Currency,
)