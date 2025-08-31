package ru.resodostudios.cashsense.feature.wallet.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import ru.resodostudios.cashsense.core.designsystem.component.CsTonalToggleButton
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.filled.Star
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Star
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Wallet
import ru.resodostudios.cashsense.core.ui.component.CurrencyDropdownMenu
import ru.resodostudios.cashsense.core.ui.component.LoadingState
import ru.resodostudios.cashsense.core.ui.util.TrackScreenViewEvent
import ru.resodostudios.cashsense.core.ui.util.cleanAmount
import ru.resodostudios.cashsense.core.ui.util.logNewItemAdded
import java.util.Currency
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
internal fun WalletDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WalletDialogViewModel = hiltViewModel(),
) {
    val walletDialogState by viewModel.walletDialogState.collectAsStateWithLifecycle()

    WalletDialog(
        walletDialogState = walletDialogState,
        onDismiss = onDismiss,
        onWalletSave = viewModel::saveWallet,
        onTitleUpdate = viewModel::updateTitle,
        onInitialBalanceUpdate = viewModel::updateInitialBalance,
        onCurrencyUpdate = viewModel::updateCurrency,
        onPrimaryUpdate = viewModel::updatePrimary,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun WalletDialog(
    walletDialogState: WalletDialogUiState,
    onDismiss: () -> Unit,
    onWalletSave: (WalletDialogUiState) -> Unit,
    onTitleUpdate: (String) -> Unit,
    onInitialBalanceUpdate: (String) -> Unit,
    onCurrencyUpdate: (Currency) -> Unit,
    onPrimaryUpdate: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val (titleRes, confirmButtonTextRes) = if (walletDialogState.id.isNotBlank()) {
        localesR.string.edit_wallet to localesR.string.save
    } else {
        localesR.string.new_wallet to localesR.string.add
    }
    val analyticsHelper = LocalAnalyticsHelper.current

    CsAlertDialog(
        titleRes = titleRes,
        confirmButtonTextRes = confirmButtonTextRes,
        dismissButtonTextRes = localesR.string.cancel,
        icon = CsIcons.Outlined.Wallet,
        onConfirm = {
            onWalletSave(walletDialogState)
            if (walletDialogState.id.isBlank()) {
                analyticsHelper.logNewItemAdded(
                    itemType = AnalyticsEvent.ItemTypes.WALLET,
                )
            }
            onDismiss()
        },
        isConfirmEnabled = walletDialogState.title.isNotBlank(),
        onDismiss = onDismiss,
        modifier = modifier,
    ) {
        if (walletDialogState.isLoading) {
            LoadingState(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp),
            )
        } else {
            val focusManager = LocalFocusManager.current
            val focusRequester = remember { FocusRequester() }

            Column(Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = walletDialogState.title,
                    onValueChange = onTitleUpdate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .focusRequester(focusRequester),
                    label = { Text(stringResource(localesR.string.title)) },
                    placeholder = { Text(stringResource(localesR.string.title) + "*") },
                    supportingText = { Text(stringResource(localesR.string.required)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) },
                    ),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = walletDialogState.initialBalance,
                    onValueChange = { onInitialBalanceUpdate(it.cleanAmount()) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    label = { Text(stringResource(localesR.string.initial_balance)) },
                    placeholder = { Text("0") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() },
                    ),
                    singleLine = true,
                )
                CurrencyDropdownMenu(
                    currency = walletDialogState.currency,
                    onCurrencyClick = onCurrencyUpdate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    enabled = walletDialogState.isCurrencyEditable,
                )
                CsTonalToggleButton(
                    checked = walletDialogState.isPrimary,
                    icon = if (walletDialogState.isPrimary) CsIcons.Filled.Star else CsIcons.Outlined.Star,
                    titleRes = localesR.string.primary,
                    onCheckedChange = onPrimaryUpdate,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            LaunchedEffect(walletDialogState.id) {
                if (walletDialogState.id.isBlank()) {
                    focusRequester.requestFocus()
                }
            }
        }
    }
    TrackScreenViewEvent(screenName = "WalletDialog")
}