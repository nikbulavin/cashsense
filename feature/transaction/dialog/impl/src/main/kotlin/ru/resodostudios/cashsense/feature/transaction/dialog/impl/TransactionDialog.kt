package ru.resodostudios.cashsense.feature.transaction.dialog.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.analytics.AnalyticsEvent
import ru.resodostudios.cashsense.core.analytics.LocalAnalyticsHelper
import ru.resodostudios.cashsense.core.designsystem.component.CsAlertDialog
import ru.resodostudios.cashsense.core.designsystem.component.button.CsConnectedButtonGroup
import ru.resodostudios.cashsense.core.designsystem.component.button.CsTonalToggleButton
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Block
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Calendar
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Check
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.CheckCircle
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Pending
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.ReceiptLong
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
import ru.resodostudios.cashsense.feature.transaction.dialog.impl.TransactionDialogEvent.Save
import ru.resodostudios.cashsense.feature.transaction.dialog.impl.TransactionDialogEvent.UpdateAmount
import ru.resodostudios.cashsense.feature.transaction.dialog.impl.TransactionDialogEvent.UpdateCategory
import ru.resodostudios.cashsense.feature.transaction.dialog.impl.TransactionDialogEvent.UpdateCompletionStatus
import ru.resodostudios.cashsense.feature.transaction.dialog.impl.TransactionDialogEvent.UpdateDate
import ru.resodostudios.cashsense.feature.transaction.dialog.impl.TransactionDialogEvent.UpdateDescription
import ru.resodostudios.cashsense.feature.transaction.dialog.impl.TransactionDialogEvent.UpdateTransactionIgnoring
import ru.resodostudios.cashsense.feature.transaction.dialog.impl.TransactionDialogEvent.UpdateTransactionType
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
internal fun TransactionDialog(
    onDismiss: () -> Unit,
    viewModel: TransactionDialogViewModel = hiltViewModel(),
) {
    val transactionDialogState by viewModel.transactionDialogUiState.collectAsStateWithLifecycle()

    TransactionDialog(
        transactionDialogState = transactionDialogState,
        onDismiss = onDismiss,
        onTransactionEvent = viewModel::onTransactionEvent,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TransactionDialog(
    transactionDialogState: TransactionDialogUiState,
    onDismiss: () -> Unit,
    onTransactionEvent: (TransactionDialogEvent) -> Unit,
) {
    val (titleRes, confirmButtonTextRes) = if (transactionDialogState.transactionId.isNotEmpty()) {
        localesR.string.edit_transaction to localesR.string.save
    } else {
        localesR.string.new_transaction to localesR.string.add
    }
    val analyticsHelper = LocalAnalyticsHelper.current

    CsAlertDialog(
        titleRes = titleRes,
        confirmButtonTextRes = confirmButtonTextRes,
        dismissButtonTextRes = localesR.string.cancel,
        icon = CsIcons.Outlined.ReceiptLong,
        onConfirm = {
            onTransactionEvent(Save(transactionDialogState))
            if (transactionDialogState.transactionId.isBlank()) {
                analyticsHelper.logNewItemAdded(
                    itemType = AnalyticsEvent.ItemTypes.TRANSACTION,
                )
            }
            onDismiss()
        },
        isConfirmEnabled = transactionDialogState.amount.isAmountValid(),
        onDismiss = onDismiss,
    ) {
        if (transactionDialogState.isLoading) {
            LoadingState(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
            )
        } else {
            val focusManager = LocalFocusManager.current
            val (descTextField, amountTextField) = remember { FocusRequester.createRefs() }

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.verticalScroll(rememberScrollState()),
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
                DatePickerTextField(
                    timestamp = transactionDialogState.date,
                    labelRes = localesR.string.date,
                    icon = CsIcons.Outlined.Calendar,
                    modifier = Modifier.fillMaxWidth(),
                    onDateSelect = { onTransactionEvent(UpdateDate(it)) },
                )
                TimePickerTextField(
                    onTimeSelect = { onTransactionEvent(UpdateDate(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    timestamp = transactionDialogState.date,
                )
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
                    icon = CsIcons.Outlined.Block,
                    titleRes = localesR.string.transaction_ignore,
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
    TrackScreenViewEvent(screenName = "TransactionDialog")
}

@Composable
private fun TransactionTypeChoiceRow(
    onTransactionEvent: (TransactionDialogEvent) -> Unit,
    transactionState: TransactionDialogUiState,
) {
    CsConnectedButtonGroup(
        selectedIndex = transactionState.transactionType.ordinal,
        options = listOf(localesR.string.expense, localesR.string.income_singular),
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
    CsConnectedButtonGroup(
        selectedIndex = if (transactionState.completed) 1 else 0,
        options = listOf(localesR.string.pending, localesR.string.completed),
        checkedIcon = CsIcons.Outlined.Check,
        uncheckedIcons = listOf(CsIcons.Outlined.Pending, CsIcons.Outlined.CheckCircle),
        onClick = { onTransactionEvent(UpdateCompletionStatus(it == 1)) },
        modifier = Modifier.fillMaxWidth(),
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
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
            containerColor = MenuDefaults.groupStandardContainerColor,
        ) {
            categories.forEachIndexed { index, category ->
                DropdownMenuItem(
                    shapes = MenuDefaults.itemShape(index, categories.size),
                    text = {
                        Text(
                            text = category?.title?: stringResource(localesR.string.none),
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
                    checkedLeadingIcon = { Icon(CsIcons.Outlined.Check, contentDescription = null) },
                )
            }
        }
    }
}