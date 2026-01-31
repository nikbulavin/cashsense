package ru.resodostudios.cashsense.core.model.data

import kotlin.time.Instant

data class Reminder(
    val notificationDate: Instant? = null,
    val repeatingInterval: Long? = null,
)