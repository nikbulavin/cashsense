package ru.resodostudios.cashsense.feature.transfer.impl

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
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
import ru.resodostudios.cashsense.core.common.getUsdCurrency
import ru.resodostudios.cashsense.core.designsystem.component.CsOutlinedTextField
import ru.resodostudios.cashsense.core.designsystem.component.button.CsButton
import ru.resodostudios.cashsense.core.designsystem.component.button.CsIconButton
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.ArrowBack
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Calendar
import ru.resodostudios.cashsense.core.model.MenuWallet
import ru.resodostudios.cashsense.core.ui.component.DatePickerTextField
import ru.resodostudios.cashsense.core.ui.component.LoadingState
import ru.resodostudios.cashsense.core.ui.component.OutlinedAmountField
import ru.resodostudios.cashsense.core.ui.component.TimePickerTextField
import ru.resodostudios.cashsense.core.ui.component.WalletDropdownMenu
import ru.resodostudios.cashsense.core.ui.util.TrackScreenViewEvent
import ru.resodostudios.cashsense.core.ui.util.cleanAmount
import ru.resodostudios.cashsense.core.ui.util.isAmountValid
import ru.resodostudios.cashsense.core.ui.util.logNewItemAdded
import kotlin.time.Instant
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
internal fun TransferEditorScreen(
    onBackClick: () -> Unit,
    viewModel: TransferEditorViewModel = hiltViewModel(),
) {
    val transferState by viewModel.transferEditorState.collectAsStateWithLifecycle()

    TransferEditorScreen(
        transferState = transferState,
        onBackClick = onBackClick,
        onSendingWalletUpdate = viewModel::updateSendingWallet,
        onReceivingWalletUpdate = viewModel::updateReceivingWallet,
        onAmountUpdate = viewModel::updateAmount,
        onExchangingRateUpdate = viewModel::updateExchangingRate,
        onConvertedAmountUpdate = viewModel::updateConvertedAmount,
        onTransferSave = viewModel::saveTransfer,
        onDateUpdate = viewModel::updateDate,
    )
}

@Composable
private fun TransferEditorScreen(
    transferState: TransferEditorState,
    onBackClick: () -> Unit,
    onSendingWalletUpdate: (MenuWallet) -> Unit,
    onReceivingWalletUpdate: (MenuWallet) -> Unit,
    onAmountUpdate: (String) -> Unit,
    onExchangingRateUpdate: (String) -> Unit,
    onConvertedAmountUpdate: (String) -> Unit,
    onTransferSave: () -> Unit,
    onDateUpdate: (Instant) -> Unit,
) {
    TrackScreenViewEvent(screenName = "TransferEditor")

    if (transferState.isLoading) {
        LoadingState(Modifier.fillMaxSize())
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(localesR.string.new_transfer),
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

                        CsButton(
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                                onTransferSave()
                                analyticsHelper.logNewItemAdded(
                                    itemType = AnalyticsEvent.ItemTypes.TRANSFER,
                                )
                                onBackClick()
                            },
                            enabled = transferState.amount.isAmountValid() &&
                                    transferState.sendingWallet.id.isNotBlank() &&
                                    transferState.receivingWallet.id.isNotBlank() &&
                                    transferState.exchangeRate.isAmountValid() &&
                                    transferState.convertedAmount.isAmountValid() &&
                                    transferState.sendingWallet != transferState.receivingWallet,
                            title = stringResource(localesR.string.transfer),
                        )
                    },
                )
            },
        ) { innerPadding ->
            val focusManager = LocalFocusManager.current
            val exchangeRateEnabled =
                transferState.sendingWallet.currency != transferState.receivingWallet.currency

            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                WalletDropdownMenu(
                    title = localesR.string.from_wallet,
                    onWalletSelect = onSendingWalletUpdate,
                    selectedWallet = transferState.sendingWallet,
                    availableWallets = transferState.availableWallets,
                    modifier = Modifier.fillMaxWidth(),
                )
                WalletDropdownMenu(
                    title = localesR.string.to_wallet,
                    onWalletSelect = onReceivingWalletUpdate,
                    selectedWallet = transferState.receivingWallet,
                    availableWallets = transferState.availableWallets,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedAmountField(
                    value = transferState.amount,
                    onValueChange = onAmountUpdate,
                    labelRes = localesR.string.amount,
                    currency = transferState.sendingWallet.currency ?: getUsdCurrency(),
                    imeAction = if (exchangeRateEnabled) ImeAction.Next else ImeAction.Done,
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) },
                        onDone = { focusManager.clearFocus() },
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
                CsOutlinedTextField(
                    value = transferState.exchangeRate,
                    onValueChange = { onExchangingRateUpdate(it.cleanAmount()) },
                    labelText = stringResource(localesR.string.exchange_rate),
                    placeholderText = "0.01",
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done,
                    ),
                    enabled = exchangeRateEnabled,
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() },
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
                AnimatedVisibility(transferState.receivingWallet.currency != null) {
                    OutlinedAmountField(
                        value = transferState.convertedAmount,
                        onValueChange = onConvertedAmountUpdate,
                        labelRes = localesR.string.converted_amount,
                        currency = transferState.receivingWallet.currency ?: getUsdCurrency(),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                DatePickerTextField(
                    timestamp = transferState.date,
                    labelRes = localesR.string.date,
                    icon = CsIcons.Outlined.Calendar,
                    modifier = Modifier.fillMaxWidth(),
                    onDateSelect = onDateUpdate,
                )
                TimePickerTextField(
                    timestamp = transferState.date,
                    modifier = Modifier.fillMaxWidth(),
                    onTimeSelect = onDateUpdate,
                )
            }
        }
    }
}
