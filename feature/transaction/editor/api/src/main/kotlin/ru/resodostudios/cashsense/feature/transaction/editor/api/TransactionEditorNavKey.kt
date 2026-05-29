package ru.resodostudios.cashsense.feature.transaction.editor.api

import kotlinx.serialization.Serializable
import ru.resodostudios.cashsense.feature.wallet.detail.api.WalletNavKey
import ru.resodostudios.core.navigation.NavDeepLinkKey
import ru.resodostudios.core.navigation.Navigator

@Serializable
data class TransactionEditorNavKey(
    val walletId: String,
    val transactionId: String? = null,
    val repeated: Boolean = false,
) : NavDeepLinkKey {
    override val parent = WalletNavKey(walletId)
}

fun Navigator.navigateToTransactionEditor(
    walletId: String,
    transactionId: String? = null,
    repeated: Boolean = false,
) {
    navigate(
        TransactionEditorNavKey(
            walletId = walletId,
            transactionId = transactionId,
            repeated = repeated,
        )
    )
}