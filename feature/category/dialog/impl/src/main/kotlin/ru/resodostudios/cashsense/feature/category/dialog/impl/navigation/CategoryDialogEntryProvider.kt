package ru.resodostudios.cashsense.feature.category.dialog.impl.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import ru.resodostudios.cashsense.feature.category.dialog.api.CategoryNavKey
import ru.resodostudios.cashsense.feature.category.dialog.impl.CategoryDialog
import ru.resodostudios.cashsense.feature.category.dialog.impl.CategoryDialogViewModel
import ru.resodostudios.core.navigation.Navigator

fun EntryProviderScope<NavKey>.categoryDialogEntry(navigator: Navigator) {
    entry<CategoryNavKey>(
        metadata = DialogSceneStrategy.dialog(),
    ) { key ->
        CategoryDialog(
            onDismiss = navigator::goBack,
            viewModel = hiltViewModel<CategoryDialogViewModel, CategoryDialogViewModel.Factory> {
                it.create(key)
            },
        )
    }
}