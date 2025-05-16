package ru.resodostudios.cashsense.core.designsystem.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
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
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hapticFeedback = LocalHapticFeedback.current
    Switch(
        checked = checked,
        onCheckedChange = { isChecked ->
            hapticFeedback.performHapticFeedback(
                if (isChecked) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff
            )
            onCheckedChange(isChecked)
        },
        thumbContent = {
            if (checked) {
                Icon(
                    imageVector = CsIcons.Outlined.Check,
                    contentDescription = null,
                    modifier = Modifier.size(SwitchDefaults.IconSize),
                )
            } else {
                Icon(
                    imageVector = CsIcons.Outlined.Close,
                    contentDescription = null,
                    modifier = Modifier.size(SwitchDefaults.IconSize),
                )
            }
        },
        modifier = modifier,
    )
}