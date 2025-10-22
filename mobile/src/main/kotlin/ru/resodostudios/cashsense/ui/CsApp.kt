package ru.resodostudios.cashsense.ui

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBars
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
import androidx.compose.material3.ToggleFloatingActionButtonDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import ru.resodostudios.cashsense.core.ui.component.FabMenu
import ru.resodostudios.cashsense.core.ui.component.FabMenuItem.CATEGORY
import ru.resodostudios.cashsense.core.ui.component.FabMenuItem.SUBSCRIPTION
import ru.resodostudios.cashsense.core.ui.component.FabMenuItem.WALLET
import ru.resodostudios.cashsense.feature.category.dialog.navigation.navigateToCategoryDialog
import ru.resodostudios.cashsense.feature.subscription.dialog.navigation.navigateToSubscriptionDialog
import ru.resodostudios.cashsense.feature.wallet.dialog.navigation.navigateToWalletDialog
import ru.resodostudios.cashsense.navigation.CsNavHost
import ru.resodostudios.cashsense.navigation.TopLevelDestination.HOME
import ru.resodostudios.cashsense.navigation.TopLevelDestination.SETTINGS
import kotlin.reflect.KClass
import ru.resodostudios.cashsense.core.locales.R as localesR

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CsApp(
    appState: CsAppState,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val currentDestination = appState.currentDestination
    val currentTopLevelDestination = appState.currentTopLevelDestination
    var shouldShowFab by rememberSaveable { mutableStateOf(true) }

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

    NavigationSuiteScaffold(
        navigationItems = {
            appState.topLevelDestinations.forEach { destination ->
                val selected = currentDestination.isRouteInHierarchy(destination.baseRoute)
                NavigationSuiteItem(
                    selected = selected,
                    icon = {
                        val icon = if (selected) {
                            destination.selectedIcon
                        } else {
                            destination.unselectedIcon
                        }
                        Icon(
                            imageVector = icon,
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
                        if (destination != HOME) shouldShowFab = true
                        appState.navigateToTopLevelDestination(destination)
                    },
                )
            }
        },
        navigationSuiteType = appState.navigationSuiteType,
        navigationItemVerticalArrangement = Arrangement.Center,
    ) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.safeDrawing)
                        .then(
                            if (appState.navigationSuiteType != NavigationSuiteType.ShortNavigationBarCompact &&
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
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(
                            WindowInsetsSides.Horizontal,
                        ),
                    ),
            ) {
                CsNavHost(
                    appState = appState,
                    navigationSuiteType = appState.navigationSuiteType,
                    onShowSnackbar = { message, action ->
                        snackbarHostState.showSnackbar(
                            message = message,
                            actionLabel = action,
                            duration = Short,
                        ) == ActionPerformed
                    },
                    updateFabVisibility = { shouldShowFab = it },
                    modifier = Modifier.fillMaxSize(),
                )
                if (currentTopLevelDestination != null && currentTopLevelDestination != SETTINGS) {
                    FabMenu(
                        visible = shouldShowFab,
                        onMenuItemClick = { fabItem ->
                            when (fabItem) {
                                WALLET -> appState.navController.navigateToWalletDialog()
                                CATEGORY -> appState.navController.navigateToCategoryDialog()
                                SUBSCRIPTION -> appState.navController.navigateToSubscriptionDialog()
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .windowInsetsPadding(WindowInsets.systemBars),
                        toggleContainerSize = if (appState.navigationSuiteType == NavigationSuiteType.NavigationRail) {
                            ToggleFloatingActionButtonDefaults.containerSizeMedium()
                        } else {
                            ToggleFloatingActionButtonDefaults.containerSize()
                        },
                    )
                }
            }
        }
    }
}

private fun NavDestination?.isRouteInHierarchy(route: KClass<*>): Boolean =
    this?.hierarchy?.any { it.hasRoute(route) } == true