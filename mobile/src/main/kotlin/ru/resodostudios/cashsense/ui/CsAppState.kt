package ru.resodostudios.cashsense.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.TimeZone
import ru.resodostudios.cashsense.core.data.util.InAppUpdateManager
import ru.resodostudios.cashsense.core.data.util.InAppUpdateResult
import ru.resodostudios.cashsense.core.data.util.PermissionManager
import ru.resodostudios.cashsense.core.data.util.TimeZoneMonitor
import ru.resodostudios.cashsense.feature.home.api.HomeNavKey
import ru.resodostudios.cashsense.navigation.TOP_LEVEL_NAV_ITEMS
import ru.resodostudios.core.navigation.NavigationState
import ru.resodostudios.core.navigation.rememberNavigationState
import kotlin.time.Duration.Companion.seconds

@Composable
fun rememberCsAppState(
    timeZoneMonitor: TimeZoneMonitor,
    inAppUpdateManager: InAppUpdateManager,
    permissionManager: PermissionManager,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navigationState: NavigationState = rememberNavigationState(
        initialBackStack = listOf(HomeNavKey()),
        topLevelKeys = TOP_LEVEL_NAV_ITEMS.keys,
    ),
): CsAppState {

    return remember(
        timeZoneMonitor,
        inAppUpdateManager,
        permissionManager,
        coroutineScope,
        navigationState,
    ) {
        CsAppState(
            timeZoneMonitor = timeZoneMonitor,
            inAppUpdateManager = inAppUpdateManager,
            permissionManager = permissionManager,
            coroutineScope = coroutineScope,
            navigationState = navigationState,
        )
    }
}

@Stable
class CsAppState(
    timeZoneMonitor: TimeZoneMonitor,
    inAppUpdateManager: InAppUpdateManager,
    permissionManager: PermissionManager,
    coroutineScope: CoroutineScope,
    val navigationState: NavigationState,
) {

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

    val shouldRequestNotifications = permissionManager.shouldRequestNotifications
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = false,
        )
}