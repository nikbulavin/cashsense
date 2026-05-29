package ru.resodostudios.cashsense.feature.transaction.editor.impl.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import ru.resodostudios.cashsense.feature.transaction.editor.api.TransactionEditorNavKey
import ru.resodostudios.cashsense.feature.transaction.editor.impl.TransactionDialogViewModel
import ru.resodostudios.cashsense.feature.transaction.editor.impl.TransactionEditorScreen
import ru.resodostudios.core.navigation.Navigator

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun EntryProviderScope<NavKey>.transactionDialogEntry(navigator: Navigator) {
    entry<TransactionEditorNavKey>(
        metadata = ListDetailSceneStrategy.extraPane(),
    ) { key ->
        TransactionEditorScreen(
            onBackClick = navigator::goBack,
            viewModel = hiltViewModel<TransactionDialogViewModel, TransactionDialogViewModel.Factory> {
                it.create(key)
            },
        )
    }
}