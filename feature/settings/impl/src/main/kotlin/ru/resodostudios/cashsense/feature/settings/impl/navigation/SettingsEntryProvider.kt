package ru.resodostudios.cashsense.feature.settings.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import ru.resodostudios.cashsense.feature.settings.api.SettingsNavKey
import ru.resodostudios.cashsense.feature.settings.api.navigateToLicenses
import ru.resodostudios.cashsense.feature.settings.impl.SettingsScreen
import ru.resodostudios.core.navigation.Navigator

fun EntryProviderScope<NavKey>.settingsEntry(navigator: Navigator) {
    entry<SettingsNavKey> {
        SettingsScreen(
            onBackClick = navigator::goBack,
            onLicensesClick = navigator::navigateToLicenses,
        )
    }
}