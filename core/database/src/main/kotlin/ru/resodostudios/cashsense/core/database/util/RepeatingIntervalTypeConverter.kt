package ru.resodostudios.cashsense.core.database.util

import androidx.room3.ColumnTypeConverter
import ru.resodostudios.cashsense.core.model.RepeatingIntervalType

class RepeatingIntervalTypeConverter {

    @ColumnTypeConverter
    fun fromRepeatingIntervalType(value: RepeatingIntervalType?): String? {
        return value?.name
    }

    @ColumnTypeConverter
    fun toRepeatingIntervalType(value: String?): RepeatingIntervalType {
        return value?.let { RepeatingIntervalType.valueOf(it) } ?: RepeatingIntervalType.NONE
    }
}