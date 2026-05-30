package ru.resodostudios.cashsense.feature.settings.impl.navigation

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.ui.unit.IntOffset
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.metadata
import androidx.navigation3.ui.NavDisplay
import ru.resodostudios.cashsense.feature.settings.api.LicensesNavKey
import ru.resodostudios.cashsense.feature.settings.impl.LicensesScreen
import ru.resodostudios.core.navigation.Navigator

fun EntryProviderScope<NavKey>.licensesEntry(
    navigator: Navigator,
    animSpec: FiniteAnimationSpec<IntOffset>,
) {
    entry<LicensesNavKey>(
        metadata = metadata {
            put(NavDisplay.TransitionKey) {
                slideInHorizontally(animSpec) { it } togetherWith slideOutHorizontally(animSpec) { -it }
            }
            put(NavDisplay.PopTransitionKey) {
                slideInHorizontally(animSpec) { -it } togetherWith slideOutHorizontally(animSpec) { it }
            }
            put(NavDisplay.PredictivePopTransitionKey) {
                slideInHorizontally(animSpec) { -it } togetherWith slideOutHorizontally(animSpec) { it }
            }
        },
    ) {
        LicensesScreen(
            onBackClick = navigator::goBack,
        )
    }
}