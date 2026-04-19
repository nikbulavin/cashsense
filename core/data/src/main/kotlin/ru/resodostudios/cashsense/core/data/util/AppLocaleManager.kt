package ru.resodostudios.cashsense.core.data.util

interface AppLocaleManager {

    fun getCurrentLanguage(): String

    fun setApplicationLocale(languageTag: String)
}