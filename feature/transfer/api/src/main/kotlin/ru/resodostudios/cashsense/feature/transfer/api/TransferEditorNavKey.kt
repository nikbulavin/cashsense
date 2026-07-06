package ru.resodostudios.cashsense.feature.transfer.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import ru.resodostudios.core.navigation.Navigator

@Serializable
data class TransferEditorNavKey(
    val walletId: String?,
) : NavKey

fun Navigator.navigateToTransferEditor(
    walletId: String?,
) {
    navigate(TransferEditorNavKey(walletId))
}
