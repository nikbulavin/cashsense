package ru.resodostudios.cashsense.feature.transaction.editor.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component1
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component2
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.result.LocalResultEventBus
import ru.resodostudios.cashsense.core.analytics.AnalyticsEvent
import ru.resodostudios.cashsense.core.analytics.LocalAnalyticsHelper
import ru.resodostudios.cashsense.core.designsystem.component.CsOutlinedTextField
import ru.resodostudios.cashsense.core.designsystem.component.button.ConnectedTonalToggleButtonGroup
import ru.resodostudios.cashsense.core.designsystem.component.button.CsButton
import ru.resodostudios.cashsense.core.designsystem.component.button.CsIconButton
import ru.resodostudios.cashsense.core.designsystem.component.button.CsTonalToggleButton
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.ArrowBack
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Block
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Calendar
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Check
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.CheckCircle
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Pending
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.TrendingDown
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.TrendingUp
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.ui.component.DatePickerTextField
import ru.resodostudios.cashsense.core.ui.component.LoadingState
import ru.resodostudios.cashsense.core.ui.component.TimePickerTextField
import ru.resodostudios.cashsense.core.ui.model.StoredIcon
import ru.resodostudios.cashsense.core.ui.util.TrackScreenViewEvent
import ru.resodostudios.cashsense.core.ui.util.isAmountValid
import ru.resodostudios.cashsense.core.ui.util.logNewItemAdded
import kotlin.time.Instant
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
internal fun TransactionEditorScreen(
    onBackClick: () -> Unit,
    viewModel: TransactionEditorViewModel = hiltViewModel(),
) {
    val transactionEditorState by viewModel.transactionEditorState.collectAsStateWithLifecycle()

    TransactionEditorScreen(
        transactionEditorState = transactionEditorState,
        onBackClick = onBackClick,
        onTransactionSave = viewModel::saveTransaction,
        onTransactionTypeUpdate = viewModel::updateTransactionType,
        onAmountUpdate = viewModel::updateAmount,
        onCategoryUpdate = viewModel::updateCategory,
        onCompletionStatusUpdate = viewModel::updateCompletionStatus,
        onDateUpdate = viewModel::updateDate,
        onDescriptionUpdate = viewModel::updateDescription,
        onIgnoredStateUpdate = viewModel::updateIgnoredState,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun TransactionEditorScreen(
    transactionEditorState: TransactionEditorState,
    onBackClick: () -> Unit,
    onTransactionSave: () -> Unit,
    onTransactionTypeUpdate: (TransactionType) -> Unit,
    onAmountUpdate: (String) -> Unit,
    onCategoryUpdate: (Category?) -> Unit,
    onCompletionStatusUpdate: (Boolean) -> Unit,
    onDateUpdate: (Instant) -> Unit,
    onDescriptionUpdate: (String) -> Unit,
    onIgnoredStateUpdate: (Boolean) -> Unit,
) {
    TrackScreenViewEvent(screenName = "TransactionEditor")
    if (transactionEditorState.isLoading) {
        LoadingState(Modifier.fillMaxSize())
    } else {
        val (titleRes, confirmButtonTextRes) = if (transactionEditorState.transactionId.isNotEmpty()) {
            localesR.string.edit_transaction to localesR.string.save
        } else {
            localesR.string.new_transaction to localesR.string.add
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
                        val analyticsHelper = LocalAnalyticsHelper.current
                        val hapticFeedback = LocalHapticFeedback.current
                        val resultEventBus = LocalResultEventBus.current

                        CsButton(
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                                if (transactionEditorState.transactionId.isBlank()) {
                                    analyticsHelper.logNewItemAdded(
                                        itemType = AnalyticsEvent.ItemTypes.TRANSACTION,
                                    )
                                }
                                if (transactionEditorState.isFromImporter) {
                                    resultEventBus.sendResult(
                                        transactionEditorState.asTransaction()
                                    )
                                } else {
                                    onTransactionSave()
                                }
                                onBackClick()
                            },
                            enabled = transactionEditorState.amount.isAmountValid(),
                            title = stringResource(confirmButtonTextRes),
                        )
                    },
                )
            },
        ) { innerPadding ->

            val focusManager = LocalFocusManager.current
            val (descTextField, amountTextField) = remember { FocusRequester.createRefs() }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
            ) {
                TransactionTypeChoiceRow(
                    onTransactionTypeUpdate = onTransactionTypeUpdate,
                    transactionEditorState = transactionEditorState,
                )
                CsOutlinedTextField(
                    value = transactionEditorState.amount,
                    onValueChange = onAmountUpdate,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next,
                    ),
                    labelText = stringResource(localesR.string.amount) + "*",
                    supportingText = stringResource(localesR.string.required),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(amountTextField)
                        .focusProperties { next = descTextField },
                )
                CategoryDropdownMenu(
                    currentCategory = transactionEditorState.category,
                    categories = transactionEditorState.categories,
                    onCategoryClick = onCategoryUpdate,
                )
                TransactionStatusChoiceRow(
                    onCompletionStatusUpdate = onCompletionStatusUpdate,
                    transactionEditorState = transactionEditorState,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    DatePickerTextField(
                        timestamp = transactionEditorState.date,
                        labelRes = localesR.string.date,
                        icon = CsIcons.Outlined.Calendar,
                        modifier = Modifier.weight(1f),
                        onDateSelect = onDateUpdate,
                    )
                    TimePickerTextField(
                        onTimeSelect = onDateUpdate,
                        modifier = Modifier.weight(1f),
                        timestamp = transactionEditorState.date,
                    )
                }
                CsOutlinedTextField(
                    value = transactionEditorState.description,
                    onValueChange = onDescriptionUpdate,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() },
                    ),
                    labelText = stringResource(localesR.string.description),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(descTextField),
                )
                CsTonalToggleButton(
                    checked = transactionEditorState.ignored,
                    icon = if (transactionEditorState.ignored) CsIcons.Outlined.Check else CsIcons.Outlined.Block,
                    title = stringResource(localesR.string.transaction_ignore),
                    onCheckedChange = onIgnoredStateUpdate,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            LaunchedEffect(transactionEditorState.amount) {
                if (transactionEditorState.amount.isEmpty()) {
                    amountTextField.requestFocus()
                }
            }
        }
    }
}

@Composable
private fun TransactionTypeChoiceRow(
    onTransactionTypeUpdate: (TransactionType) -> Unit,
    transactionEditorState: TransactionEditorState,
) {
    ConnectedTonalToggleButtonGroup(
        selectedIndex = transactionEditorState.transactionType.ordinal,
        options = listOf(
            stringResource(localesR.string.expense),
            stringResource(localesR.string.income_singular),
        ),
        checkedIcon = CsIcons.Outlined.Check,
        uncheckedIcons = listOf(CsIcons.Outlined.TrendingDown, CsIcons.Outlined.TrendingUp),
        onClick = { onTransactionTypeUpdate(TransactionType.entries[it]) },
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun TransactionStatusChoiceRow(
    onCompletionStatusUpdate: (Boolean) -> Unit,
    transactionEditorState: TransactionEditorState,
) {
    ConnectedTonalToggleButtonGroup(
        selectedIndex = if (transactionEditorState.completed) 1 else 0,
        options = listOf(
            stringResource(localesR.string.pending),
            stringResource(localesR.string.completed),
        ),
        checkedIcon = CsIcons.Outlined.Check,
        uncheckedIcons = listOf(CsIcons.Outlined.Pending, CsIcons.Outlined.CheckCircle),
        onClick = { onCompletionStatusUpdate(it == 1) },
        modifier = Modifier.fillMaxWidth(),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropdownMenu(
    currentCategory: Category?,
    categories: List<Category?>,
    onCategoryClick: (Category?) -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var iconId by rememberSaveable { mutableIntStateOf(currentCategory?.iconId ?: 0) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        CsOutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            readOnly = true,
            value = currentCategory?.title ?: stringResource(localesR.string.none),
            onValueChange = {},
            labelText = stringResource(localesR.string.category_title),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            leadingIcon = {
                Icon(
                    imageVector = StoredIcon.asImageVector(iconId),
                    contentDescription = null,
                )
            },
            singleLine = true,
            enabled = categories.isNotEmpty(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = MenuDefaults.standaloneGroupShape,
            containerColor = MenuDefaults.groupVibrantContainerColor,
        ) {
            categories.forEachIndexed { index, category ->
                DropdownMenuItem(
                    shapes = MenuDefaults.itemShape(index, categories.size),
                    text = {
                        Text(
                            text = category?.title ?: stringResource(localesR.string.none),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    selected = index == categories.indexOf(currentCategory),
                    onClick = {
                        onCategoryClick(category)
                        iconId = category?.iconId ?: 0
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    leadingIcon = {
                        Icon(
                            imageVector = StoredIcon.asImageVector(category?.iconId),
                            contentDescription = null,
                        )
                    },
                    selectedLeadingIcon = {
                        Icon(
                            imageVector = CsIcons.Outlined.Check,
                            contentDescription = null,
                        )
                    },
                    colors = MenuDefaults.selectableItemVibrantColors(),
                )
            }
        }
    }
}