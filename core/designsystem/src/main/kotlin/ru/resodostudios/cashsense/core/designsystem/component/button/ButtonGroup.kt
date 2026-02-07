package ru.resodostudios.cashsense.core.designsystem.component.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CsConnectedButtonGroup(
    selectedIndex: Int,
    options: List<String>,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    checkedIcon: ImageVector? = null,
    uncheckedIcons: List<ImageVector>? = null,
    enabled: Boolean = true,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
        modifier = modifier,
    ) {
        options.forEachIndexed { index, label ->
            val checked = selectedIndex == index
            CsTonalToggleButton(
                checked = checked,
                onCheckedChange = { onClick(index) },
                shapes = when (index) {
                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                    options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                },
                modifier = Modifier.weight(1f),
                icon = if (checked) checkedIcon else uncheckedIcons?.getOrNull(index),
                title = label,
                enabled = enabled,
            )
        }
    }
}