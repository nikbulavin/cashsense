package ru.resodostudios.cashsense.core.model.data

import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.util.Currency
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
data class Transaction(
    val id: String,
    val walletOwnerId: String,
    val description: String?,
    @Serializable(with = BigDecimalSerializer::class)
    val amount: BigDecimal,
    val timestamp: Instant,
    val completed: Boolean,
    val ignored: Boolean,
    val transferId: Uuid?,
    @Serializable(with = CurrencySerializer::class)
    val currency: Currency,
    val category: Category?,
)