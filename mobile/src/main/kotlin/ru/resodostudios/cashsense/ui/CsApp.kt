package ru.resodostudios.cashsense.ui

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration.Indefinite
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult.ActionPerformed
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButtonDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import ru.resodostudios.cashsense.core.data.util.InAppUpdateResult
import ru.resodostudios.cashsense.core.ui.LocalSnackbarHostState
import ru.resodostudios.cashsense.core.ui.component.FabMenu
import ru.resodostudios.cashsense.core.ui.component.FabMenuItem.CATEGORY
import ru.resodostudios.cashsense.core.ui.component.FabMenuItem.SUBSCRIPTION
import ru.resodostudios.cashsense.core.ui.component.FabMenuItem.WALLET
import ru.resodostudios.cashsense.feature.category.dialog.api.navigateToCategoryDialog
import ru.resodostudios.cashsense.feature.category.dialog.impl.navigation.categoryDialogEntry
import ru.resodostudios.cashsense.feature.category.list.impl.navigation.categoriesEntry
import ru.resodostudios.cashsense.feature.home.impl.navigation.homeEntry
import ru.resodostudios.cashsense.feature.settings.impl.navigation.SettingsBaseRoute
import ru.resodostudios.cashsense.feature.subscription.dialog.api.navigateToSubscriptionDialog
import ru.resodostudios.cashsense.feature.subscription.dialog.impl.navigation.subscriptionDialogEntry
import ru.resodostudios.cashsense.feature.subscription.list.impl.navigation.subscriptionsEntry
import ru.resodostudios.cashsense.feature.wallet.dialog.api.navigateToWalletDialog
import ru.resodostudios.cashsense.feature.wallet.dialog.impl.navigation.walletDialogEntry
import ru.resodostudios.cashsense.navigation.HOME
import ru.resodostudios.cashsense.navigation.TOP_LEVEL_NAV_ITEMS
import ru.resodostudios.core.navigation.Navigator
import ru.resodostudios.core.navigation.toEntries
import kotlin.reflect.KClass
import ru.resodostudios.cashsense.core.locales.R as localesR

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun CsApp(
    appState: CsAppState,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val currentDestination = appState.currentDestination

    var shouldShowFab by rememberSaveable { mutableStateOf(true) }
    var isUpdateInProgressSnackbarShown by rememberSaveable { mutableStateOf(false) }

    val inAppUpdateResult = appState.inAppUpdateResult.collectAsStateWithLifecycle().value
    val activity = LocalActivity.current

    val updateAvailableMessage = stringResource(localesR.string.app_update_available)
    val updateInProgressMessage = stringResource(localesR.string.app_update_in_progress)
    val updateDownloadedMessage = stringResource(localesR.string.app_update_downloaded)
    val updateActionLabel = stringResource(localesR.string.update)
    val installActionLabel = stringResource(localesR.string.install)

    LaunchedEffect(inAppUpdateResult) {
        when (inAppUpdateResult) {
            InAppUpdateResult.NotAvailable -> {
                isUpdateInProgressSnackbarShown = false
            }

            is InAppUpdateResult.Available -> {
                val snackbarResult = snackbarHostState.showSnackbar(
                    message = updateAvailableMessage,
                    actionLabel = updateActionLabel,
                    duration = Indefinite,
                    withDismissAction = true,
                ) == ActionPerformed
                if (snackbarResult) activity?.let { inAppUpdateResult.startFlexibleUpdate(it, 120) }
            }

            InAppUpdateResult.InProgress -> {
                if (!isUpdateInProgressSnackbarShown) {
                    snackbarHostState.showSnackbar(
                        message = updateInProgressMessage,
                        duration = Indefinite,
                    )
                    isUpdateInProgressSnackbarShown = true
                }
            }

            is InAppUpdateResult.Downloaded -> {
                val snackbarResult = snackbarHostState.showSnackbar(
                    message = updateDownloadedMessage,
                    actionLabel = installActionLabel,
                    duration = Indefinite,
                ) == ActionPerformed
                if (snackbarResult) inAppUpdateResult.completeUpdate()
            }
        }
    }

    val navigator = remember { Navigator(appState.navigationState) }

    NavigationSuiteScaffold(
        navigationItems = {
            TOP_LEVEL_NAV_ITEMS.forEach { (navKey, navItem) ->
                val selected = navKey == appState.navigationState.currentTopLevelKey
                NavigationSuiteItem(
                    selected = selected,
                    icon = {
                        Icon(
                            imageVector = if (selected) navItem.selectedIcon else navItem.unselectedIcon,
                            contentDescription = null,
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(navItem.iconTextId),
                            maxLines = 1,
                        )
                    },
                    onClick = {
                        if (navItem != HOME) shouldShowFab = true
                        navigator.navigate(navKey)
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
                                !currentDestination.isRouteInHierarchy(SettingsBaseRoute::class)
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
                CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
                    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()
                    val dialogStrategy = remember { DialogSceneStrategy<NavKey>() }

                    val entryProvider = entryProvider {
                        homeEntry(navigator)
                        walletDialogEntry(navigator)
                        categoriesEntry(navigator)
                        categoryDialogEntry(navigator)
                        subscriptionsEntry(navigator)
                        subscriptionDialogEntry(navigator)
                    }

                    NavDisplay(
                        entries = appState.navigationState.toEntries(entryProvider),
                        sceneStrategy = dialogStrategy,
                        onBack = navigator::goBack,
                    )
                    if (appState.navigationState.currentTopLevelKey in TOP_LEVEL_NAV_ITEMS.keys) {
                        FabMenu(
                            visible = shouldShowFab,
                            onMenuItemClick = { fabItem ->
                                when (fabItem) {
                                    WALLET -> navigator.navigateToWalletDialog()
                                    CATEGORY -> navigator.navigateToCategoryDialog()
                                    SUBSCRIPTION -> navigator.navigateToSubscriptionDialog()
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
}

private fun NavDestination?.isRouteInHierarchy(route: KClass<*>): Boolean {
    return this?.hierarchy?.any { it.hasRoute(route) } == true
}