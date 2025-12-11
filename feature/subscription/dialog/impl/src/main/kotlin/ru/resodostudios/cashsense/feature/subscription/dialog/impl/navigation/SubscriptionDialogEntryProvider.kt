package ru.resodostudios.cashsense.feature.subscription.dialog.impl.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import ru.resodostudios.cashsense.feature.subscription.dialog.api.SubscriptionNavKey
import ru.resodostudios.cashsense.feature.subscription.dialog.impl.SubscriptionDialog
import ru.resodostudios.cashsense.feature.subscription.dialog.impl.SubscriptionDialogViewModel
import ru.resodostudios.core.navigation.Navigator

fun EntryProviderScope<NavKey>.subscriptionDialogEntry(navigator: Navigator) {
    entry<SubscriptionNavKey>(
        metadata = DialogSceneStrategy.dialog(),
    ) { key ->
        SubscriptionDialog(
            onDismiss = navigator::goBack,
            viewModel = hiltViewModel<SubscriptionDialogViewModel, SubscriptionDialogViewModel.Factory> {
                it.create(key)
            },
        )
    }
}