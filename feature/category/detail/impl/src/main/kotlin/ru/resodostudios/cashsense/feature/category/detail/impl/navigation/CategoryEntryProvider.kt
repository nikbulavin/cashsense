package ru.resodostudios.cashsense.feature.category.detail.impl.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import ru.resodostudios.cashsense.feature.category.detail.api.CategoryNavKey
import ru.resodostudios.cashsense.feature.category.detail.impl.CategoryScreen
import ru.resodostudios.cashsense.feature.category.editor.api.navigateToCategoryEditor
import ru.resodostudios.core.navigation.Navigator

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun EntryProviderScope<NavKey>.categoryEntry(navigator: Navigator) {
    entry<CategoryNavKey>(
        metadata = ListDetailSceneStrategy.detailPane(),
    ) {
        CategoryScreen(
            onEditCategory = navigator::navigateToCategoryEditor,
        )
    }
}