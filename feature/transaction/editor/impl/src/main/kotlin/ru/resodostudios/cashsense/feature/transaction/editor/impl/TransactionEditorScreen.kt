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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
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
import ru.resodostudios.cashsense.core.analytics.AnalyticsEvent
import ru.resodostudios.cashsense.core.analytics.LocalAnalyticsHelper
import ru.resodostudios.cashsense.core.designsystem.component.button.ConnectedTonalToggleButtonGroup
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
import ru.resodostudios.cashsense.core.ui.component.StoredIcon
import ru.resodostudios.cashsense.core.ui.component.TimePickerTextField
import ru.resodostudios.cashsense.core.ui.util.TrackScreenViewEvent
import ru.resodostudios.cashsense.core.ui.util.isAmountValid
import ru.resodostudios.cashsense.core.ui.util.logNewItemAdded
import ru.resodostudios.cashsense.feature.transaction.editor.impl.TransactionDialogEvent.Save
import ru.resodostudios.cashsense.feature.transaction.editor.impl.TransactionDialogEvent.UpdateAmount
import ru.resodostudios.cashsense.feature.transaction.editor.impl.TransactionDialogEvent.UpdateCategory
import ru.resodostudios.cashsense.feature.transaction.editor.impl.TransactionDialogEvent.UpdateCompletionStatus
import ru.resodostudios.cashsense.feature.transaction.editor.impl.TransactionDialogEvent.UpdateDate
import ru.resodostudios.cashsense.feature.transaction.editor.impl.TransactionDialogEvent.UpdateDescription
import ru.resodostudios.cashsense.feature.transaction.editor.impl.TransactionDialogEvent.UpdateTransactionIgnoring
import ru.resodostudios.cashsense.feature.transaction.editor.impl.TransactionDialogEvent.UpdateTransactionType
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
internal fun TransactionEditorScreen(
    onBackClick: () -> Unit,
    viewModel: TransactionDialogViewModel = hiltViewModel(),
) {
    val transactionDialogState by viewModel.transactionDialogUiState.collectAsStateWithLifecycle()

    TransactionEditorScreen(
        transactionDialogState = transactionDialogState,
        onBackClick = onBackClick,
        onTransactionEvent = viewModel::onTransactionEvent,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TransactionEditorScreen(
    transactionDialogState: TransactionDialogUiState,
    onBackClick: () -> Unit,
    onTransactionEvent: (TransactionDialogEvent) -> Unit,
) {
    TrackScreenViewEvent(screenName = "TransactionEditor")
    if (transactionDialogState.isLoading) {
        LoadingState(Modifier.fillMaxSize())
    } else {
        val (titleRes, confirmButtonTextRes) = if (transactionDialogState.transactionId.isNotEmpty()) {
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
                        Button(
                            shapes = ButtonDefaults.shapes(),
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                                if (transactionDialogState.transactionId.isBlank()) {
                                    analyticsHelper.logNewItemAdded(
                                        itemType = AnalyticsEvent.ItemTypes.TRANSACTION,
                                    )
                                }
                                onTransactionEvent(Save(transactionDialogState))
                                onBackClick()
                            },
                            enabled = transactionDialogState.amount.isAmountValid(),
                        ) {
                            Text(stringResource(confirmButtonTextRes))
                        }
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
                    onTransactionEvent = onTransactionEvent,
                    transactionState = transactionDialogState,
                )
                OutlinedTextField(
                    value = transactionDialogState.amount,
                    onValueChange = { onTransactionEvent(UpdateAmount(it)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next,
                    ),
                    label = { Text(stringResource(localesR.string.amount)) },
                    placeholder = { Text(stringResource(localesR.string.amount) + "*") },
                    supportingText = { Text(stringResource(localesR.string.required)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(amountTextField)
                        .focusProperties { next = descTextField },
                )
                CategoryDropdownMenu(
                    currentCategory = transactionDialogState.category,
                    categories = transactionDialogState.categories,
                    onCategoryClick = { onTransactionEvent(UpdateCategory(it)) },
                )
                TransactionStatusChoiceRow(
                    onTransactionEvent = onTransactionEvent,
                    transactionState = transactionDialogState,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    DatePickerTextField(
                        timestamp = transactionDialogState.date,
                        labelRes = localesR.string.date,
                        icon = CsIcons.Outlined.Calendar,
                        modifier = Modifier.weight(1f),
                        onDateSelect = { onTransactionEvent(UpdateDate(it)) },
                    )
                    TimePickerTextField(
                        onTimeSelect = { onTransactionEvent(UpdateDate(it)) },
                        modifier = Modifier.weight(1f),
                        timestamp = transactionDialogState.date,
                    )
                }
                OutlinedTextField(
                    value = transactionDialogState.description,
                    onValueChange = { onTransactionEvent(UpdateDescription(it)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() },
                    ),
                    label = { Text(stringResource(localesR.string.description)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(descTextField),
                )
                CsTonalToggleButton(
                    checked = transactionDialogState.ignored,
                    icon = if (transactionDialogState.ignored) CsIcons.Outlined.Check else CsIcons.Outlined.Block,
                    title = stringResource(localesR.string.transaction_ignore),
                    onCheckedChange = { onTransactionEvent(UpdateTransactionIgnoring(it)) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            LaunchedEffect(transactionDialogState.amount) {
                if (transactionDialogState.amount.isEmpty()) {
                    amountTextField.requestFocus()
                }
            }
        }
    }
}

@Composable
private fun TransactionTypeChoiceRow(
    onTransactionEvent: (TransactionDialogEvent) -> Unit,
    transactionState: TransactionDialogUiState,
) {
    ConnectedTonalToggleButtonGroup(
        selectedIndex = transactionState.transactionType.ordinal,
        options = listOf(
            stringResource(localesR.string.expense),
            stringResource(localesR.string.income_singular),
        ),
        checkedIcon = CsIcons.Outlined.Check,
        uncheckedIcons = listOf(CsIcons.Outlined.TrendingDown, CsIcons.Outlined.TrendingUp),
        onClick = { onTransactionEvent(UpdateTransactionType(TransactionType.entries[it])) },
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun TransactionStatusChoiceRow(
    onTransactionEvent: (TransactionDialogEvent) -> Unit,
    transactionState: TransactionDialogUiState,
) {
    ConnectedTonalToggleButtonGroup(
        selectedIndex = if (transactionState.completed) 1 else 0,
        options = listOf(
            stringResource(localesR.string.pending),
            stringResource(localesR.string.completed),
        ),
        checkedIcon = CsIcons.Outlined.Check,
        uncheckedIcons = listOf(CsIcons.Outlined.Pending, CsIcons.Outlined.CheckCircle),
        onClick = { onTransactionEvent(UpdateCompletionStatus(it == 1)) },
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
    var expanded by remember { mutableStateOf(false) }
    var iconId by rememberSaveable { mutableIntStateOf(currentCategory?.iconId ?: 0) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            readOnly = true,
            value = currentCategory?.title ?: stringResource(localesR.string.none),
            onValueChange = {},
            label = { Text(stringResource(localesR.string.category_title)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            leadingIcon = {
                Icon(
                    imageVector = StoredIcon.asImageVector(iconId),
                    contentDescription = null,
                )
            },
            singleLine = true,
            enabled = categories.isNotEmpty(),
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
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