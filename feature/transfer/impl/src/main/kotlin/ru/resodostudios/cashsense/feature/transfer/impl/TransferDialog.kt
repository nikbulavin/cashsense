package ru.resodostudios.cashsense.feature.transfer.impl

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
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
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Calendar
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Check
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.SendMoney
import ru.resodostudios.cashsense.core.ui.component.DatePickerTextField
import ru.resodostudios.cashsense.core.ui.component.LoadingState
import ru.resodostudios.cashsense.core.ui.component.OutlinedAmountField
import ru.resodostudios.cashsense.core.ui.component.TimePickerTextField
import ru.resodostudios.cashsense.core.ui.util.TrackScreenViewEvent
import ru.resodostudios.cashsense.core.ui.util.cleanAmount
import ru.resodostudios.cashsense.core.ui.util.formatAmount
import ru.resodostudios.cashsense.core.ui.util.isAmountValid
import ru.resodostudios.cashsense.core.ui.util.logNewItemAdded
import ru.resodostudios.cashsense.core.util.getUsdCurrency
import kotlin.time.Instant
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
internal fun TransferDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TransferDialogViewModel = hiltViewModel(),
) {
    val transferState by viewModel.transferDialogState.collectAsStateWithLifecycle()

    TransferDialog(
        transferState = transferState,
        onDismiss = onDismiss,
        onSendingWalletUpdate = viewModel::updateSendingWallet,
        onReceivingWalletUpdate = viewModel::updateReceivingWallet,
        onAmountUpdate = viewModel::updateAmount,
        onExchangingRateUpdate = viewModel::updateExchangingRate,
        onConvertedAmountUpdate = viewModel::updateConvertedAmount,
        onTransferSave = viewModel::saveTransfer,
        onDateUpdate = viewModel::updateDate,
        modifier = modifier,
    )
}

@Composable
private fun TransferDialog(
    transferState: TransferDialogUiState,
    onDismiss: () -> Unit,
    onSendingWalletUpdate: (TransferWallet) -> Unit,
    onReceivingWalletUpdate: (TransferWallet) -> Unit,
    onAmountUpdate: (String) -> Unit,
    onExchangingRateUpdate: (String) -> Unit,
    onConvertedAmountUpdate: (String) -> Unit,
    onTransferSave: (TransferDialogUiState) -> Unit,
    onDateUpdate: (Instant) -> Unit,
    modifier: Modifier = Modifier,
) {
    val analyticsHelper = LocalAnalyticsHelper.current
    CsAlertDialog(
        titleRes = localesR.string.new_transfer,
        confirmButtonTextRes = localesR.string.transfer,
        dismissButtonTextRes = localesR.string.cancel,
        icon = CsIcons.Outlined.SendMoney,
        onConfirm = {
            onTransferSave(transferState)
            analyticsHelper.logNewItemAdded(
                itemType = AnalyticsEvent.ItemTypes.TRANSFER,
            )
            onDismiss()
        },
        isConfirmEnabled = transferState.amount.isAmountValid() &&
                transferState.sendingWallet.id.isNotBlank() &&
                transferState.receivingWallet.id.isNotBlank() &&
                transferState.exchangeRate.isAmountValid() &&
                transferState.convertedAmount.isAmountValid() &&
                transferState.sendingWallet != transferState.receivingWallet,
        onDismiss = onDismiss,
        modifier = modifier,
    ) {
        if (transferState.isLoading) {
            LoadingState(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp),
            )
        } else {
            val focusManager = LocalFocusManager.current
            val exchangeRateEnabled =
                transferState.sendingWallet.currency != transferState.receivingWallet.currency

            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                WalletDropdownMenu(
                    title = localesR.string.from_wallet,
                    onWalletSelect = onSendingWalletUpdate,
                    selectedWallet = transferState.sendingWallet,
                    availableWallets = transferState.transferWallets,
                    modifier = Modifier.fillMaxWidth(),
                )
                WalletDropdownMenu(
                    title = localesR.string.to_wallet,
                    onWalletSelect = onReceivingWalletUpdate,
                    selectedWallet = transferState.receivingWallet,
                    availableWallets = transferState.transferWallets,
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
                OutlinedTextField(
                    value = transferState.exchangeRate,
                    onValueChange = { onExchangingRateUpdate(it.cleanAmount()) },
                    label = { Text(stringResource(localesR.string.exchange_rate)) },
                    placeholder = { Text("0.01") },
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
    TrackScreenViewEvent(screenName = "TransferDialog")
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun WalletDropdownMenu(
    @StringRes title: Int,
    onWalletSelect: (TransferWallet) -> Unit,
    selectedWallet: TransferWallet,
    availableWallets: List<TransferWallet>,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = selectedWallet.title,
            onValueChange = {},
            modifier = modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            readOnly = true,
            singleLine = true,
            label = { Text(stringResource(title)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            placeholder = { Text(stringResource(localesR.string.choose_wallet)) },
            supportingText = if (selectedWallet.currency != null) {
                {
                    Text(selectedWallet.currentBalance.formatAmount(selectedWallet.currency))
                }
            } else {
                null
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = MenuDefaults.standaloneGroupShape,
            containerColor = MenuDefaults.groupStandardContainerColor,
        ) {
            availableWallets.forEachIndexed { index, wallet ->
                val currentBalance = wallet.currency?.let { wallet.currentBalance.formatAmount(it) }
                val menuText = "${wallet.title} – $currentBalance"
                DropdownMenuItem(
                    shapes = MenuDefaults.itemShape(index, availableWallets.size),
                    selected = selectedWallet == wallet,
                    checkedLeadingIcon = { Icon(CsIcons.Outlined.Check, contentDescription = null) },
                    text = {
                        Text(
                            text = menuText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    onClick = {
                        onWalletSelect(wallet)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}