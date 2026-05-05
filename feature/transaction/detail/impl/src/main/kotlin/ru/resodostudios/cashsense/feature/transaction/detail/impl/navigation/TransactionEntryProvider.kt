package ru.resodostudios.cashsense.feature.transaction.detail.impl.navigation

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
import ru.resodostudios.cashsense.feature.transaction.detail.api.TransactionNavKey
import ru.resodostudios.cashsense.feature.transaction.detail.impl.TransactionScreen
import ru.resodostudios.cashsense.feature.transaction.detail.impl.TransactionViewModel
import ru.resodostudios.cashsense.feature.transaction.dialog.api.navigateToTransactionDialog
import ru.resodostudios.core.navigation.Navigator

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun EntryProviderScope<NavKey>.transactionEntry(navigator: Navigator) {
    entry<TransactionNavKey>(
        metadata = ListDetailSceneStrategy.extraPane() + metadata {
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
        TransactionScreen(
            onBackClick = navigator::goBack,
            onRepeatClick = { walletId, transactionId ->
                navigator.navigateToTransactionDialog(walletId, transactionId, true)
            },
            onEditClick = navigator::navigateToTransactionDialog,
            viewModel = hiltViewModel<TransactionViewModel, TransactionViewModel.Factory> {
                it.create(key)
            },
        )
    }
}