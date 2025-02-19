package ru.resodostudios.cashsense.core.ui.util

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.temporal.WeekFields

fun Instant.getZonedDateTime() = toLocalDateTime(TimeZone.currentSystemDefault())

fun Instant.getZonedYear() = this.getZonedDateTime().year

fun Instant.getZonedMonth() = this.getZonedDateTime().monthNumber

fun Instant.getZonedWeek() = this
    .getZonedDateTime()
    .toJavaLocalDateTime()
    .get(WeekFields.ISO.weekOfWeekBasedYear())

fun getCurrentZonedDateTime() =
    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

fun Instant.isInCurrentMonthAndYear(): Boolean {
    val localDateTime = this.getZonedDateTime()
    val currentDate = getCurrentZonedDateTime()
    return localDateTime.year == currentDate.year && localDateTime.month == currentDate.month
}

fun getCurrentYear(): Int = getCurrentZonedDateTime().year

fun getCurrentMonth(): Int = getCurrentZonedDateTime().monthNumber

fun getCurrentWeek(): Int = getCurrentZonedDateTime()
    .toJavaLocalDateTime()
    .get(WeekFields.ISO.weekOfWeekBasedYear())