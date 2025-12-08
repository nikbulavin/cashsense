package ru.resodostudios.cashsense.feature.category.dialog.impl.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.dialog
import kotlinx.serialization.Serializable
import ru.resodostudios.cashsense.feature.category.dialog.impl.CategoryDialog

@Serializable
data class CategoryDialogRoute(
    val categoryId: String? = null,
)

fun NavController.navigateToCategoryDialog(
    categoryId: String? = null,
) = navigate(route = CategoryDialogRoute(categoryId)) {
    launchSingleTop = true
}

fun NavGraphBuilder.categoryDialog(
    onDismiss: () -> Unit,
) {
    dialog<CategoryDialogRoute> {
        CategoryDialog(
            onDismiss = onDismiss,
        )
    }
}