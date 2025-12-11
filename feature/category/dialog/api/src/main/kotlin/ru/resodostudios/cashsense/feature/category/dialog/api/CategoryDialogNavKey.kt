package ru.resodostudios.cashsense.feature.category.dialog.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import ru.resodostudios.core.navigation.Navigator

@Serializable
data class CategoryDialogNavKey(
    val categoryId: String? = null,
) : NavKey

fun Navigator.navigateToCategoryDialog(
    categoryId: String? = null,
) {
    navigate(CategoryDialogNavKey(categoryId))
}