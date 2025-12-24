package ru.resodostudios.cashsense.feature.wallet.detail.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import ru.resodostudios.cashsense.feature.home.api.HomeNavKey
import ru.resodostudios.core.navigation.NavDeepLinkKey
import ru.resodostudios.core.navigation.Navigator

@Serializable
data class WalletNavKey(
    val walletId: String,
) : NavDeepLinkKey {
    override val parent: NavKey = HomeNavKey()
}

fun Navigator.navigateToWallet(
    walletId: String,
) {
    navigate(WalletNavKey(walletId))
}