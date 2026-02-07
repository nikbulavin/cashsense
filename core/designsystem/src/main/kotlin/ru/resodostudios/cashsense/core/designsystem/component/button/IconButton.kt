@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ru.resodostudios.cashsense.core.designsystem.component.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

@Composable
fun CsIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    tooltipPosition: TooltipAnchorPosition = TooltipAnchorPosition.Above,
    colors: IconButtonColors = IconButtonDefaults.iconButtonVibrantColors(),
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
            colors = colors,
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
    colors: IconButtonColors = IconButtonDefaults.outlinedIconButtonVibrantColors(),
    border: BorderStroke = IconButtonDefaults.outlinedIconButtonVibrantBorder(true),
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
            shapes = IconButtonDefaults.shapes(),
            colors = colors,
            border = border,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CsIconToggleButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
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
        val hapticFeedback = LocalHapticFeedback.current
        IconToggleButton(
            shapes = IconButtonDefaults.toggleableShapes(),
            checked = checked,
            onCheckedChange = { isChecked ->
                hapticFeedback.performHapticFeedback(
                    if (isChecked) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff
                )
                onCheckedChange(isChecked)
            },
            modifier = modifier,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CsFilledTonalIconToggleButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
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
        val hapticFeedback = LocalHapticFeedback.current
        FilledTonalIconToggleButton(
            shapes = IconButtonDefaults.toggleableShapes(),
            checked = checked,
            onCheckedChange = { isChecked ->
                hapticFeedback.performHapticFeedback(
                    if (isChecked) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff
                )
                onCheckedChange(isChecked)
            },
            modifier = modifier,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
            )
        }
    }
}