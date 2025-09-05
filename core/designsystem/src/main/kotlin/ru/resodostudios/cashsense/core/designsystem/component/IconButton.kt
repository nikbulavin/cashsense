@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ru.resodostudios.cashsense.core.designsystem.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun CsIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    tooltipPosition: TooltipAnchorPosition = TooltipAnchorPosition.Above,
) {
    TooltipBox(
        modifier = modifier,
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
            positioning = tooltipPosition,
        ),
        tooltip = { PlainTooltip { Text(contentDescription) } },
        state = rememberTooltipState(),
    ) {
        IconButton(
            onClick = onClick,
            shapes = IconButtonDefaults.shapes(),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
            )
        }
    }
}

@Composable
fun CsOutlinedIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    tooltipPosition: TooltipAnchorPosition = TooltipAnchorPosition.Above,
) {
    TooltipBox(
        modifier = modifier,
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
            positioning = tooltipPosition,
        ),
        tooltip = { PlainTooltip { Text(contentDescription) } },
        state = rememberTooltipState(),
    ) {
        OutlinedIconButton(
            onClick = onClick,
            border = IconButtonDefaults.outlinedIconButtonBorder(true).copy(
                brush = SolidColor(MaterialTheme.colorScheme.outlineVariant),
            ),
            shapes = IconButtonDefaults.shapes(),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
            )
        }
    }
}