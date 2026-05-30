package ru.resodostudios.cashsense.feature.transaction.csvimport.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import ru.resodostudios.core.navigation.Navigator

@Serializable
data class ImportNavKey(
    val walletId: String,
) : NavKey

fun Navigator.navigateToImport(
    walletId: String,
) {
    navigate(ImportNavKey(walletId))
}
