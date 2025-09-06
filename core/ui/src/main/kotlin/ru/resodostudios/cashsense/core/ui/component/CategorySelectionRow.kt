package ru.resodostudios.cashsense.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.component.AnimatedIcon
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Check
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.ui.CategoryPreviewParameterProvider

@Composable
fun CategorySelectionRow(
    availableCategories: List<Category>,
    selectedCategories: Set<Category>,
    onCategorySelect: (Category) -> Unit,
    onCategoryDeselect: (Category) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        availableCategories.forEach { category ->
            val selected = category in selectedCategories
            CategoryChip(
                selected = selected,
                category = category,
                onClick = {
                    if (selected) onCategoryDeselect(category) else onCategorySelect(category)
                },
            )
        }
    }
}

@Composable
private fun CategoryChip(
    selected: Boolean,
    category: Category,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hapticFeedback = LocalHapticFeedback.current

    FilterChip(
        selected = selected,
        onClick = {
            hapticFeedback.performHapticFeedback(
                if (selected) HapticFeedbackType.ToggleOff else HapticFeedbackType.ToggleOn
            )
            onClick()
        },
        label = {
            Text(
                text = category.title.toString(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        leadingIcon = {
            val icon = if (selected) CsIcons.Outlined.Check else StoredIcon.asImageVector(category.iconId)
            AnimatedIcon(
                icon = icon,
                iconSize = FilterChipDefaults.IconSize,
            )
        },
        modifier = modifier,
    )
}

@Preview
@Composable
private fun CategorySelectorRowPreview(
    @PreviewParameter(CategoryPreviewParameterProvider::class)
    categories: List<Category>,
) {
    CsTheme {
        Surface {
            CategorySelectionRow(
                availableCategories = categories,
                selectedCategories = setOf(categories.first()),
                onCategorySelect = {},
                onCategoryDeselect = {},
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}