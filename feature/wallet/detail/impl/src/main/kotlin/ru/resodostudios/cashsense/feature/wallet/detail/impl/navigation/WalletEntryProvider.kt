package ru.resodostudios.cashsense.feature.wallet.detail.impl.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.metadata
import androidx.navigation3.ui.NavDisplay
import ru.resodostudios.cashsense.feature.transaction.detail.api.navigateToTransaction
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
        metadata = ListDetailSceneStrategy.detailPane() + metadata {
            put(NavDisplay.TransitionKey) {
                EnterTransition.None togetherWith ExitTransition.None
            }
            put(NavDisplay.PopTransitionKey) {
                EnterTransition.None togetherWith ExitTransition.None
            }
            put(NavDisplay.PredictivePopTransitionKey) {
                EnterTransition.None togetherWith ExitTransition.None
            }
        },
    ) { key ->
        WalletScreen(
            onBackClick = navigator::goBack,
            onTransactionClick = navigator::navigateToTransaction,
            onTransfer = navigator::navigateToTransferDialog,
            onEditWallet = navigator::navigateToWalletDialog,
            navigateToTransactionDialog = navigator::navigateToTransactionDialog,
            shouldShowNavigationIcon = true,
            shouldHighlightSelectedTransaction = false,
            viewModel = hiltViewModel<WalletViewModel, WalletViewModel.Factory> { it.create(key) },
        )
    }
}