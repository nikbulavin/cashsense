package ru.resodostudios.cashsense.feature.transaction.overview.impl.navigation

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.metadata
import androidx.navigation3.ui.NavDisplay
import ru.resodostudios.cashsense.core.ui.LocalIsSinglePane
import ru.resodostudios.cashsense.feature.transaction.detail.api.navigateToTransaction
import ru.resodostudios.cashsense.feature.transaction.editor.api.navigateToTransactionEditor
import ru.resodostudios.cashsense.feature.transaction.importer.api.navigateToTransactionImporter
import ru.resodostudios.cashsense.feature.transaction.overview.api.TransactionOverviewNavKey
import ru.resodostudios.cashsense.feature.transaction.overview.impl.TransactionOverviewScreen
import ru.resodostudios.cashsense.feature.transaction.overview.impl.TransactionOverviewViewModel
import ru.resodostudios.cashsense.feature.transfer.dialog.api.navigateToTransferDialog
import ru.resodostudios.cashsense.feature.wallet.dialog.api.navigateToWalletDialog
import ru.resodostudios.core.navigation.Navigator

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun EntryProviderScope<NavKey>.transactionOverviewEntry(
    navigator: Navigator,
    animSpec: FiniteAnimationSpec<Float>,
) {
    entry<TransactionOverviewNavKey>(
        metadata = ListDetailSceneStrategy.detailPane() + metadata {
            put(NavDisplay.TransitionKey) {
                fadeIn(animSpec) togetherWith fadeOut(animSpec)
            }
            put(NavDisplay.PopTransitionKey) {
                fadeIn(animSpec) togetherWith fadeOut(animSpec)
            }
            put(NavDisplay.PredictivePopTransitionKey) {
                fadeIn(animSpec) togetherWith fadeOut(animSpec)
            }
        },
    ) { key ->
        TransactionOverviewScreen(
            onBackClick = navigator::goBack,
            onTransactionClick = navigator::navigateToTransaction,
            onTransfer = navigator::navigateToTransferDialog,
            onEditWallet = navigator::navigateToWalletDialog,
            onImportClick = navigator::navigateToTransactionImporter,
            navigateToTransactionEditor = navigator::navigateToTransactionEditor,
            shouldShowNavigationIcon = LocalIsSinglePane.current,
            viewModel = hiltViewModel<TransactionOverviewViewModel, TransactionOverviewViewModel.Factory> {
                it.create(key)
            },
        )
    }
}
