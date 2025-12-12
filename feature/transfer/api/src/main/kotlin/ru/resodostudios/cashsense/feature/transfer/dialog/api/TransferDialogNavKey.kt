package ru.resodostudios.cashsense.feature.transfer.dialog.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import ru.resodostudios.core.navigation.Navigator

@Serializable
data class TransferDialogNavKey(
    val walletId: String,
) : NavKey

fun Navigator.navigateToTransferDialog(
    walletId: String,
) {
    navigate(TransferDialogNavKey(walletId))
}