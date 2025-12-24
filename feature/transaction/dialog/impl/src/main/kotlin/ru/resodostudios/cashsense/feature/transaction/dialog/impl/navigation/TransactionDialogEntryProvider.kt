package ru.resodostudios.cashsense.feature.transaction.dialog.impl.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import ru.resodostudios.cashsense.feature.transaction.dialog.api.TransactionDialogNavKey
import ru.resodostudios.cashsense.feature.transaction.dialog.impl.TransactionDialog
import ru.resodostudios.cashsense.feature.transaction.dialog.impl.TransactionDialogViewModel
import ru.resodostudios.core.navigation.Navigator

fun EntryProviderScope<NavKey>.transactionDialogEntry(navigator: Navigator) {
    entry<TransactionDialogNavKey>(
        metadata = DialogSceneStrategy.dialog(),
    ) { key ->
        TransactionDialog(
            onDismiss = navigator::goBack,
            viewModel = hiltViewModel<TransactionDialogViewModel, TransactionDialogViewModel.Factory> {
                it.create(key)
            },
        )
    }
}