package ru.resodostudios.cashsense.feature.settings.impl.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MotionScheme
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import ru.resodostudios.cashsense.feature.settings.impl.LicensesScreen

@Serializable
data object LicensesRoute

fun NavController.navigateToLicenses() {
    navigate(LicensesRoute) {
        launchSingleTop = true
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun NavGraphBuilder.licensesScreen(
    motionScheme: MotionScheme,
    onBackClick: () -> Unit,
) {
    composable<LicensesRoute>(
        enterTransition = {
            slideInHorizontally(motionScheme.defaultSpatialSpec()) { it } +
                    fadeIn(motionScheme.defaultEffectsSpec())
        },
        exitTransition = {
            slideOutHorizontally(motionScheme.defaultSpatialSpec()) { it } +
                    fadeOut(motionScheme.defaultEffectsSpec())
        },
    ) {
        LicensesScreen(
            onBackClick = onBackClick,
        )
    }
}