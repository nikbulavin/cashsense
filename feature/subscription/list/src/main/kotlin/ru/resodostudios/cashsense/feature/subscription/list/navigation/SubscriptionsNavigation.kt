package ru.resodostudios.cashsense.feature.subscription.list.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import kotlinx.serialization.Serializable
import ru.resodostudios.cashsense.core.util.Constants.DEEP_LINK_SCHEME_AND_HOST
import ru.resodostudios.cashsense.core.util.Constants.SUBSCRIPTIONS_PATH
import ru.resodostudios.cashsense.feature.subscription.list.SubscriptionsScreen

@Serializable
data object SubscriptionsBaseRoute

@Serializable
data object SubscriptionsRoute

private const val DEEP_LINK_BASE_PATH = "$DEEP_LINK_SCHEME_AND_HOST/$SUBSCRIPTIONS_PATH"

fun NavController.navigateToSubscriptions(navOptions: NavOptions) =
    navigate(route = SubscriptionsBaseRoute, navOptions)

fun NavGraphBuilder.subscriptionsScreen(
    onEditSubscription: (String) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    nestedGraphs: NavGraphBuilder.() -> Unit,
) {
    navigation<SubscriptionsBaseRoute>(
        startDestination = SubscriptionsRoute,
    ) {
        composable<SubscriptionsRoute>(
            deepLinks = listOf(
                navDeepLink<SubscriptionsRoute>(
                    basePath = DEEP_LINK_BASE_PATH,
                ),
            ),
        ) {
            SubscriptionsScreen(
                onEditSubscription = onEditSubscription,
                onShowSnackbar = onShowSnackbar,
            )
        }
        nestedGraphs()
    }
}