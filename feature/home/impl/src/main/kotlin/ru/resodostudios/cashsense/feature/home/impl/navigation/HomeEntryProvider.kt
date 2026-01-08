package ru.resodostudios.cashsense.feature.home.impl.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import ru.resodostudios.cashsense.feature.home.api.HomeNavKey
import ru.resodostudios.cashsense.feature.home.impl.HomeDetailPlaceholder
import ru.resodostudios.cashsense.feature.home.impl.HomeScreen
import ru.resodostudios.cashsense.feature.home.impl.HomeViewModel
import ru.resodostudios.cashsense.feature.settings.api.navigateToSettings
import ru.resodostudios.cashsense.feature.transaction.dialog.api.navigateToTransactionDialog
import ru.resodostudios.cashsense.feature.transaction.overview.api.navigateToTransactionOverview
import ru.resodostudios.cashsense.feature.transfer.dialog.api.navigateToTransferDialog
import ru.resodostudios.cashsense.feature.wallet.detail.api.navigateToWallet
import ru.resodostudios.core.navigation.Navigator

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun EntryProviderScope<NavKey>.homeEntry(navigator: Navigator) {
    entry<HomeNavKey>(
        metadata = ListDetailSceneStrategy.listPane {
            HomeDetailPlaceholder()
        },
    ) { key ->
        HomeScreen(
            onWalletClick = navigator::navigateToWallet,
            onTransfer = navigator::navigateToTransferDialog,
            onTransactionCreate = navigator::navigateToTransactionDialog,
            onSettingsClick = navigator::navigateToSettings,
            onTotalBalanceClick = navigator::navigateToTransactionOverview,
            highlightSelectedWallet = false,
            viewModel = hiltViewModel<HomeViewModel, HomeViewModel.Factory> { it.create(key) },
        )
    }
}