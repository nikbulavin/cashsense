package ru.resodostudios.cashsense.feature.category.editor.impl

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconButtonDefaults.mediumContainerSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.hapticfeedback.HapticFeedbackType.Companion.ToggleOn
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.designsystem.component.CsTextField
import ru.resodostudios.cashsense.core.designsystem.component.button.CsButton
import ru.resodostudios.cashsense.core.designsystem.component.button.CsIconButton
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.ArrowBack
import ru.resodostudios.cashsense.core.ui.component.LoadingState
import ru.resodostudios.cashsense.core.ui.component.StoredIcon
import ru.resodostudios.cashsense.core.ui.util.TrackScreenViewEvent
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
internal fun CategoryEditorScreen(
    onBackClick: () -> Unit,
    viewModel: CategoryEditorViewModel = hiltViewModel(),
) {
    val categoryEditorState by viewModel.categoryEditorState.collectAsStateWithLifecycle()

    CategoryEditorScreen(
        categoryEditorState = categoryEditorState,
        onBackClick = onBackClick,
        onSaveCategory = viewModel::saveCategory,
        onUpdateTitle = viewModel::updateTitle,
        onUpdateIconId = viewModel::updateIconId,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun CategoryEditorScreen(
    categoryEditorState: CategoryEditorState,
    onBackClick: () -> Unit,
    onSaveCategory: () -> Unit,
    onUpdateTitle: (String) -> Unit,
    onUpdateIconId: (Int) -> Unit,
) {
    TrackScreenViewEvent(screenName = "CategoryEditor")
    if (categoryEditorState.isLoading) {
        LoadingState(Modifier.fillMaxSize())
    } else {
        val (titleRes, confirmButtonTextRes) = if (categoryEditorState.id.isNotEmpty()) {
            localesR.string.edit_category to localesR.string.save
        } else {
            localesR.string.new_category to localesR.string.add
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(titleRes),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    navigationIcon = {
                        CsIconButton(
                            onClick = onBackClick,
                            icon = CsIcons.Outlined.ArrowBack,
                            contentDescription = stringResource(localesR.string.navigation_back_icon_description),
                            tooltipPosition = TooltipAnchorPosition.Right,
                        )
                    },
                    actions = {
                        val hapticFeedback = LocalHapticFeedback.current

                        CsButton(
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                                onSaveCategory()
                                onBackClick()
                            },
                            enabled = categoryEditorState.title.isNotBlank(),
                            title = stringResource(confirmButtonTextRes),
                        )
                    },
                )
            },
        ) { innerPadding ->
            val focusManager = LocalFocusManager.current
            val focusRequester = remember { FocusRequester() }

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
            ) {
                CsTextField(
                    value = categoryEditorState.title,
                    onValueChange = onUpdateTitle,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done,
                    ),
                    labelText = stringResource(localesR.string.title),
                    placeholderText = stringResource(localesR.string.title) + "*",
                    supportingText = stringResource(localesR.string.required),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    val icons = StoredIcon.entries.chunked(5)
                    icons.forEach { rowIcons ->
                        ButtonGroup(
                            overflowIndicator = {},
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            rowIcons.forEach { icon ->
                                customItem(
                                    buttonGroupContent = {
                                        val interactionSource = remember { MutableInteractionSource() }
                                        val hapticFeedback = LocalHapticFeedback.current
                                        val checked = categoryEditorState.iconId == icon.storedId
                                        val size = mediumContainerSize()
                                        FilledTonalIconToggleButton(
                                            shapes = IconButtonDefaults.toggleableShapes(),
                                            checked = checked,
                                            onCheckedChange = {
                                                if (!checked) hapticFeedback.performHapticFeedback(ToggleOn)
                                                onUpdateIconId(icon.storedId)
                                                focusManager.clearFocus()
                                            },
                                            modifier = Modifier
                                                .sizeIn(size.width, size.height)
                                                .weight(1f)
                                                .animateWidth(interactionSource),
                                            interactionSource = interactionSource,
                                        ) {
                                            Icon(
                                                imageVector = icon.imageVector,
                                                contentDescription = null,
                                                modifier = Modifier.size(IconButtonDefaults.mediumIconSize),
                                            )
                                        }
                                    },
                                    menuContent = {},
                                )
                            }
                        }
                    }
                }
            }
            LaunchedEffect(categoryEditorState.title) {
                if (categoryEditorState.title.isBlank()) {
                    focusRequester.requestFocus()
                }
            }
        }
    }
}
