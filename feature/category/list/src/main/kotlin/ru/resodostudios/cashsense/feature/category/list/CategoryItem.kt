package ru.resodostudios.cashsense.feature.category.list

import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import ru.resodostudios.cashsense.core.designsystem.component.CsListItemEmphasized
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.ui.component.StoredIcon

@Composable
internal fun CategoryItem(
    category: Category,
    modifier: Modifier = Modifier,
) {
    CsListItemEmphasized(
        headlineContent = {
            Text(
                text = category.title.toString(),
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
        modifier = modifier,
        colors = ListItemDefaults.colors().copy(
            containerColor = Color.Transparent,
        ),
    )
}