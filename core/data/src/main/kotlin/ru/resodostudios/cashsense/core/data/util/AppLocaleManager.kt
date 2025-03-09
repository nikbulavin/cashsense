package ru.resodostudios.cashsense.core.data.util

import kotlinx.coroutines.flow.Flow

interface AppLocaleManager {

    val currentLocale: Flow<String>

    fun updateLocale(code: String)
}