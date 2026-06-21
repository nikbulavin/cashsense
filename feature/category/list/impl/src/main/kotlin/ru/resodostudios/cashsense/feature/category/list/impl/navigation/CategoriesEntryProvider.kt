package ru.resodostudios.cashsense.feature.category.list.impl.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import ru.resodostudios.cashsense.core.ui.LocalIsNavRailVisible
import ru.resodostudios.cashsense.core.ui.LocalIsSinglePane
import ru.resodostudios.cashsense.core.ui.component.DetailPanePlaceholder
import ru.resodostudios.cashsense.core.ui.component.FabMenuItem.CATEGORY
import ru.resodostudios.cashsense.core.ui.component.FabMenuItem.SUBSCRIPTION
import ru.resodostudios.cashsense.core.ui.component.FabMenuItem.WALLET
import ru.resodostudios.cashsense.feature.category.detail.api.navigateToCategory
import ru.resodostudios.cashsense.feature.category.editor.api.navigateToCategoryEditor
import ru.resodostudios.cashsense.feature.category.list.api.CategoriesNavKey
import ru.resodostudios.cashsense.feature.category.list.impl.CategoriesScreen
import ru.resodostudios.cashsense.feature.subscription.dialog.api.navigateToSubscriptionDialog
import ru.resodostudios.cashsense.feature.wallet.dialog.api.navigateToWalletDialog
import ru.resodostudios.core.navigation.Navigator
import ru.resodostudios.cashsense.core.locales.R as localesR

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun EntryProviderScope<NavKey>.categoriesEntry(navigator: Navigator) {
    entry<CategoriesNavKey>(
        metadata = ListDetailSceneStrategy.listPane {
            DetailPanePlaceholder(localesR.string.select_category)
        },
    ) {
        val isSinglePane = LocalIsSinglePane.current
        CategoriesScreen(
            navigateToCategory = navigator::navigateToCategory,
            shouldHighlightSelectedCategory = !isSinglePane,
            shouldShowFab = !isSinglePane,
            isNavRailVisible = LocalIsNavRailVisible.current,
            onFabMenuItemClick = { fabItem ->
                when (fabItem) {
                    WALLET -> navigator.navigateToWalletDialog()
                    CATEGORY -> navigator.navigateToCategoryEditor()
                    SUBSCRIPTION -> navigator.navigateToSubscriptionDialog()
                }
            },
        )
    }
}
