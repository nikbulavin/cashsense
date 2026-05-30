package ru.resodostudios.cashsense.feature.transaction.csvimport.impl.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import ru.resodostudios.cashsense.feature.transaction.csvimport.api.ImportNavKey
import ru.resodostudios.cashsense.feature.transaction.csvimport.impl.ImportDialog
import ru.resodostudios.cashsense.feature.transaction.csvimport.impl.ImportViewModel
import ru.resodostudios.core.navigation.Navigator

fun EntryProviderScope<NavKey>.importDialogEntry(navigator: Navigator) {
    entry<ImportNavKey>(
        metadata = DialogSceneStrategy.dialog(),
    ) { key: ImportNavKey ->
        ImportDialogContent(
            navigator = navigator,
            key = key,
        )
    }
}

@Composable
private fun ImportDialogContent(
    navigator: Navigator,
    key: ImportNavKey,
) {
    ImportDialog(
        onDismiss = { navigator.goBack() },
        viewModel = hiltViewModel<ImportViewModel, ImportViewModel.Factory> { it.create(key) },
    )
}
