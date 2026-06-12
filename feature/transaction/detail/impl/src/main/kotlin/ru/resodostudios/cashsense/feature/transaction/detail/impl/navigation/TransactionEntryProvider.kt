package ru.resodostudios.cashsense.feature.transaction.detail.impl.navigation

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
import ru.resodostudios.cashsense.feature.transaction.detail.api.TransactionNavKey
import ru.resodostudios.cashsense.feature.transaction.detail.impl.TransactionScreen
import ru.resodostudios.cashsense.feature.transaction.detail.impl.TransactionViewModel
import ru.resodostudios.cashsense.feature.transaction.editor.api.navigateToTransactionEditor
import ru.resodostudios.core.navigation.Navigator

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun EntryProviderScope<NavKey>.transactionEntry(
    navigator: Navigator,
    animSpec: FiniteAnimationSpec<Float>,
) {
    entry<TransactionNavKey>(
        metadata = ListDetailSceneStrategy.extraPane() + metadata {
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
        TransactionScreen(
            onBackClick = navigator::goBack,
            onRepeatClick = { walletId, transactionId ->
                navigator.navigateToTransactionEditor(walletId, transactionId, true)
            },
            onEditClick = navigator::navigateToTransactionEditor,
            shouldShowNavigationIcon = LocalIsSinglePane.current,
            viewModel = hiltViewModel<TransactionViewModel, TransactionViewModel.Factory> {
                it.create(key)
            },
        )
    }
}