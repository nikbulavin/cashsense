package ru.resodostudios.cashsense.feature.settings.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import ru.resodostudios.cashsense.feature.settings.LicensesScreen

@Serializable
object LicensesRoute

fun NavController.navigateToLicenses() = navigate(LicensesRoute) {
    launchSingleTop = true
}

fun NavGraphBuilder.licensesScreen(
    onBackClick: () -> Unit,
) {
    composable<LicensesRoute>(
        popExitTransition = { slideOutHorizontally { it / 12 } + fadeOut(tween(300)) },
        enterTransition = { slideInHorizontally { it / 12 } + fadeIn(tween(300)) },
    ) {
        LicensesScreen(
            onBackClick = onBackClick,
        )
    }
}