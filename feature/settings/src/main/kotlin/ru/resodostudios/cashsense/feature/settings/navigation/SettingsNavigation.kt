package ru.resodostudios.cashsense.feature.settings.navigation

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
import ru.resodostudios.cashsense.feature.settings.SettingsScreen

@Serializable
object SettingsBaseRoute

@Serializable
object SettingsRoute

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
            slideInHorizontally(motionScheme.fastSpatialSpec()) { -it / 4 } +
                    fadeIn(motionScheme.fastEffectsSpec())
        },
        enterTransition = {
            slideInHorizontally(motionScheme.fastSpatialSpec()) { it / 4 } +
                    fadeIn(motionScheme.fastEffectsSpec())
        },
        exitTransition = {
            slideOutHorizontally(motionScheme.fastSpatialSpec()) { it / 4 } +
                    fadeOut(motionScheme.fastEffectsSpec())
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