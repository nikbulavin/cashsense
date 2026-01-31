package ru.resodostudios.cashsense.core.data.util

import kotlin.time.Instant

interface ReminderScheduler {

    fun schedule(reminderId: Int, time: Instant)

    fun cancel(reminderId: Int)
}