package ru.resodostudios.cashsense.feature.subscription.list.navigation

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult.ActionPerformed
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import kotlinx.serialization.Serializable
import ru.resodostudios.cashsense.core.ui.LocalSnackbarHostState
import ru.resodostudios.cashsense.core.util.Constants.DEEP_LINK_SCHEME_AND_HOST
import ru.resodostudios.cashsense.core.util.Constants.SUBSCRIPTIONS_PATH
import ru.resodostudios.cashsense.feature.subscription.list.SubscriptionsScreen

@Serializable
data object SubscriptionsRoute

private const val DEEP_LINK_BASE_PATH = "$DEEP_LINK_SCHEME_AND_HOST/$SUBSCRIPTIONS_PATH"

fun NavController.navigateToSubscriptions(navOptions: NavOptions) {
    navigate(route = SubscriptionsRoute, navOptions)
}

fun NavGraphBuilder.subscriptionsScreen(
    onEditSubscription: (String) -> Unit,
) {
    composable<SubscriptionsRoute>(
        deepLinks = listOf(
            navDeepLink<SubscriptionsRoute>(
                basePath = DEEP_LINK_BASE_PATH,
            ),
        ),
    ) {
        val snackbarHostState = LocalSnackbarHostState.current
        SubscriptionsScreen(
            onEditSubscription = onEditSubscription,
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