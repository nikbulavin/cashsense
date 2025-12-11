package ru.resodostudios.cashsense.feature.category.list.impl.navigation

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult.ActionPerformed
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import ru.resodostudios.cashsense.core.ui.LocalSnackbarHostState
import ru.resodostudios.cashsense.feature.category.list.api.CategoriesNavKey
import ru.resodostudios.cashsense.feature.category.list.impl.CategoriesScreen
import ru.resodostudios.core.navigation.Navigator

@Serializable
data object CategoriesRoute

fun NavGraphBuilder.categoriesScreen(
    onEditCategory: (String) -> Unit,
) {
    composable<CategoriesRoute> {
        val snackbarHostState = LocalSnackbarHostState.current
        CategoriesScreen(
            onEditCategory = onEditCategory,
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

fun EntryProviderScope<NavKey>.categoriesEntry(navigator: Navigator) {
    entry<CategoriesNavKey> {
        val snackbarHostState = LocalSnackbarHostState.current
        CategoriesScreen(
            onEditCategory = {},
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