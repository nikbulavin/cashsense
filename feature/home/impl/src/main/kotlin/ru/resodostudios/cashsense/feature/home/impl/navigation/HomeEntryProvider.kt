package ru.resodostudios.cashsense.feature.home.impl.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import ru.resodostudios.cashsense.core.ui.LocalIsSinglePane
import ru.resodostudios.cashsense.core.ui.component.DetailPanePlaceholder
import ru.resodostudios.cashsense.feature.home.api.HomeNavKey
import ru.resodostudios.cashsense.feature.home.impl.HomeScreen
import ru.resodostudios.cashsense.feature.home.impl.HomeViewModel
import ru.resodostudios.cashsense.feature.settings.api.navigateToSettings
import ru.resodostudios.cashsense.feature.transaction.detail.api.navigateToTransaction
import ru.resodostudios.cashsense.feature.transaction.editor.api.navigateToTransactionEditor
import ru.resodostudios.cashsense.feature.transaction.overview.api.navigateToTransactionOverview
import ru.resodostudios.cashsense.feature.transfer.dialog.api.navigateToTransferDialog
import ru.resodostudios.cashsense.feature.wallet.detail.api.navigateToWallet
import ru.resodostudios.core.navigation.Navigator
import ru.resodostudios.cashsense.core.locales.R as localesR

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun EntryProviderScope<NavKey>.homeEntry(navigator: Navigator) {
    entry<HomeNavKey>(
        metadata = ListDetailSceneStrategy.listPane {
            DetailPanePlaceholder(localesR.string.select_wallet)
        },
    ) { key ->
        HomeScreen(
            onWalletClick = navigator::navigateToWallet,
            onTransfer = navigator::navigateToTransferDialog,
            onTransactionCreate = navigator::navigateToTransactionEditor,
            onTransactionClick = navigator::navigateToTransaction,
            onSettingsClick = navigator::navigateToSettings,
            onTotalBalanceClick = navigator::navigateToTransactionOverview,
            shouldHighlightSelectedWallet = !LocalIsSinglePane.current,
            viewModel = hiltViewModel<HomeViewModel, HomeViewModel.Factory> { it.create(key) },
        )
    }
}
