package ru.resodostudios.cashsense.core.database.util

import androidx.room3.ColumnTypeConverter
import kotlin.time.Instant

internal class InstantConverter {

    @ColumnTypeConverter
    fun instantToLong(instant: Instant?): Long? = instant?.toEpochMilliseconds()

    @ColumnTypeConverter
    fun longToInstant(value: Long?): Instant? = value?.let(Instant::fromEpochMilliseconds)
}