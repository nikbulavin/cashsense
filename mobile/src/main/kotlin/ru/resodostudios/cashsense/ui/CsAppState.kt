package ru.resodostudios.cashsense.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
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
import ru.resodostudios.cashsense.feature.category.dialog.navigation.CategoryDialogRoute
import ru.resodostudios.cashsense.feature.category.list.navigation.CategoriesRoute
import ru.resodostudios.cashsense.feature.category.list.navigation.navigateToCategories
import ru.resodostudios.cashsense.feature.home.navigation.HomeRoute
import ru.resodostudios.cashsense.feature.home.navigation.navigateToHome
import ru.resodostudios.cashsense.feature.settings.navigation.SettingsRoute
import ru.resodostudios.cashsense.feature.settings.navigation.navigateToSettings
import ru.resodostudios.cashsense.feature.subscription.dialog.navigation.SubscriptionDialogRoute
import ru.resodostudios.cashsense.feature.subscription.list.navigation.SubscriptionsRoute
import ru.resodostudios.cashsense.feature.subscription.list.navigation.navigateToSubscriptions
import ru.resodostudios.cashsense.feature.transaction.dialog.navigation.TransactionDialogRoute
import ru.resodostudios.cashsense.feature.transfer.navigation.TransferDialogRoute
import ru.resodostudios.cashsense.feature.wallet.dialog.navigation.WalletDialogRoute
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
    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() {
            with(currentDestination) {
                if (this?.hasRoute<HomeRoute>() == true ||
                    this?.hasRoute<WalletDialogRoute>() == true ||
                    this?.hasRoute<TransferDialogRoute>() == true ||
                    this?.hasRoute<TransactionDialogRoute>() == true
                ) return HOME
                if (this?.hasRoute<CategoriesRoute>() == true ||
                    this?.hasRoute<CategoryDialogRoute>() == true
                ) return CATEGORIES
                if (this?.hasRoute<SubscriptionsRoute>() == true ||
                    this?.hasRoute<SubscriptionDialogRoute>() == true
                ) return SUBSCRIPTIONS
                if (this?.hasRoute<SettingsRoute>() == true) return SETTINGS
            }
            return null
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
                SETTINGS -> navController.navigateToSettings(topLevelNavOptions)
            }
        }
    }
}