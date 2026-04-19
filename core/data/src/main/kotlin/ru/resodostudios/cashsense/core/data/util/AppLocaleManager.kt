package ru.resodostudios.cashsense.core.data.util

import kotlinx.coroutines.flow.Flow

interface AppLocaleManager {

    val currentLanguageTag: Flow<String>

    fun setApplicationLocale(languageTag: String)
}