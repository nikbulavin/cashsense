package ru.resodostudios.cashsense.feature.category.editor.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import ru.resodostudios.core.navigation.Navigator

@Serializable
data class CategoryEditorNavKey(
    val categoryId: String? = null,
) : NavKey

fun Navigator.navigateToCategoryEditor(
    categoryId: String? = null,
) {
    navigate(CategoryEditorNavKey(categoryId))
}
