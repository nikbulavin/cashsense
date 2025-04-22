package ru.resodostudios.cashsense.feature.settings.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import kotlinx.serialization.Serializable
import ru.resodostudios.cashsense.feature.settings.SettingsScreen

@Serializable
object SettingsBaseRoute

@Serializable
object SettingsRoute

fun NavController.navigateToSettings(navOptions: NavOptions? = null) =
    navigate(route = SettingsBaseRoute, navOptions)

fun NavGraphBuilder.settingsScreen(
    onLicensesClick: () -> Unit,
    nestedGraphs: NavGraphBuilder.() -> Unit,
) {
    navigation<SettingsBaseRoute>(
        startDestination = SettingsRoute,
        popEnterTransition = { slideInHorizontally { -it / 12 } + fadeIn(tween(300)) },
    ) {
        composable<SettingsRoute> {
            SettingsScreen(onLicensesClick)
        }
        nestedGraphs()
    }
}