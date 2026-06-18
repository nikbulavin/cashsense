package ru.resodostudios.cashsense.feature.category.detail.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import ru.resodostudios.core.navigation.Navigator

@Serializable
data class CategoryNavKey(
    val categoryId: String,
) : NavKey

fun Navigator.navigateToCategory(
    categoryId: String,
) {
    navigate(CategoryNavKey(categoryId))
}