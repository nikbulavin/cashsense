package ru.resodostudios.cashsense.feature.transfer.impl.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import ru.resodostudios.cashsense.feature.transfer.dialog.api.TransferDialogNavKey
import ru.resodostudios.cashsense.feature.transfer.impl.TransferDialog
import ru.resodostudios.cashsense.feature.transfer.impl.TransferDialogViewModel
import ru.resodostudios.core.navigation.Navigator

fun EntryProviderScope<NavKey>.transferDialogEntry(navigator: Navigator) {
    entry<TransferDialogNavKey>(
        metadata = DialogSceneStrategy.dialog(),
    ) { key ->
        TransferDialog(
            onDismiss = navigator::goBack,
            viewModel = hiltViewModel<TransferDialogViewModel, TransferDialogViewModel.Factory> {
                it.create(key)
            },
        )
    }
}