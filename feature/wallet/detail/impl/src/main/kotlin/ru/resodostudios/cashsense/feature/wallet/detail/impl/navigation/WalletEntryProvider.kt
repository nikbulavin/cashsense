package ru.resodostudios.cashsense.feature.wallet.detail.impl.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import ru.resodostudios.cashsense.feature.transaction.dialog.api.navigateToTransactionDialog
import ru.resodostudios.cashsense.feature.transfer.dialog.api.navigateToTransferDialog
import ru.resodostudios.cashsense.feature.wallet.detail.api.WalletNavKey
import ru.resodostudios.cashsense.feature.wallet.detail.impl.WalletScreen
import ru.resodostudios.cashsense.feature.wallet.detail.impl.WalletViewModel
import ru.resodostudios.cashsense.feature.wallet.dialog.api.navigateToWalletDialog
import ru.resodostudios.core.navigation.Navigator

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun EntryProviderScope<NavKey>.walletEntry(navigator: Navigator) {
    entry<WalletNavKey>(
        metadata = ListDetailSceneStrategy.detailPane(),
    ) { key ->
        WalletScreen(
            onBackClick = navigator::goBack,
            onTransfer = navigator::navigateToTransferDialog,
            onEditWallet = navigator::navigateToWalletDialog,
            navigateToTransactionDialog = navigator::navigateToTransactionDialog,
            shouldShowNavigationIcon = true,
            onShowSnackbar = { _, _ -> false },
            onDeleteClick = {},
            viewModel = hiltViewModel<WalletViewModel, WalletViewModel.Factory> { it.create(key) },
        )
    }
}