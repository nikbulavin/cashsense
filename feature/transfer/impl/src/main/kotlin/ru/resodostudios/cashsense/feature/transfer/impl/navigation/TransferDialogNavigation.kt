package ru.resodostudios.cashsense.feature.transfer.impl.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.dialog
import kotlinx.serialization.Serializable
import ru.resodostudios.cashsense.feature.transfer.impl.TransferDialog

@Serializable
data class TransferDialogRoute(
    val walletId: String? = null,
)

fun NavController.navigateToTransferDialog(
    walletId: String? = null,
) {
    navigate(route = TransferDialogRoute(walletId)) {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.transferDialog(
    onDismiss: () -> Unit,
) {
    dialog<TransferDialogRoute> {
        TransferDialog(
            onDismiss = onDismiss,
        )
    }
}