package ru.resodostudios.cashsense.feature.transaction.overview.impl.navigation

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult.ActionPerformed
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import ru.resodostudios.cashsense.core.ui.LocalSnackbarHostState
import ru.resodostudios.cashsense.feature.transaction.dialog.api.navigateToTransactionDialog
import ru.resodostudios.cashsense.feature.transaction.overview.api.TransactionOverviewNavKey
import ru.resodostudios.cashsense.feature.transaction.overview.impl.TransactionOverviewScreen
import ru.resodostudios.core.navigation.Navigator

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun EntryProviderScope<NavKey>.transactionOverviewEntry(navigator: Navigator) {
    entry<TransactionOverviewNavKey>(
        metadata = ListDetailSceneStrategy.detailPane(),
    ) {
        val snackbarHostState = LocalSnackbarHostState.current
        TransactionOverviewScreen(
            onBackClick = navigator::goBack,
            navigateToTransactionDialog = navigator::navigateToTransactionDialog,
            shouldShowNavigationIcon = true,
            onShowSnackbar = { message, actionLabel ->
                snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = actionLabel,
                    duration = SnackbarDuration.Short,
                ) == ActionPerformed
            },
        )
    }
}