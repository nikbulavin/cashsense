package ru.resodostudios.cashsense.feature.wallet.dialog.impl.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.dialog
import kotlinx.serialization.Serializable
import ru.resodostudios.cashsense.feature.wallet.dialog.impl.WalletDialog

@Serializable
data class WalletDialogRoute(
    val walletId: String? = null,
)

fun NavController.navigateToWalletDialog(
    walletId: String? = null,
) {
    navigate(route = WalletDialogRoute(walletId)) {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.walletDialog(
    onDismiss: () -> Unit,
) {
    dialog<WalletDialogRoute> {
        WalletDialog(
            onDismiss = onDismiss,
        )
    }
}