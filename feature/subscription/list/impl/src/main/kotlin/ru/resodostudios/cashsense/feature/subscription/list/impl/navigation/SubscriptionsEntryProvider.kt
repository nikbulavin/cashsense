package ru.resodostudios.cashsense.feature.subscription.list.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import ru.resodostudios.cashsense.feature.subscription.dialog.api.navigateToSubscriptionDialog
import ru.resodostudios.cashsense.feature.subscription.list.api.SubscriptionsNavKey
import ru.resodostudios.cashsense.feature.subscription.list.impl.SubscriptionsScreen
import ru.resodostudios.core.navigation.Navigator

fun EntryProviderScope<NavKey>.subscriptionsEntry(navigator: Navigator) {
    entry<SubscriptionsNavKey> {
        SubscriptionsScreen(
            onEditSubscription = navigator::navigateToSubscriptionDialog,
        )
    }
}