package ru.resodostudios.cashsense.feature.category.list.impl.navigation

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult.ActionPerformed
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import ru.resodostudios.cashsense.core.ui.LocalSnackbarHostState
import ru.resodostudios.cashsense.feature.category.editor.api.navigateToCategoryEditor
import ru.resodostudios.cashsense.feature.category.list.api.CategoriesNavKey
import ru.resodostudios.cashsense.feature.category.list.impl.CategoriesScreen
import ru.resodostudios.core.navigation.Navigator

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun EntryProviderScope<NavKey>.categoriesEntry(navigator: Navigator) {
    entry<CategoriesNavKey>(
        metadata = ListDetailSceneStrategy.listPane(),
    ) {
        val snackbarHostState = LocalSnackbarHostState.current
        CategoriesScreen(
            onEditCategory = navigator::navigateToCategoryEditor,
            onShowSnackbar = { message, actionLabel ->
                snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = actionLabel,
                    duration = SnackbarDuration.Short,
                ) == ActionPerformed
            },
        )
    }
}