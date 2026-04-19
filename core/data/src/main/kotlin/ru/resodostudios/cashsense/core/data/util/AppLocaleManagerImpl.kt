package ru.resodostudios.cashsense.core.data.util

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import jakarta.inject.Inject

internal class AppLocaleManagerImpl @Inject constructor() : AppLocaleManager {

    override fun getCurrentLanguage(): String {
        return AppCompatDelegate.getApplicationLocales()[0]?.toLanguageTag() ?: ""
    }

    override fun setApplicationLocale(languageTag: String) {
        val localeList = if (languageTag.isEmpty()) {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(languageTag)
        }
        AppCompatDelegate.setApplicationLocales(localeList)
    }
}