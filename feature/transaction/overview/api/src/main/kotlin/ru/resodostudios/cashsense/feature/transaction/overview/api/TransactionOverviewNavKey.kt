package ru.resodostudios.cashsense.feature.transaction.overview.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import ru.resodostudios.core.navigation.Navigator

@Serializable
data object TransactionOverviewNavKey : NavKey

fun Navigator.navigateToTransactionOverview() = navigate(TransactionOverviewNavKey)