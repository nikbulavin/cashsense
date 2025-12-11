package ru.resodostudios.cashsense.feature.category.list.impl.navigation

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult.ActionPerformed
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import ru.resodostudios.cashsense.core.ui.LocalSnackbarHostState
import ru.resodostudios.cashsense.feature.category.dialog.api.navigateToCategoryDialog
import ru.resodostudios.cashsense.feature.category.list.api.CategoriesNavKey
import ru.resodostudios.cashsense.feature.category.list.impl.CategoriesScreen
import ru.resodostudios.core.navigation.Navigator

fun EntryProviderScope<NavKey>.categoriesEntry(navigator: Navigator) {
    entry<CategoriesNavKey> {
        val snackbarHostState = LocalSnackbarHostState.current
        CategoriesScreen(
            onEditCategory = navigator::navigateToCategoryDialog,
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