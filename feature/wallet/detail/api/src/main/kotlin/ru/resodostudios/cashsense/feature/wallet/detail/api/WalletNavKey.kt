package ru.resodostudios.cashsense.feature.wallet.detail.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import ru.resodostudios.core.navigation.Navigator

@Serializable
data class WalletNavKey(
    val walletId: String,
) : NavKey

fun Navigator.navigateToWallet(
    walletId: String,
) {
    navigate(WalletNavKey(walletId))
}