package ru.resodostudios.cashsense.feature.category.list.navigation

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult.ActionPerformed
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import ru.resodostudios.cashsense.core.ui.LocalSnackbarHostState
import ru.resodostudios.cashsense.feature.category.list.CategoriesScreen

@Serializable
data object CategoriesRoute

fun NavController.navigateToCategories(navOptions: NavOptions? = null) {
    navigate(route = CategoriesRoute, navOptions)
}

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