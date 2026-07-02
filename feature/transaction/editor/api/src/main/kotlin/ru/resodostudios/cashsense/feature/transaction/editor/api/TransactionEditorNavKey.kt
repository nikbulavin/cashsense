package ru.resodostudios.cashsense.feature.transaction.editor.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import ru.resodostudios.cashsense.core.model.Transaction
import ru.resodostudios.cashsense.feature.home.api.HomeNavKey
import ru.resodostudios.cashsense.feature.transaction.overview.api.TransactionOverviewNavKey
import ru.resodostudios.core.navigation.NavDeepLinkKey
import ru.resodostudios.core.navigation.Navigator

@Serializable
data class TransactionEditorNavKey(
    val walletId: String? = null,
    val transactionId: String? = null,
    val repeated: Boolean = false,
    val transaction: Transaction? = null,
) : NavDeepLinkKey {

    @Transient
    override val parent = walletId?.let { TransactionOverviewNavKey(it) } ?: HomeNavKey()
}

fun Navigator.navigateToTransactionEditor(
    walletId: String? = null,
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