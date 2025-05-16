package ru.resodostudios.cashsense.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CsListItem(
    headlineContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    overlineContent: @Composable (() -> Unit)? = null,
    supportingContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
) {
    ListItem(
        headlineContent = headlineContent,
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else modifier
            ),
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
fun CsListItemEmphasized(
    headlineContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = ListItemShape.Single,
    onClick: (() -> Unit)? = null,
    overlineContent: @Composable (() -> Unit)? = null,
    supportingContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
) {
    ListItem(
        headlineContent = headlineContent,
        modifier = modifier
            .clip(shape)
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            ),
        overlineContent = overlineContent,
        supportingContent = supportingContent,
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
    )
}

/**
 * An object that provides different shapes for list items.
 * It defines shapes for single, middle, first, and last items in a list.
 *
 * - `Single`: A shape with large rounded corners on all sides, suitable for a standalone list item.
 * - `Middle`: A shape with small rounded corners on all sides, suitable for items in the middle of a list.
 * - `First`: A shape with large rounded corners on the top and small rounded corners on the bottom, suitable for the first item in a list.
 * - `Last`: A shape with small rounded corners on the top and large rounded corners on the bottom, suitable for the last item in a list.
 */
object ListItemShape {

    private val largeCorner = 18.dp
    private val smallCorner = 6.dp

    val Single = RoundedCornerShape(largeCorner)
    val Middle = RoundedCornerShape(smallCorner)
    val First = RoundedCornerShape(largeCorner, largeCorner, smallCorner, smallCorner)
    val Last = RoundedCornerShape(smallCorner, smallCorner, largeCorner, largeCorner)
}