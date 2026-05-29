package ru.resodostudios.cashsense.core.designsystem.component.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconButtonDefaults.smallContainerSize
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize

@Composable
fun CsIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    tooltipPosition: TooltipAnchorPosition = TooltipAnchorPosition.Above,
    colors: IconButtonColors = IconButtonDefaults.iconButtonVibrantColors(),
    interactionSource: MutableInteractionSource? = null,
    enabled: Boolean = true,
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
            interactionSource = interactionSource,
            enabled = enabled,
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

@Composable
fun CsFilledTonalIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    tooltipPosition: TooltipAnchorPosition = TooltipAnchorPosition.Above,
    colors: IconButtonColors = IconButtonDefaults.filledTonalIconButtonColors(),
    containerSize: DpSize = smallContainerSize(),
    iconSize: Dp = IconButtonDefaults.smallIconSize,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
) {
    TooltipBox(
        modifier = modifier,
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
            positioning = tooltipPosition,
        ),
        tooltip = { PlainTooltip { Text(contentDescription) } },
        state = rememberTooltipState(),
    ) {
        FilledTonalIconButton(
            onClick = onClick,
            shapes = IconButtonDefaults.shapes(),
            colors = colors,
            modifier = Modifier
                .sizeIn(minHeight = containerSize.height, minWidth = containerSize.width)
                .fillMaxWidth(),
            enabled = enabled,
            interactionSource = interactionSource,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(iconSize),
            )
        }
    }
}

@Composable
fun CsFilledIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    tooltipPosition: TooltipAnchorPosition = TooltipAnchorPosition.Above,
    colors: IconButtonColors = IconButtonDefaults.filledIconButtonColors(),
    containerSize: DpSize = smallContainerSize(),
    iconSize: Dp = IconButtonDefaults.smallIconSize,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
) {
    TooltipBox(
        modifier = modifier,
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
            positioning = tooltipPosition,
        ),
        tooltip = { PlainTooltip { Text(contentDescription) } },
        state = rememberTooltipState(),
    ) {
        FilledIconButton(
            onClick = onClick,
            shapes = IconButtonDefaults.shapes(),
            colors = colors,
            modifier = Modifier
                .sizeIn(minHeight = containerSize.height, minWidth = containerSize.width)
                .fillMaxWidth(),
            enabled = enabled,
            interactionSource = interactionSource,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(iconSize),
            )
        }
    }
}

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