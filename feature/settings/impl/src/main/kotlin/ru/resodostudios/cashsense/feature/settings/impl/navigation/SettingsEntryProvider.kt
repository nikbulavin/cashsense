package ru.resodostudios.cashsense.feature.settings.impl.navigation

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.ui.unit.IntOffset
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import ru.resodostudios.cashsense.feature.settings.api.SettingsNavKey
import ru.resodostudios.cashsense.feature.settings.api.navigateToLicenses
import ru.resodostudios.cashsense.feature.settings.impl.SettingsScreen
import ru.resodostudios.core.navigation.Navigator

fun EntryProviderScope<NavKey>.settingsEntry(
    navigator: Navigator,
    animSpec: FiniteAnimationSpec<IntOffset>,
) {
    entry<SettingsNavKey>(
        metadata = NavDisplay.transitionSpec {
            slideInHorizontally(animSpec) { it } togetherWith slideOutHorizontally(animSpec) { -it }
        } + NavDisplay.popTransitionSpec {
            slideInHorizontally(animSpec) { -it } togetherWith slideOutHorizontally(animSpec) { it }
        } + NavDisplay.predictivePopTransitionSpec {
            slideInHorizontally(animSpec) { -it } togetherWith slideOutHorizontally(animSpec) { it }
        },
    ) {
        SettingsScreen(
            onBackClick = navigator::goBack,
            onLicensesClick = navigator::navigateToLicenses,
        )
    }
}