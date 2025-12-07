package ru.resodostudios.cashsense.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import ru.resodostudios.cashsense.feature.category.dialog.navigation.categoryDialog
import ru.resodostudios.cashsense.feature.category.dialog.navigation.navigateToCategoryDialog
import ru.resodostudios.cashsense.feature.category.list.navigation.categoriesScreen
import ru.resodostudios.cashsense.feature.settings.navigation.licensesScreen
import ru.resodostudios.cashsense.feature.settings.navigation.navigateToLicenses
import ru.resodostudios.cashsense.feature.settings.navigation.navigateToSettings
import ru.resodostudios.cashsense.feature.settings.navigation.settingsScreen
import ru.resodostudios.cashsense.feature.subscription.dialog.navigation.navigateToSubscriptionDialog
import ru.resodostudios.cashsense.feature.subscription.dialog.navigation.subscriptionDialog
import ru.resodostudios.cashsense.feature.subscription.list.navigation.subscriptionsScreen
import ru.resodostudios.cashsense.feature.transaction.dialog.navigation.navigateToTransactionDialog
import ru.resodostudios.cashsense.feature.transaction.dialog.navigation.transactionDialog
import ru.resodostudios.cashsense.feature.transfer.navigation.navigateToTransferDialog
import ru.resodostudios.cashsense.feature.transfer.navigation.transferDialog
import ru.resodostudios.cashsense.feature.wallet.dialog.navigation.navigateToWalletDialog
import ru.resodostudios.cashsense.feature.wallet.dialog.navigation.walletDialog
import ru.resodostudios.cashsense.ui.CsAppState
import ru.resodostudios.cashsense.ui.home2pane.HomeListDetailRoute
import ru.resodostudios.cashsense.ui.home2pane.homeListDetailScreen

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CsNavHost(
    appState: CsAppState,
    navigationSuiteType: NavigationSuiteType,
    updateFabVisibility: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val navController = appState.navController
    val motionScheme = MaterialTheme.motionScheme
    val topLevelDestinations = appState.topLevelDestinations

    NavHost(
        navController = navController,
        startDestination = HomeListDetailRoute,
        modifier = modifier,
        popEnterTransition = {
            slideInHorizontally(motionScheme.defaultSpatialSpec()) { -it } +
                    fadeIn(motionScheme.defaultEffectsSpec())
        },
        enterTransition = {
            val isTopLevelNav =
                isTopLevelNavigation(initialState, targetState, topLevelDestinations)

            if (isTopLevelNav) {
                val initialIndex = getTopLevelIndex(initialState.destination, topLevelDestinations)
                val targetIndex = getTopLevelIndex(targetState.destination, topLevelDestinations)

                if (initialIndex != -1 && targetIndex != -1) {
                    val isNavigatingToTheRight = targetIndex > initialIndex
                    slideInHorizontally(motionScheme.defaultSpatialSpec()) {
                        if (isNavigatingToTheRight) it else -it
                    } + fadeIn(motionScheme.defaultEffectsSpec())
                } else {
                    fadeIn()
                }
            } else {
                fadeIn()
            }
        },
        exitTransition = {
            val isTopLevelNav =
                isTopLevelNavigation(initialState, targetState, topLevelDestinations)

            if (isTopLevelNav) {
                val initialIndex = getTopLevelIndex(initialState.destination, topLevelDestinations)
                val targetIndex = getTopLevelIndex(targetState.destination, topLevelDestinations)

                if (initialIndex != -1 && targetIndex != -1) {
                    val isNavigatingToTheRight = targetIndex > initialIndex
                    slideOutHorizontally(motionScheme.defaultSpatialSpec()) {
                        if (isNavigatingToTheRight) -it else it
                    } + fadeOut(motionScheme.defaultEffectsSpec())
                } else {
                    fadeOut()
                }
            } else {
                slideOutHorizontally(motionScheme.defaultSpatialSpec()) { -it } +
                        fadeOut(motionScheme.defaultEffectsSpec())
            }
        },
    ) {
        homeListDetailScreen(
            onEditWallet = navController::navigateToWalletDialog,
            onTransfer = navController::navigateToTransferDialog,
            navigateToTransactionDialog = navController::navigateToTransactionDialog,
            navigateToWalletDialog = navController::navigateToWalletDialog,
            navigateToCategoryDialog = navController::navigateToCategoryDialog,
            navigateToSubscriptionDialog = navController::navigateToSubscriptionDialog,
            navigateToSettings = navController::navigateToSettings,
            updateFabVisibility = updateFabVisibility,
            updateSnackbarBottomPadding = { appState.snackbarBottomPadding = it },
            navigationSuiteType = navigationSuiteType,
            nestedDestinations = {
                transferDialog(navController::navigateUp)
                transactionDialog(navController::navigateUp)
                settingsScreen(
                    onBackClick = navController::navigateUp,
                    onLicensesClick = navController::navigateToLicenses,
                    motionScheme = motionScheme,
                    nestedGraphs = {
                        licensesScreen(
                            motionScheme = motionScheme,
                            onBackClick = navController::navigateUp,
                        )
                    },
                )
            },
        )
        categoriesScreen(
            onEditCategory = navController::navigateToCategoryDialog,
        )
        subscriptionsScreen(
            onEditSubscription = navController::navigateToSubscriptionDialog,
        )
        walletDialog(navController::navigateUp)
        categoryDialog(navController::navigateUp)
        subscriptionDialog(navController::navigateUp)
    }
}

private fun isTopLevelNavigation(
    initialState: NavBackStackEntry,
    targetState: NavBackStackEntry,
    topLevelDestinations: List<TopLevelDestination>,
): Boolean {
    val initialIsTopLevel = topLevelDestinations.any {
        it.routes.any { route -> initialState.destination.hasRoute(route) }
    }
    val targetIsTopLevel = topLevelDestinations.any {
        it.routes.any { route -> targetState.destination.hasRoute(route) }
    }
    return initialIsTopLevel && targetIsTopLevel
}

private fun getTopLevelIndex(
    destination: NavDestination,
    topLevelDestinations: List<TopLevelDestination>,
): Int {
    return topLevelDestinations.indexOfFirst { topLevelDestination ->
        topLevelDestination.routes.any { route -> destination.hasRoute(route) }
    }
}