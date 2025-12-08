package ru.resodostudios.cashsense.feature.subscription.dialog.impl.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.dialog
import kotlinx.serialization.Serializable
import ru.resodostudios.cashsense.feature.subscription.dialog.impl.SubscriptionDialog

@Serializable
data class SubscriptionDialogRoute(
    val subscriptionId: String? = null,
)

fun NavController.navigateToSubscriptionDialog(
    subscriptionId: String? = null,
) = navigate(route = SubscriptionDialogRoute(subscriptionId)) {
    launchSingleTop = true
}

fun NavGraphBuilder.subscriptionDialog(
    onDismiss: () -> Unit,
) {
    dialog<SubscriptionDialogRoute> {
        SubscriptionDialog(
            onDismiss = onDismiss,
        )
    }
}