package ru.resodostudios.cashsense.ui

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration.Indefinite
import androidx.compose.material3.SnackbarDuration.Short
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult.ActionPerformed
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import ru.resodostudios.cashsense.core.data.util.InAppUpdateResult
import ru.resodostudios.cashsense.core.designsystem.component.button.CsFloatingActionButton
import ru.resodostudios.cashsense.feature.category.dialog.navigation.navigateToCategoryDialog
import ru.resodostudios.cashsense.feature.subscription.dialog.navigation.navigateToSubscriptionDialog
import ru.resodostudios.cashsense.feature.wallet.dialog.navigation.navigateToWalletDialog
import ru.resodostudios.cashsense.navigation.CsNavHost
import ru.resodostudios.cashsense.navigation.TopLevelDestination.CATEGORIES
import ru.resodostudios.cashsense.navigation.TopLevelDestination.HOME
import ru.resodostudios.cashsense.navigation.TopLevelDestination.SETTINGS
import ru.resodostudios.cashsense.navigation.TopLevelDestination.SUBSCRIPTIONS
import kotlin.reflect.KClass
import ru.resodostudios.cashsense.core.locales.R as localesR

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CsApp(
    appState: CsAppState,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val currentDestination = appState.currentDestination
    val currentTopLevelDestination = appState.currentTopLevelDestination

    val inAppUpdateResult = appState.inAppUpdateResult.collectAsStateWithLifecycle().value

    val activity = LocalActivity.current

    val updateAvailableMessage = stringResource(localesR.string.app_update_available)
    val updateDownloadedMessage = stringResource(localesR.string.app_update_downloaded)
    val updateText = stringResource(localesR.string.update)
    val installText = stringResource(localesR.string.install)

    LaunchedEffect(inAppUpdateResult) {
        when (inAppUpdateResult) {
            is InAppUpdateResult.Available -> {
                val snackbarResult = snackbarHostState.showSnackbar(
                    message = updateAvailableMessage,
                    actionLabel = updateText,
                    duration = Indefinite,
                    withDismissAction = true,
                ) == ActionPerformed
                if (snackbarResult) activity?.let { inAppUpdateResult.startFlexibleUpdate(it, 120) }
            }

            is InAppUpdateResult.Downloaded -> {
                val snackbarResult = snackbarHostState.showSnackbar(
                    message = updateDownloadedMessage,
                    actionLabel = installText,
                    duration = Indefinite,
                ) == ActionPerformed
                if (snackbarResult) inAppUpdateResult.completeUpdate()
            }

            else -> {}
        }
    }

    var previousDestination by remember { mutableStateOf(HOME) }

    val navigationSuiteType = NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(windowAdaptiveInfo)

    NavigationSuiteScaffold(
        primaryActionContent = {
            if (currentTopLevelDestination != null) {
                CsFloatingActionButton(
                    contentDescriptionRes = currentTopLevelDestination.fabTitle ?: previousDestination.fabTitle!!,
                    icon = currentTopLevelDestination.fabIcon ?: previousDestination.fabIcon!!,
                    onClick = {
                        when (currentTopLevelDestination) {
                            HOME -> appState.navController.navigateToWalletDialog()
                            CATEGORIES -> appState.navController.navigateToCategoryDialog()
                            SUBSCRIPTIONS -> appState.navController.navigateToSubscriptionDialog()
                            SETTINGS -> {}
                        }
                    },
                    modifier = Modifier
                        .animateFloatingActionButton(
                            visible = currentTopLevelDestination != SETTINGS && !appState.hideFab,
                            alignment = Alignment.BottomEnd,
                        ),
                )
            }
        },
        navigationItems = {
            appState.topLevelDestinations.forEach { destination ->
                val selected = currentDestination.isRouteInHierarchy(destination.baseRoute)
                NavigationSuiteItem(
                    selected = selected,
                    icon = {
                        val navItemIcon = if (selected) {
                            destination.selectedIcon
                        } else {
                            destination.unselectedIcon
                        }
                        Icon(
                            imageVector = navItemIcon,
                            contentDescription = null,
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(destination.iconTextId),
                            maxLines = 1,
                        )
                    },
                    onClick = {
                        if (currentTopLevelDestination != null && currentTopLevelDestination != SETTINGS) {
                            previousDestination = currentTopLevelDestination
                        }
                        if (destination != HOME) appState.hideFab = false
                        appState.navigateToTopLevelDestination(destination)
                    },
                )
            }
        },
        navigationSuiteType = navigationSuiteType,
        navigationItemVerticalArrangement = Arrangement.Center,
    ) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.safeDrawing)
                        .then(
                            if (navigationSuiteType != NavigationSuiteType.ShortNavigationBarCompact &&
                                currentTopLevelDestination != SETTINGS
                            ) {
                                Modifier.padding(bottom = appState.snackbarBottomPadding)
                            } else {
                                Modifier
                            },
                        ),
                )
            },
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            modifier = Modifier.semantics {
                testTagsAsResourceId = true
            },
        ) { innerPadding ->
            CsNavHost(
                appState = appState,
                navigationSuiteType = navigationSuiteType,
                onShowSnackbar = { message, action ->
                    snackbarHostState.showSnackbar(
                        message = message,
                        actionLabel = action,
                        duration = Short,
                    ) == ActionPerformed
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(
                            WindowInsetsSides.Horizontal,
                        ),
                    ),
            )
        }
    }
}

private fun NavDestination?.isRouteInHierarchy(route: KClass<*>): Boolean =
    this?.hierarchy?.any { it.hasRoute(route) } == true