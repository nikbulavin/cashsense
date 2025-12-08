package ru.resodostudios.cashsense.ui

import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
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
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.TimeZone
import ru.resodostudios.cashsense.core.data.util.InAppUpdateManager
import ru.resodostudios.cashsense.core.data.util.InAppUpdateResult
import ru.resodostudios.cashsense.core.data.util.TimeZoneMonitor
import ru.resodostudios.cashsense.feature.category.list.impl.navigation.navigateToCategories
import ru.resodostudios.cashsense.feature.home.impl.navigation.navigateToHome
import ru.resodostudios.cashsense.feature.subscription.list.impl.navigation.navigateToSubscriptions
import ru.resodostudios.cashsense.navigation.TopLevelDestination
import ru.resodostudios.cashsense.navigation.TopLevelDestination.CATEGORIES
import ru.resodostudios.cashsense.navigation.TopLevelDestination.HOME
import ru.resodostudios.cashsense.navigation.TopLevelDestination.SUBSCRIPTIONS
import kotlin.time.Duration.Companion.seconds

@Composable
fun rememberCsAppState(
    timeZoneMonitor: TimeZoneMonitor,
    inAppUpdateManager: InAppUpdateManager,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(true),
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
            windowAdaptiveInfo = windowAdaptiveInfo,
        )
    }
}

@Stable
class CsAppState(
    timeZoneMonitor: TimeZoneMonitor,
    inAppUpdateManager: InAppUpdateManager,
    coroutineScope: CoroutineScope,
    val navController: NavHostController,
    val windowAdaptiveInfo: WindowAdaptiveInfo,
) {
    private val previousDestination = mutableStateOf<NavDestination?>(null)

    val currentDestination: NavDestination?
        @Composable get() {
            val currentEntry by navController.currentBackStackEntryFlow.collectAsState(null)
            return currentEntry?.destination.also { destination ->
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
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = TimeZone.currentSystemDefault(),
        )

    val inAppUpdateResult = inAppUpdateManager.inAppUpdateResult
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = InAppUpdateResult.NotAvailable,
        )

    val navigationSuiteType = NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(windowAdaptiveInfo)

    var snackbarBottomPadding by mutableStateOf(
        if (navigationSuiteType == NavigationSuiteType.NavigationRail) {
            110.dp
        } else {
            76.dp
        }
    )

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
                HOME -> navController.navigateToHome(navOptions = topLevelNavOptions)
                CATEGORIES -> navController.navigateToCategories(topLevelNavOptions)
                SUBSCRIPTIONS -> navController.navigateToSubscriptions(topLevelNavOptions)
            }
        }
    }
}