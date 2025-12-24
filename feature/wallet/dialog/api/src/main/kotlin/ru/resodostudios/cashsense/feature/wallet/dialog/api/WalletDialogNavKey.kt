package ru.resodostudios.cashsense.feature.wallet.dialog.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import ru.resodostudios.core.navigation.Navigator

@Serializable
data class WalletDialogNavKey(
    val walletId: String? = null,
) : NavKey

fun Navigator.navigateToWalletDialog(
    walletId: String? = null,
) {
    navigate(WalletDialogNavKey(walletId))
}