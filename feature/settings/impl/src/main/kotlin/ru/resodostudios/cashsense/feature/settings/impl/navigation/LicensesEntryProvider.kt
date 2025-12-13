package ru.resodostudios.cashsense.feature.settings.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import ru.resodostudios.cashsense.feature.settings.api.LicensesNavKey
import ru.resodostudios.cashsense.feature.settings.impl.LicensesScreen
import ru.resodostudios.core.navigation.Navigator

fun EntryProviderScope<NavKey>.licensesEntry(navigator: Navigator) {
    entry<LicensesNavKey> {
        LicensesScreen(
            onBackClick = navigator::goBack,
        )
    }
}