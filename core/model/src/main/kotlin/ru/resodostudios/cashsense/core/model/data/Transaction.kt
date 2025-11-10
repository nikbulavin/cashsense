package ru.resodostudios.cashsense.core.model.data

import java.math.BigDecimal
import java.util.Currency
import kotlin.time.Instant
import kotlin.uuid.Uuid

data class Transaction(
    val id: String,
    val walletOwnerId: String,
    val description: String?,
    val amount: BigDecimal,
    val timestamp: Instant,
    val completed: Boolean,
    val ignored: Boolean,
    val transferId: Uuid?,
    val currency: Currency,
)