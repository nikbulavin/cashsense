package ru.resodostudios.cashsense.core.designsystem.component.button

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.ToggleButtonShapes
import androidx.compose.material3.TonalToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import ru.resodostudios.cashsense.core.designsystem.component.AnimatedIcon

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CsTonalToggleButton(
    checked: Boolean,
    title: String,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    shapes: ToggleButtonShapes = ToggleButtonDefaults.shapes(),
    enabled: Boolean = true,
) {
    val hapticFeedback = LocalHapticFeedback.current

    TonalToggleButton(
        checked = checked,
        onCheckedChange = { isChecked ->
            hapticFeedback.performHapticFeedback(
                if (isChecked) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff
            )
            onCheckedChange(isChecked)
        },
        modifier = modifier,
        shapes = shapes,
        enabled = enabled,
    ) {
        AnimatedIcon(
            icon = icon,
            iconSize = ToggleButtonDefaults.IconSize,
        )
        if (icon != null) Spacer(Modifier.width(ToggleButtonDefaults.IconSpacing))
        Text(
            text = title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}