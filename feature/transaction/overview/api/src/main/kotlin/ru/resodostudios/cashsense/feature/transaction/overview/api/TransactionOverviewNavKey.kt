package ru.resodostudios.cashsense.feature.transaction.overview.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import ru.resodostudios.cashsense.feature.home.api.HomeNavKey
import ru.resodostudios.core.navigation.NavDeepLinkKey
import ru.resodostudios.core.navigation.Navigator

@Serializable
data class TransactionOverviewNavKey(
    val walletId: String? = null,
) : NavDeepLinkKey {

    @Transient
    override val parent: NavKey = HomeNavKey()
}

fun Navigator.navigateToTransactionOverview(
    walletId: String? = null,
) = navigate(TransactionOverviewNavKey(walletId))