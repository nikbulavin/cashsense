package ru.resodostudios.cashsense.core.database.util

import androidx.room3.ColumnTypeConverter
import kotlin.uuid.Uuid

internal class UuidConverter {

    @ColumnTypeConverter
    fun uuidToString(value: Uuid?): String? = value?.toString()

    @ColumnTypeConverter
    fun stringToUuid(value: String?): Uuid? = value?.let(Uuid::parse)
}