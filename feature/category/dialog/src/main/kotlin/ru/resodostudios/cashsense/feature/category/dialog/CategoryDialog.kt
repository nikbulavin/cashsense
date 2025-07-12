package ru.resodostudios.cashsense.feature.category.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.designsystem.component.CsAlertDialog
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Category
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.ui.component.IconPickerDropdownMenu
import ru.resodostudios.cashsense.core.ui.component.LoadingState
import ru.resodostudios.cashsense.core.ui.component.StoredIcon
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
fun CategoryDialog(
    onDismiss: () -> Unit,
    viewModel: CategoryDialogViewModel = hiltViewModel(),
) {
    val categoryDialogState by viewModel.categoryDialogUiState.collectAsStateWithLifecycle()

    CategoryDialog(
        categoryDialogState = categoryDialogState,
        onSaveCategory = viewModel::saveCategory,
        onUpdateTitle = viewModel::updateTitle,
        onUpdateIconId = viewModel::updateIconId,
        onDismiss = onDismiss,
    )
}

@Composable
fun CategoryDialog(
    categoryDialogState: CategoryDialogUiState,
    onSaveCategory: (Category) -> Unit,
    onUpdateTitle: (String) -> Unit,
    onUpdateIconId: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val (dialogTitle, dialogConfirmText) = if (categoryDialogState.id.isNotEmpty()) {
        localesR.string.edit_category to localesR.string.save
    } else {
        localesR.string.new_category to localesR.string.add
    }

    CsAlertDialog(
        titleRes = dialogTitle,
        confirmButtonTextRes = dialogConfirmText,
        dismissButtonTextRes = localesR.string.cancel,
        icon = CsIcons.Outlined.Category,
        onConfirm = {
            onSaveCategory(categoryDialogState.asCategory())
            onDismiss()
        },
        isConfirmEnabled = categoryDialogState.title.isNotBlank(),
        onDismiss = onDismiss,
    ) {
        if (categoryDialogState.isLoading) {
            LoadingState(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
            )
        } else {
            val focusManager = LocalFocusManager.current
            val focusRequester = remember { FocusRequester() }

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.verticalScroll(rememberScrollState()),
            ) {
                OutlinedTextField(
                    value = categoryDialogState.title,
                    onValueChange = onUpdateTitle,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done,
                    ),
                    label = { Text(stringResource(localesR.string.icon_and_title)) },
                    placeholder = { Text(stringResource(localesR.string.title) + "*") },
                    supportingText = { Text(stringResource(localesR.string.required)) },
                    maxLines = 1,
                    leadingIcon = {
                        IconPickerDropdownMenu(
                            currentIcon = StoredIcon.asImageVector(categoryDialogState.iconId),
                            onSelectedIconClick = onUpdateIconId,
                            onClick = { focusManager.clearFocus() },
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                )
            }
            LaunchedEffect(categoryDialogState.title) {
                if (categoryDialogState.title.isBlank()) {
                    focusRequester.requestFocus()
                }
            }
        }
    }
}