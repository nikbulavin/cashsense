package ru.resodostudios.cashsense.feature.category.list.impl.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import ru.resodostudios.cashsense.feature.category.detail.api.navigateToCategory
import ru.resodostudios.cashsense.feature.category.list.api.CategoriesNavKey
import ru.resodostudios.cashsense.feature.category.list.impl.CategoriesScreen
import ru.resodostudios.core.navigation.Navigator

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun EntryProviderScope<NavKey>.categoriesEntry(navigator: Navigator) {
    entry<CategoriesNavKey>(
        metadata = ListDetailSceneStrategy.listPane(),
    ) {
        CategoriesScreen(
            navigateToCategory = navigator::navigateToCategory,
        )
    }
}