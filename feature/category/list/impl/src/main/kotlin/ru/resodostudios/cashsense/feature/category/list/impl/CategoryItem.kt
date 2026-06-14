package ru.resodostudios.cashsense.feature.category.list.impl

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import ru.resodostudios.cashsense.core.designsystem.component.CsSelectableListItem
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.ui.component.StoredIcon

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun CategoryItem(
    category: Category,
    onClick: (Category) -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    shapes: ListItemShapes = ListItemDefaults.shapes(),
) {
    CsSelectableListItem(
        shapes = shapes,
        modifier = modifier,
        selected = selected,
        onClick = { onClick(category) },
        content = {
            Text(
                text = category.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        leadingContent = {
            Icon(
                imageVector = StoredIcon.asImageVector(category.iconId),
                contentDescription = null,
            )
        },
    )
}