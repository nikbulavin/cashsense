package ru.resodostudios.cashsense.feature.settings.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import ru.resodostudios.cashsense.core.designsystem.component.CsAlertDialog
import ru.resodostudios.cashsense.core.designsystem.component.CsSelectableListItem
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Language
import ru.resodostudios.cashsense.core.model.data.Language
import ru.resodostudios.cashsense.core.locales.R as localesR

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun LanguageDialog(
    language: String,
    availableLanguages: List<Language>,
    onLanguageClick: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var languageState by rememberSaveable { mutableStateOf(language) }

    CsAlertDialog(
        titleRes = localesR.string.language,
        confirmButtonTextRes = localesR.string.ok,
        dismissButtonTextRes = localesR.string.cancel,
        icon = CsIcons.Outlined.Language,
        onConfirm = {
            onLanguageClick(languageState)
            onDismiss()
        },
        onDismiss = onDismiss,
        modifier = modifier,
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
        ) {
            itemsIndexed(
                items = availableLanguages,
                key = { _, language -> language.code },
            ) { index, language ->
                val selected = language.code == languageState
                CsSelectableListItem(
                    content = {
                        Text(
                            text = language.displayName,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    selected = selected,
                    onClick = { languageState = language.code },
                    trailingContent = {
                        RadioButton(
                            selected = selected,
                            onClick = null,
                        )
                    },
                    shapes = ListItemDefaults.segmentedShapes(index, availableLanguages.size),
                    colors = ListItemDefaults.segmentedColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    ),
                )
            }
        }
    }
}