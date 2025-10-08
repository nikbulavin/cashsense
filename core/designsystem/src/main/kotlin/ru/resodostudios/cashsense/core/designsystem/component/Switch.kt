package ru.resodostudios.cashsense.core.designsystem.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Check
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Close

@Composable
fun CsSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val hapticFeedback = LocalHapticFeedback.current
    Switch(
        checked = checked,
        onCheckedChange = if (onCheckedChange != null) { isChecked ->
            hapticFeedback.performHapticFeedback(
                if (isChecked) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff
            )
            onCheckedChange(isChecked)
        } else null,
        thumbContent = {
            AnimatedIcon(
                icon = if (checked) CsIcons.Outlined.Check else CsIcons.Outlined.Close,
                modifier = Modifier.size(SwitchDefaults.IconSize),
            )
        },
        modifier = modifier,
    )
}