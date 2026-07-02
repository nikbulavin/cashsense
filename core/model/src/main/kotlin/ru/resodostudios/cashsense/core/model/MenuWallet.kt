package ru.resodostudios.cashsense.core.model

import java.math.BigDecimal
import java.util.Currency

data class MenuWallet(
    val id: String = "",
    val title: String = "",
    val currentBalance: BigDecimal = BigDecimal.ZERO,
    val currency: Currency? = null,
)