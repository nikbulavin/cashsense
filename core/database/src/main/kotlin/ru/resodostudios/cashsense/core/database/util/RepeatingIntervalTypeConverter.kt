package ru.resodostudios.cashsense.core.database.util

import androidx.room.TypeConverter
import ru.resodostudios.cashsense.core.model.data.RepeatingIntervalType

class RepeatingIntervalTypeConverter {

    @TypeConverter
    fun fromRepeatingIntervalType(value: RepeatingIntervalType?): String? {
        return value?.name
    }

    @TypeConverter
    fun toRepeatingIntervalType(value: String?): RepeatingIntervalType {
        return value?.let { RepeatingIntervalType.valueOf(it) } ?: RepeatingIntervalType.NONE
    }
}