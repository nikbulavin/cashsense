package ru.resodostudios.cashsense.feature.subscription.dialog.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import ru.resodostudios.core.navigation.Navigator

@Serializable
data class SubscriptionDialogNavKey(
    val subscriptionId: String? = null,
) : NavKey

fun Navigator.navigateToSubscriptionDialog(
    subscriptionId: String? = null,
) {
    navigate(SubscriptionDialogNavKey(subscriptionId))
}