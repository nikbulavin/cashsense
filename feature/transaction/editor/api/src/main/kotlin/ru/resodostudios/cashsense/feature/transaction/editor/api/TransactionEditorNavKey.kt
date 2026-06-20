package ru.resodostudios.cashsense.feature.transaction.editor.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import ru.resodostudios.cashsense.core.model.Transaction
import ru.resodostudios.cashsense.feature.wallet.detail.api.WalletNavKey
import ru.resodostudios.core.navigation.NavDeepLinkKey
import ru.resodostudios.core.navigation.Navigator

@Serializable
data class TransactionEditorNavKey(
    val walletId: String,
    val transactionId: String? = null,
    val repeated: Boolean = false,
    val transaction: Transaction? = null,
) : NavDeepLinkKey {

    @Transient
    override val parent = WalletNavKey(walletId)
}

fun Navigator.navigateToTransactionEditor(
    walletId: String,
    transactionId: String? = null,
    repeated: Boolean = false,
    transaction: Transaction? = null,
) {
    navigate(
        TransactionEditorNavKey(
            walletId = walletId,
            transactionId = transactionId,
            repeated = repeated,
            transaction = transaction,
        )
    )
}