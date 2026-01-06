package ru.resodostudios.cashsense.feature.category.list.impl

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.component.CsListItem
import ru.resodostudios.cashsense.core.designsystem.component.CsListItemEmphasized
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Delete
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Edit
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.ui.component.StoredIcon
import ru.resodostudios.cashsense.core.locales.R as localesR

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun CategoryItem(
    category: Category,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onEditClick: (String) -> Unit = {},
    onDeleteClick: (String) -> Unit = {},
) {
    val effectsSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Float>()
    val spatialSpec = MaterialTheme.motionScheme.defaultSpatialSpec<IntSize>()

    Column(modifier = modifier) {
        CsListItem(
            headlineContent = {
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
        AnimatedVisibility(
            visible = selected,
            enter = fadeIn(effectsSpec) + expandVertically(spatialSpec),
            exit = fadeOut(effectsSpec) + shrinkVertically(spatialSpec),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            ) {
                CsListItemEmphasized(
                    content = { Text(stringResource(localesR.string.edit)) },
                    leadingContent = {
                        Icon(
                            imageVector = CsIcons.Outlined.Edit,
                            contentDescription = null,
                        )
                    },
                    onClick = { onEditClick(category.id) },
                    colors = ListItemDefaults.segmentedColors(),
                    shapes = ListItemDefaults.segmentedShapes(0, 2),
                )
                CsListItemEmphasized(
                    content = { Text(stringResource(localesR.string.delete)) },
                    leadingContent = {
                        Icon(
                            imageVector = CsIcons.Outlined.Delete,
                            contentDescription = null,
                        )
                    },
                    onClick = { onDeleteClick(category.id) },
                    colors = ListItemDefaults.segmentedColors(),
                    shapes = ListItemDefaults.segmentedShapes(1, 2),
                )
            }
        }
    }
}