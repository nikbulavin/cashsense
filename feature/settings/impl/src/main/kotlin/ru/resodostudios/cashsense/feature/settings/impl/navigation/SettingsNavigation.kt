package ru.resodostudios.cashsense.feature.settings.impl.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MotionScheme
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import kotlinx.serialization.Serializable
import ru.resodostudios.cashsense.feature.settings.impl.SettingsScreen

@Serializable
data object SettingsBaseRoute

@Serializable
data object SettingsRoute

fun NavController.navigateToSettings(navOptions: NavOptions? = null) {
    navigate(SettingsBaseRoute, navOptions)
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun NavGraphBuilder.settingsScreen(
    onBackClick: () -> Unit,
    onLicensesClick: () -> Unit,
    motionScheme: MotionScheme,
    nestedGraphs: NavGraphBuilder.() -> Unit,
) {
    navigation<SettingsBaseRoute>(
        startDestination = SettingsRoute,
        popEnterTransition = {
            slideInHorizontally(motionScheme.defaultSpatialSpec()) { -it } +
                    fadeIn(motionScheme.defaultEffectsSpec())
        },
        popExitTransition = {
            slideOutHorizontally(motionScheme.defaultSpatialSpec()) { it } +
                    fadeOut(motionScheme.defaultEffectsSpec())
        },
        enterTransition = {
            slideInHorizontally(motionScheme.defaultSpatialSpec()) { it } +
                    fadeIn(motionScheme.defaultEffectsSpec())
        },
        exitTransition = {
            slideOutHorizontally(motionScheme.defaultSpatialSpec()) { -it } +
                    fadeOut(motionScheme.defaultEffectsSpec())
        },
    ) {
        composable<SettingsRoute> {
            SettingsScreen(
                onBackClick = onBackClick,
                onLicensesClick = onLicensesClick,
            )
        }
        nestedGraphs()
    }
}