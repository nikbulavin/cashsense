package ru.resodostudios.cashsense.feature.subscription.dialog.impl

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.analytics.AnalyticsEvent
import ru.resodostudios.cashsense.core.analytics.LocalAnalyticsHelper
import ru.resodostudios.cashsense.core.designsystem.component.CsAlertDialog
import ru.resodostudios.cashsense.core.designsystem.component.button.CsTonalToggleButton
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Autorenew
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Calendar
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Check
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Notifications
import ru.resodostudios.cashsense.core.model.data.RepeatingIntervalType
import ru.resodostudios.cashsense.core.ui.component.CurrencyDropdownMenu
import ru.resodostudios.cashsense.core.ui.component.DatePickerTextField
import ru.resodostudios.cashsense.core.ui.permission.NotificationPermissionEffect
import ru.resodostudios.cashsense.core.ui.util.TrackScreenViewEvent
import ru.resodostudios.cashsense.core.ui.util.isAmountValid
import ru.resodostudios.cashsense.core.ui.util.logNewItemAdded
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
internal fun SubscriptionDialog(
    onDismiss: () -> Unit,
    viewModel: SubscriptionDialogViewModel = hiltViewModel(),
) {
    val subscriptionDialogState by viewModel.subscriptionDialogUiState.collectAsStateWithLifecycle()

    SubscriptionDialog(
        subscriptionDialogState = subscriptionDialogState,
        onSubscriptionEvent = viewModel::onSubscriptionEvent,
        onDismiss = onDismiss,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SubscriptionDialog(
    subscriptionDialogState: SubscriptionDialogUiState,
    onSubscriptionEvent: (SubscriptionDialogEvent) -> Unit,
    onDismiss: () -> Unit,
) {
    val (dialogTitle, dialogConfirmText) = if (subscriptionDialogState.id.isNotEmpty()) {
        localesR.string.edit_subscription to localesR.string.save
    } else {
        localesR.string.new_subscription to localesR.string.add
    }
    val analyticsHelper = LocalAnalyticsHelper.current

    CsAlertDialog(
        titleRes = dialogTitle,
        confirmButtonTextRes = dialogConfirmText,
        dismissButtonTextRes = localesR.string.cancel,
        icon = CsIcons.Outlined.Autorenew,
        onConfirm = {
            onSubscriptionEvent(SubscriptionDialogEvent.Save(subscriptionDialogState.asSubscription()))
            if (subscriptionDialogState.id.isBlank()) {
                analyticsHelper.logNewItemAdded(
                    itemType = AnalyticsEvent.ItemTypes.SUBSCRIPTION,
                )
            }
            onDismiss()
        },
        isConfirmEnabled = subscriptionDialogState.title.isNotBlank() &&
                subscriptionDialogState.amount.isAmountValid(),
        onDismiss = onDismiss,
    ) {
        val focusManager = LocalFocusManager.current
        val focusRequester = remember { FocusRequester() }

        Column(Modifier.verticalScroll(rememberScrollState())) {
            OutlinedTextField(
                value = subscriptionDialogState.title,
                onValueChange = { onSubscriptionEvent(SubscriptionDialogEvent.UpdateTitle(it)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) },
                ),
                singleLine = true,
                label = { Text(stringResource(localesR.string.title)) },
                placeholder = { Text(stringResource(localesR.string.title) + "*") },
                supportingText = { Text(stringResource(localesR.string.required)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .focusRequester(focusRequester),
            )
            OutlinedTextField(
                value = subscriptionDialogState.amount,
                onValueChange = { onSubscriptionEvent(SubscriptionDialogEvent.UpdateAmount(it)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() },
                ),
                singleLine = true,
                label = { Text(stringResource(localesR.string.amount)) },
                placeholder = { Text(stringResource(localesR.string.amount) + "*") },
                supportingText = { Text(stringResource(localesR.string.required)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
            )
            CurrencyDropdownMenu(
                currency = subscriptionDialogState.currency,
                onCurrencyClick = { onSubscriptionEvent(SubscriptionDialogEvent.UpdateCurrency(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
            )
            DatePickerTextField(
                timestamp = subscriptionDialogState.paymentDate,
                labelRes = localesR.string.payment_date,
                icon = CsIcons.Outlined.Calendar,
                onDateSelect = { onSubscriptionEvent(SubscriptionDialogEvent.UpdatePaymentDate(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                onlyFutureDates = true,
            )
            CsTonalToggleButton(
                checked = subscriptionDialogState.isReminderEnabled,
                icon = if (subscriptionDialogState.isReminderEnabled) CsIcons.Outlined.Check else CsIcons.Outlined.Notifications,
                titleRes = localesR.string.reminder,
                onCheckedChange = { onSubscriptionEvent(SubscriptionDialogEvent.UpdateReminderSwitch(it)) },
                modifier = Modifier.fillMaxWidth(),
            )
            AnimatedVisibility(subscriptionDialogState.isReminderEnabled) {
                RepeatingIntervalDropdownMenu(
                    interval = subscriptionDialogState.repeatingInterval,
                    onIntervalChange = {
                        onSubscriptionEvent(SubscriptionDialogEvent.UpdateRepeatingInterval(it))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                )
            }
        }
        LaunchedEffect(subscriptionDialogState.id) {
            if (subscriptionDialogState.id.isEmpty()) focusRequester.requestFocus()
        }
        NotificationPermissionEffect(subscriptionDialogState.isReminderEnabled)
    }
    TrackScreenViewEvent(screenName = "SubscriptionDialog")
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun RepeatingIntervalDropdownMenu(
    interval: RepeatingIntervalType,
    onIntervalChange: (RepeatingIntervalType) -> Unit,
    modifier: Modifier = Modifier,
) {
    val intervalNames = listOf(
        stringResource(localesR.string.repeat_none),
        stringResource(localesR.string.repeat_daily),
        stringResource(localesR.string.repeat_weekly),
        stringResource(localesR.string.repeat_monthly),
        stringResource(localesR.string.repeat_yearly),
    )
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = intervalNames[interval.ordinal],
            onValueChange = {},
            modifier = modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            readOnly = true,
            singleLine = true,
            label = { Text(stringResource(localesR.string.repeating_interval)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = MenuDefaults.standaloneGroupShape,
            containerColor = MenuDefaults.groupStandardContainerColor,
        ) {
            intervalNames.forEachIndexed { index, label ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        onIntervalChange(RepeatingIntervalType.entries[index])
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    shapes = MenuDefaults.itemShape(index, intervalNames.size),
                    selected = index == interval.ordinal,
                    checkedLeadingIcon = { Icon(CsIcons.Outlined.Check, contentDescription = null) },
                )
            }
        }
    }
}