package ru.resodostudios.cashsense.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import androidx.tracing.trace
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.TimeZone
import ru.resodostudios.cashsense.core.data.util.InAppUpdateManager
import ru.resodostudios.cashsense.core.data.util.InAppUpdateResult
import ru.resodostudios.cashsense.core.data.util.TimeZoneMonitor
import ru.resodostudios.cashsense.feature.category.list.navigation.navigateToCategories
import ru.resodostudios.cashsense.feature.home.navigation.navigateToHome
import ru.resodostudios.cashsense.feature.settings.navigation.navigateToSettings
import ru.resodostudios.cashsense.feature.subscription.list.navigation.navigateToSubscriptions
import ru.resodostudios.cashsense.navigation.TopLevelDestination
import ru.resodostudios.cashsense.navigation.TopLevelDestination.CATEGORIES
import ru.resodostudios.cashsense.navigation.TopLevelDestination.HOME
import ru.resodostudios.cashsense.navigation.TopLevelDestination.SETTINGS
import ru.resodostudios.cashsense.navigation.TopLevelDestination.SUBSCRIPTIONS

@Composable
fun rememberCsAppState(
    timeZoneMonitor: TimeZoneMonitor,
    inAppUpdateManager: InAppUpdateManager,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
): CsAppState {

    return remember(
        timeZoneMonitor,
        coroutineScope,
        navController,
    ) {
        CsAppState(
            timeZoneMonitor = timeZoneMonitor,
            inAppUpdateManager = inAppUpdateManager,
            coroutineScope = coroutineScope,
            navController = navController,
        )
    }
}

@Stable
class CsAppState(
    timeZoneMonitor: TimeZoneMonitor,
    inAppUpdateManager: InAppUpdateManager,
    coroutineScope: CoroutineScope,
    val navController: NavHostController,
) {
    private val previousDestination = mutableStateOf<NavDestination?>(null)

    val currentDestination: NavDestination?
        @Composable get() {
            val currentEntry = navController.currentBackStackEntryFlow.collectAsState(null)
            return currentEntry.value?.destination.also { destination ->
                if (destination != null) previousDestination.value = destination
            } ?: previousDestination.value
        }

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() {
            return TopLevelDestination.entries.find { topLevelDestination ->
                topLevelDestination.routes.any { routeClass ->
                    currentDestination?.hasRoute(routeClass) == true
                }
            }
        }

    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries

    val currentTimeZone = timeZoneMonitor.currentTimeZone
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TimeZone.currentSystemDefault(),
        )

    val inAppUpdateResult = inAppUpdateManager.inAppUpdateResult
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = InAppUpdateResult.NotAvailable,
        )

    var hideFab by mutableStateOf(false)
    var snackbarBottomPadding by mutableStateOf(100.dp)

    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        trace("Navigation: ${topLevelDestination.name}") {
            val topLevelNavOptions = navOptions {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }

            when (topLevelDestination) {
                HOME -> {
                    navController.navigateToHome(navOptions = topLevelNavOptions)
                    snackbarBottomPadding = 100.dp
                }
                CATEGORIES -> {
                    navController.navigateToCategories(topLevelNavOptions)
                    snackbarBottomPadding = 100.dp
                }
                SUBSCRIPTIONS -> {
                    navController.navigateToSubscriptions(topLevelNavOptions)
                    snackbarBottomPadding = 100.dp
                }
                SETTINGS -> {
                    navController.navigateToSettings(topLevelNavOptions)
                    snackbarBottomPadding = 0.dp
                }
            }
        }
    }
}