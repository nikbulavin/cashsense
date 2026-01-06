package ru.resodostudios.cashsense.feature.transaction.detail.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import ru.resodostudios.core.navigation.Navigator

@Serializable
data class TransactionNavKey(
    val transactionId: String,
) : NavKey

fun Navigator.navigateToTransaction(
    transactionId: String,
) {
    navigate(TransactionNavKey(transactionId))
}