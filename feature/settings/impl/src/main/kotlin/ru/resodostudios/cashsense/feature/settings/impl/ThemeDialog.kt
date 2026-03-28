package ru.resodostudios.cashsense.feature.settings.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import ru.resodostudios.cashsense.core.designsystem.component.CsAlertDialog
import ru.resodostudios.cashsense.core.designsystem.component.CsSelectableListItem
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.filled.Palette
import ru.resodostudios.cashsense.core.model.data.DarkThemeConfig
import ru.resodostudios.cashsense.core.locales.R as localesR

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun ThemeDialog(
    themeConfig: DarkThemeConfig,
    themeOptions: List<Pair<String, ImageVector>>,
    onThemeConfigUpdate: (DarkThemeConfig) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CsAlertDialog(
        titleRes = localesR.string.theme,
        confirmButtonTextRes = localesR.string.ok,
        dismissButtonTextRes = localesR.string.cancel,
        icon = CsIcons.Filled.Palette,
        onConfirm = onDismiss,
        onDismiss = onDismiss,
        modifier = modifier,
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
        ) {
            itemsIndexed(
                items = themeOptions,
                key = { _, option -> option.first},
            ) { index, (label, icon) ->
                val selected = themeConfig.ordinal == index
                CsSelectableListItem(
                    content = {
                        Text(
                            text = label,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    selected = selected,
                    onClick = { onThemeConfigUpdate(DarkThemeConfig.entries[index]) },
                    trailingContent = {
                        RadioButton(
                            selected = selected,
                            onClick = null,
                        )
                    },
                    leadingContent = {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                        )
                    },
                    shapes = ListItemDefaults.segmentedShapes(index, themeOptions.size),
                    colors = ListItemDefaults.segmentedColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    ),
                )
            }
        }
    }
}