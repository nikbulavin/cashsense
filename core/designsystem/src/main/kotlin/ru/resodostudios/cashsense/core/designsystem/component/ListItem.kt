package ru.resodostudios.cashsense.core.designsystem.component

import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun CsListItem(
    headlineContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    overlineContent: @Composable (() -> Unit)? = null,
    supportingContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
) {
    ListItem(
        headlineContent = headlineContent,
        modifier = modifier,
        overlineContent = overlineContent,
        supportingContent = supportingContent,
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent,
        ),
    )
}

@Composable
fun CsToggableListItem(
    headlineContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = ListItemPositionShapes.Single,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    checked: Boolean = false,
    overlineContent: @Composable (() -> Unit)? = null,
    supportingContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    colors: ListItemColors = ListItemDefaults.colors(
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
    ),
) {
    val hapticFeedback = LocalHapticFeedback.current
    ListItem(
        headlineContent = headlineContent,
        modifier = modifier
            .clip(shape)
            .then(
                if (onCheckedChange != null) {
                    Modifier.toggleable(
                        value = checked,
                        onValueChange = { isChecked ->
                            hapticFeedback.performHapticFeedback(
                                if (isChecked) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff
                            )
                            onCheckedChange(isChecked)
                        },
                        role = Role.Switch,
                    )
                } else {
                    modifier
                }
            ),
        overlineContent = overlineContent,
        supportingContent = supportingContent,
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        colors = colors,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CsListItemEmphasized(
    content: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    overlineContent: @Composable (() -> Unit)? = null,
    supportingContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    colors: ListItemColors = ListItemDefaults.colors(
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
    ),
    shapes: ListItemShapes = ListItemDefaults.shapes(),
) {
    ListItem(
        shapes = shapes,
        onClick = onClick,
        content = content,
        modifier = modifier,
        overlineContent = overlineContent,
        supportingContent = supportingContent,
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        colors = colors,
    )
}

/**
 * Defines the shapes for list items based on their position in a list.
 * This is used to create a visually cohesive list where the corners of items
 * are rounded differently depending on whether the item is at the beginning,
 * middle, or end of the list, or if it's a single item.
 *
 * - `Single`: Used for a standalone list item. Has large rounded corners on all sides.
 * - `Middle`: Used for items in the middle of a list. Has small rounded corners on all sides.
 * - `First`: Used for the first item in a list. Has large rounded corners on the top and small rounded corners on the bottom.
 * - `Last`: Used for the last item in a list. Has small rounded corners on the top and large rounded corners on the bottom.
 */
object ListItemPositionShapes {

    private val largeCornerRadius = 18.dp
    private val smallCornerRadius = 6.dp

    val Single = RoundedCornerShape(largeCornerRadius)
    val Middle = RoundedCornerShape(smallCornerRadius)
    val First = RoundedCornerShape(
        topStart = largeCornerRadius,
        topEnd = largeCornerRadius,
        bottomEnd = smallCornerRadius,
        bottomStart = smallCornerRadius,
    )
    val Last = RoundedCornerShape(
        topStart = smallCornerRadius,
        topEnd = smallCornerRadius,
        bottomEnd = largeCornerRadius,
        bottomStart = largeCornerRadius,
    )
}