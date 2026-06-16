package ru.resodostudios.cashsense.feature.category.editor.impl.navigation

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.metadata
import androidx.navigation3.ui.NavDisplay
import ru.resodostudios.cashsense.feature.category.editor.api.CategoryEditorNavKey
import ru.resodostudios.cashsense.feature.category.editor.impl.CategoryEditorScreen
import ru.resodostudios.cashsense.feature.category.editor.impl.CategoryEditorViewModel
import ru.resodostudios.core.navigation.Navigator

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun EntryProviderScope<NavKey>.categoryEditorEntry(
    navigator: Navigator,
    animSpec: FiniteAnimationSpec<IntOffset>,
) {
    entry<CategoryEditorNavKey>(
        metadata = ListDetailSceneStrategy.detailPane() + metadata {
            put(NavDisplay.TransitionKey) {
                slideInHorizontally(animSpec) { it } togetherWith slideOutHorizontally(animSpec) { -it }
            }
            put(NavDisplay.PopTransitionKey) {
                slideInHorizontally(animSpec) { -it } togetherWith slideOutHorizontally(animSpec) { it }
            }
            put(NavDisplay.PredictivePopTransitionKey) {
                slideInHorizontally(animSpec) { -it } togetherWith slideOutHorizontally(animSpec) { it }
            }
        },
    ) { key ->
        CategoryEditorScreen(
            onBackClick = navigator::goBack,
            viewModel = hiltViewModel<CategoryEditorViewModel, CategoryEditorViewModel.Factory> {
                it.create(key)
            },
        )
    }
}
