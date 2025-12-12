package ru.resodostudios.cashsense.feature.transaction.dialog.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import ru.resodostudios.core.navigation.Navigator

@Serializable
data class TransactionDialogNavKey(
    val walletId: String,
    val transactionId: String? = null,
    val repeated: Boolean = false,
) : NavKey

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