package ru.resodostudios.cashsense.feature.subscription.list.impl.navigation

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult.ActionPerformed
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import ru.resodostudios.cashsense.core.ui.LocalSnackbarHostState
import ru.resodostudios.cashsense.feature.subscription.dialog.api.navigateToSubscriptionDialog
import ru.resodostudios.cashsense.feature.subscription.list.api.SubscriptionsNavKey
import ru.resodostudios.cashsense.feature.subscription.list.impl.SubscriptionsScreen
import ru.resodostudios.core.navigation.Navigator

fun EntryProviderScope<NavKey>.subscriptionsEntry(navigator: Navigator) {
    entry<SubscriptionsNavKey> {
        val snackbarHostState = LocalSnackbarHostState.current
        SubscriptionsScreen(
            onEditSubscription = navigator::navigateToSubscriptionDialog,
            onShowSnackbar = { message, actionLabel ->
                snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = actionLabel,
                    duration = SnackbarDuration.Short,
                ) == ActionPerformed
            },
        )
    }
}