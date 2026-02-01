package ru.resodostudios.cashsense.core.model.data

import java.math.BigDecimal
import java.util.Currency
import kotlin.time.Instant

data class Subscription(
    val id: String,
    val title: String,
    val amount: BigDecimal,
    val currency: Currency,
    val paymentDate: Instant,
    val notificationDate: Instant?,
    val repeatingInterval: RepeatingIntervalType,
)