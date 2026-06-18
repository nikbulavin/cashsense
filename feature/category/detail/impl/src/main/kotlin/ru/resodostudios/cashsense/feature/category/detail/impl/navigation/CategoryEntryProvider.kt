package ru.resodostudios.cashsense.feature.category.detail.impl.navigation

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
import ru.resodostudios.cashsense.feature.category.detail.api.CategoryNavKey
import ru.resodostudios.cashsense.feature.category.detail.impl.CategoryScreen
import ru.resodostudios.cashsense.feature.category.detail.impl.CategoryViewModel
import ru.resodostudios.cashsense.feature.category.editor.api.navigateToCategoryEditor
import ru.resodostudios.cashsense.feature.transaction.detail.api.TransactionNavKey
import ru.resodostudios.cashsense.feature.transaction.detail.api.navigateToTransaction
import ru.resodostudios.core.navigation.Navigator

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun EntryProviderScope<NavKey>.categoryEntry(
    navigator: Navigator,
    animSpec: FiniteAnimationSpec<Float>,
) {
    entry<CategoryNavKey>(
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
        val isSinglePane = LocalIsSinglePane.current
        CategoryScreen(
            onBackClick = navigator::goBack,
            onEditCategory = navigator::navigateToCategoryEditor,
            onTransactionClick = navigator::navigateToTransaction,
            shouldShowNavigationIcon = isSinglePane,
            shouldHighlightSelectedTransaction = !isSinglePane && navigator.state.currentSubStack.any { it is TransactionNavKey },
            viewModel = hiltViewModel<CategoryViewModel, CategoryViewModel.Factory> {
                it.create(key)
            },
        )
    }
}
