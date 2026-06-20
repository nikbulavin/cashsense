package ru.resodostudios.cashsense.feature.category.list.impl

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemDefaults
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.model.Category

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
internal fun LazyListScope.categories(
    categories: List<Category>,
    onCategoryClick: (Category) -> Unit,
    shouldHighlightSelectedCategory: Boolean = false,
    selectedCategory: Category? = null,
) {
    itemsIndexed(
        items = categories,
        key = { _, category -> category.id },
        contentType = { _, _ -> "Category" },
    ) { index, category ->
        CategoryItem(
            category = category,
            modifier = Modifier.animateItem(),
            selected = shouldHighlightSelectedCategory && category == selectedCategory,
            onClick = { onCategoryClick(category) },
            shapes = if (categories.size == 1) {
                ListItemDefaults.shapes(shape = RoundedCornerShape(16.dp))
            } else {
                ListItemDefaults.segmentedShapes(index, categories.size)
            },
        )
    }
}