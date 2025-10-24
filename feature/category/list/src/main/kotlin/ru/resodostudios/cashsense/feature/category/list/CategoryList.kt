package ru.resodostudios.cashsense.feature.category.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import ru.resodostudios.cashsense.core.designsystem.component.ListItemPositionShapes
import ru.resodostudios.cashsense.core.model.data.Category

internal fun LazyGridScope.categories(
    categories: List<Category>,
    onCategoryClick: (Category?) -> Unit,
    selectedCategory: Category? = null,
) {
    itemsIndexed(
        items = categories,
        key = { _, category -> category.id!! },
        contentType = { _, _ -> "Category" },
    ) { index, category ->
        val shape = when {
            index == 0 && categories.size == 1 -> ListItemPositionShapes.Single
            index == 0 -> ListItemPositionShapes.First
            index == categories.lastIndex -> ListItemPositionShapes.Last
            else -> ListItemPositionShapes.Middle
        }
        val selected = category == selectedCategory

        CategoryItem(
            category = category,
            modifier = Modifier
                .clip(shape)
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .clickable { onCategoryClick(if (selected) null else category) }
                .animateItem(),
        )
    }
}