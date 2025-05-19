package ru.resodostudios.cashsense.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TonalToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Check

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CsIconToggleButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CsConnectedButtonGroup(
    selectedIndex: Int,
    options: List<String>,
    uncheckedIcons: List<ImageVector>,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hapticFeedback = LocalHapticFeedback.current

    Row(
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
        modifier = modifier,
    ) {
        options.forEachIndexed { index, label ->
            val checked = selectedIndex == index
            TonalToggleButton(
                checked = checked,
                onCheckedChange = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.ToggleOn)
                    onClick(index)
                },
                shapes = when (index) {
                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                    options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                },
                modifier = Modifier.weight(1f),
            ) {
                Icon(
                    imageVector = if (checked) CsIcons.Outlined.Check else uncheckedIcons[index],
                    contentDescription = null,
                )
                Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
                Text(
                    text = label,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}