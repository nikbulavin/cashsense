package ru.resodostudios.cashsense.feature.transaction.dialog.api

import kotlinx.serialization.Serializable
import ru.resodostudios.cashsense.feature.wallet.detail.api.WalletNavKey
import ru.resodostudios.core.navigation.NavDeepLinkKey
import ru.resodostudios.core.navigation.Navigator

@Serializable
data class TransactionDialogNavKey(
    val walletId: String,
    val transactionId: String? = null,
    val repeated: Boolean = false,
) : NavDeepLinkKey {
    override val parent = WalletNavKey(walletId)
}

fun Navigator.navigateToTransactionDialog(
    walletId: String,
    transactionId: String? = null,
    repeated: Boolean = false,
) {
    navigate(
        TransactionDialogNavKey(
            walletId = walletId,
            transactionId = transactionId,
            repeated = repeated,
        )
    )
}