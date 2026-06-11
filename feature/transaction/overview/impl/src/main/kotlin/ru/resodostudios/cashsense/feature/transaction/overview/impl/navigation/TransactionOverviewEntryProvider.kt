package ru.resodostudios.cashsense.feature.transaction.overview.impl.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import ru.resodostudios.cashsense.feature.transaction.detail.api.navigateToTransaction
import ru.resodostudios.cashsense.feature.transaction.overview.api.TransactionOverviewNavKey
import ru.resodostudios.cashsense.feature.transaction.overview.impl.TransactionOverviewScreen
import ru.resodostudios.core.navigation.Navigator

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun EntryProviderScope<NavKey>.transactionOverviewEntry(navigator: Navigator) {
    entry<TransactionOverviewNavKey>(
        metadata = ListDetailSceneStrategy.detailPane(),
    ) {
        val isSinglePane = calculatePaneScaffoldDirective(currentWindowAdaptiveInfoV2())
            .maxHorizontalPartitions <= 1
        TransactionOverviewScreen(
            onBackClick = navigator::goBack,
            onTransactionClick = navigator::navigateToTransaction,
            shouldShowNavigationIcon = isSinglePane,
        )
    }
}