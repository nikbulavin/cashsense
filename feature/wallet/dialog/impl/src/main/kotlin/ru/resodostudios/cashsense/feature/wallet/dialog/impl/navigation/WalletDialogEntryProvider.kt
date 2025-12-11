package ru.resodostudios.cashsense.feature.wallet.dialog.impl.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import ru.resodostudios.cashsense.feature.wallet.dialog.api.WalletDialogNavKey
import ru.resodostudios.cashsense.feature.wallet.dialog.impl.WalletDialog
import ru.resodostudios.cashsense.feature.wallet.dialog.impl.WalletDialogViewModel
import ru.resodostudios.core.navigation.Navigator

fun EntryProviderScope<NavKey>.walletDialogEntry(navigator: Navigator) {
    entry<WalletDialogNavKey>(
        metadata = DialogSceneStrategy.dialog(),
    ) { key ->
        WalletDialog(
            onDismiss = navigator::goBack,
            viewModel = hiltViewModel<WalletDialogViewModel, WalletDialogViewModel.Factory> {
                it.create(key)
            },
        )
    }
}