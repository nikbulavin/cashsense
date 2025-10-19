package ru.resodostudios.cashsense.ui

import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration.Indefinite
import androidx.compose.material3.SnackbarDuration.Short
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult.ActionPerformed
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import ru.resodostudios.cashsense.core.data.util.InAppUpdateResult
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Add
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Autorenew
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Category
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Close
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Wallet
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
            floatingActionButton = {
                FabMenu(
                    fabVisible = shouldShowFab && currentTopLevelDestination != SETTINGS,
                    modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
                )
            },
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun FabMenu(
    fabVisible: Boolean,
    modifier: Modifier = Modifier,
) {
    var fabMenuExpanded by rememberSaveable { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val items = listOf(
        CsIcons.Outlined.Wallet to stringResource(localesR.string.new_wallet),
        CsIcons.Outlined.Category to stringResource(localesR.string.new_category),
        CsIcons.Outlined.Autorenew to stringResource(localesR.string.new_subscription),
    )
    val closeText = stringResource(localesR.string.close)
    val toggleMenuText = stringResource(localesR.string.toggle_menu)
    val expandedText = stringResource(localesR.string.expanded)
    val collapsedText = stringResource(localesR.string.collapsed)

    BackHandler(fabMenuExpanded) { fabMenuExpanded = false }

    FloatingActionButtonMenu(
        modifier = modifier,
        expanded = fabMenuExpanded,
        button = {
            ToggleFloatingActionButton(
                modifier = Modifier
                    .semantics {
                        traversalIndex = -1f
                        stateDescription = if (fabMenuExpanded) expandedText else collapsedText
                        contentDescription = toggleMenuText
                    }
                    .animateFloatingActionButton(
                        visible = fabVisible || fabMenuExpanded,
                        alignment = Alignment.BottomEnd,
                    )
                    .focusRequester(focusRequester),
                checked = fabMenuExpanded,
                onCheckedChange = { fabMenuExpanded = !fabMenuExpanded },
            ) {
                val imageVector by remember {
                    derivedStateOf {
                        if (checkedProgress > 0.5f) CsIcons.Outlined.Close else CsIcons.Outlined.Add
                    }
                }
                Icon(
                    painter = rememberVectorPainter(imageVector),
                    contentDescription = null,
                    modifier = Modifier.animateIcon({ checkedProgress }),
                )
            }
        },
    ) {
        items.forEachIndexed { index, item ->
            FloatingActionButtonMenuItem(
                modifier =
                    Modifier
                        .semantics {
                            isTraversalGroup = true
                            if (index == items.size - 1) {
                                customActions =
                                    listOf(
                                        CustomAccessibilityAction(
                                            label = closeText,
                                            action = {
                                                fabMenuExpanded = false
                                                true
                                            },
                                        )
                                    )
                            }
                        }
                        .then(
                            if (index == 0) {
                                Modifier.onKeyEvent {
                                    if (
                                        it.type == KeyEventType.KeyDown &&
                                        (it.key == Key.DirectionUp ||
                                                (it.isShiftPressed && it.key == Key.Tab))
                                    ) {
                                        focusRequester.requestFocus()
                                        return@onKeyEvent true
                                    }
                                    return@onKeyEvent false
                                }
                            } else {
                                Modifier
                            }
                        ),
                onClick = { fabMenuExpanded = false },
                icon = {
                    Icon(
                        imageVector = item.first,
                        contentDescription = null,
                    )
                },
                text = {
                    Text(
                        text = item.second,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
            )
        }
    }
}

private fun NavDestination?.isRouteInHierarchy(route: KClass<*>): Boolean =
    this?.hierarchy?.any { it.hasRoute(route) } == true