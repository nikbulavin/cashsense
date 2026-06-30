package ru.resodostudios.cashsense.feature.home.impl.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import ru.resodostudios.cashsense.core.ui.LocalIsNavRailVisible
import ru.resodostudios.cashsense.core.ui.LocalIsSinglePane
import ru.resodostudios.cashsense.core.ui.component.DetailPanePlaceholder
import ru.resodostudios.cashsense.core.ui.component.FabMenuItem.CATEGORY
import ru.resodostudios.cashsense.core.ui.component.FabMenuItem.SUBSCRIPTION
import ru.resodostudios.cashsense.core.ui.component.FabMenuItem.WALLET
import ru.resodostudios.cashsense.feature.category.editor.api.navigateToCategoryEditor
import ru.resodostudios.cashsense.feature.home.api.HomeNavKey
import ru.resodostudios.cashsense.feature.home.impl.HomeScreen
import ru.resodostudios.cashsense.feature.home.impl.HomeViewModel
import ru.resodostudios.cashsense.feature.settings.api.navigateToSettings
import ru.resodostudios.cashsense.feature.subscription.dialog.api.navigateToSubscriptionDialog
import ru.resodostudios.cashsense.feature.transaction.detail.api.navigateToTransaction
import ru.resodostudios.cashsense.feature.transaction.editor.api.navigateToTransactionEditor
import ru.resodostudios.cashsense.feature.transaction.overview.api.navigateToTransactionOverview
import ru.resodostudios.cashsense.feature.transfer.dialog.api.navigateToTransferDialog
import ru.resodostudios.cashsense.feature.wallet.dialog.api.navigateToWalletDialog
import ru.resodostudios.core.navigation.Navigator
import ru.resodostudios.cashsense.core.locales.R as localesR

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun EntryProviderScope<NavKey>.homeEntry(
    navigator: Navigator,
) {
    entry<HomeNavKey>(
        metadata = ListDetailSceneStrategy.listPane {
            DetailPanePlaceholder(localesR.string.select_wallet)
        },
    ) { key ->
        val isSinglePane = LocalIsSinglePane.current
        HomeScreen(
            onWalletClick = navigator::navigateToTransactionOverview,
            onTransfer = navigator::navigateToTransferDialog,
            onTransactionCreate = navigator::navigateToTransactionEditor,
            onTransactionClick = navigator::navigateToTransaction,
            onSettingsClick = navigator::navigateToSettings,
            onTotalBalanceClick = navigator::navigateToTransactionOverview,
            shouldHighlightSelectedWallet = !isSinglePane,
            shouldShowFab = !isSinglePane,
            isNavRailVisible = LocalIsNavRailVisible.current,
            onFabMenuItemClick = { fabItem ->
                when (fabItem) {
                    WALLET -> navigator.navigateToWalletDialog()
                    CATEGORY -> navigator.navigateToCategoryEditor()
                    SUBSCRIPTION -> navigator.navigateToSubscriptionDialog()
                }
            },
            viewModel = hiltViewModel<HomeViewModel, HomeViewModel.Factory> { it.create(key) },
        )
    }
}
