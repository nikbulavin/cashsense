package ru.resodostudios.cashsense.feature.category.list.impl

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import ru.resodostudios.cashsense.core.designsystem.component.CsSelectableListItem
import ru.resodostudios.cashsense.core.designsystem.theme.LocalSharedTransitionScope
import ru.resodostudios.cashsense.core.designsystem.theme.SharedElementKey
import ru.resodostudios.cashsense.core.designsystem.theme.SharedElementType
import ru.resodostudios.cashsense.core.designsystem.theme.sharedBoundsAdaptive
import ru.resodostudios.cashsense.core.model.Category
import ru.resodostudios.cashsense.core.ui.model.StoredIcon

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun CategoryItem(
    category: Category,
    onClick: (Category) -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    shapes: ListItemShapes = ListItemDefaults.shapes(),
) {
    with(LocalSharedTransitionScope.current) {
        CsSelectableListItem(
            shapes = shapes,
            modifier = modifier.sharedBoundsAdaptive(
                sharedContentState = rememberSharedContentState(
                    key = SharedElementKey(
                        id = category.id,
                        origin = category.id,
                        type = SharedElementType.Bounds,
                    ),
                ),
                placeholderSize = SharedTransitionScope.PlaceholderSize.AnimatedSize,
            ),
            selected = selected,
            onClick = { onClick(category) },
            content = {
                Text(
                    text = category.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.sharedBoundsAdaptive(
                        sharedContentState = rememberSharedContentState(
                            key = SharedElementKey(
                                id = category.id,
                                origin = category.title,
                                type = SharedElementType.CategoryTitle,
                            ),
                        ),
                        resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(),
                    )
                )
            },
            leadingContent = {
                val icon = StoredIcon.asImageVector(category.iconId)
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.sharedBoundsAdaptive(
                        sharedContentState = rememberSharedContentState(
                            key = SharedElementKey(
                                id = category.id,
                                origin = icon.toString(),
                                type = SharedElementType.CategoryIcon,
                            ),
                        ),
                        resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(),
                    ),
                )
            },
        )
    }
}