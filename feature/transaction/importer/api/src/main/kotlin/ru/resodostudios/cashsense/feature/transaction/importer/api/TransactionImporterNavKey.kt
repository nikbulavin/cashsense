package ru.resodostudios.cashsense.feature.transaction.importer.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import ru.resodostudios.core.navigation.Navigator

@Serializable
data class TransactionImporterNavKey(
    val walletId: String,
) : NavKey

fun Navigator.navigateToTransactionImporter(
    walletId: String,
) {
    navigate(TransactionImporterNavKey(walletId))
}
