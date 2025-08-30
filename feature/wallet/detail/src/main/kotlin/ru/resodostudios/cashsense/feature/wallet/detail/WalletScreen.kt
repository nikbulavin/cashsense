package ru.resodostudios.cashsense.feature.wallet.detail

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.designsystem.component.CsAlertDialog
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.filled.Star
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Add
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.ArrowBack
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Delete
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Star
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.DateType
import ru.resodostudios.cashsense.core.model.data.FinanceType
import ru.resodostudios.cashsense.core.model.data.UserWallet
import ru.resodostudios.cashsense.core.ui.component.AnimatedAmount
import ru.resodostudios.cashsense.core.ui.component.FinancePanel
import ru.resodostudios.cashsense.core.ui.component.LoadingState
import ru.resodostudios.cashsense.core.ui.component.TransactionBottomSheet
import ru.resodostudios.cashsense.core.ui.component.WalletDropdownMenu
import ru.resodostudios.cashsense.core.ui.transactions
import ru.resodostudios.cashsense.core.ui.util.TrackScreenViewEvent
import ru.resodostudios.cashsense.core.ui.util.formatAmount
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
fun WalletScreen(
    onBackClick: () -> Unit,
    onTransfer: (String) -> Unit,
    onEditWallet: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    showNavigationIcon: Boolean,
    navigateToTransactionDialog: (walletId: String, transactionId: String?, repeated: Boolean) -> Unit,
    viewModel: WalletViewModel = hiltViewModel(),
) {
    val walletState by viewModel.walletUiState.collectAsStateWithLifecycle()

    WalletScreen(
        walletState = walletState,
        showNavigationIcon = showNavigationIcon,
        onPrimaryClick = viewModel::setPrimaryWalletId,
        onTransfer = onTransfer,
        onEditWallet = onEditWallet,
        onDeleteWallet = onDeleteClick,
        onBackClick = onBackClick,
        navigateToTransactionDialog = navigateToTransactionDialog,
        updateTransactionId = viewModel::updateTransactionId,
        onUpdateTransactionIgnoring = viewModel::updateTransactionIgnoring,
        onDeleteTransaction = viewModel::deleteTransaction,
        onDateTypeUpdate = viewModel::updateDateType,
        onFinanceTypeUpdate = viewModel::updateFinanceType,
        onSelectedDateUpdate = viewModel::updateSelectedDate,
        onCategorySelect = viewModel::addToSelectedCategories,
        onCategoryDeselect = viewModel::removeFromSelectedCategories
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WalletScreen(
    walletState: WalletUiState,
    showNavigationIcon: Boolean,
    onPrimaryClick: (walletId: String, isPrimary: Boolean) -> Unit,
    onTransfer: (String) -> Unit,
    onEditWallet: (String) -> Unit,
    onDeleteWallet: (String) -> Unit,
    onBackClick: () -> Unit,
    onDateTypeUpdate: (DateType) -> Unit,
    onFinanceTypeUpdate: (FinanceType) -> Unit,
    onSelectedDateUpdate: (Int) -> Unit,
    onCategorySelect: (Category) -> Unit,
    onCategoryDeselect: (Category) -> Unit,
    navigateToTransactionDialog: (walletId: String, transactionId: String?, repeated: Boolean) -> Unit,
    updateTransactionId: (String) -> Unit = {},
    onUpdateTransactionIgnoring: (Boolean) -> Unit = {},
    onDeleteTransaction: () -> Unit = {},
) {
    when (walletState) {
        WalletUiState.Loading -> LoadingState(Modifier.fillMaxSize())
        is WalletUiState.Success -> {
            var showTransactionBottomSheet by rememberSaveable { mutableStateOf(false) }
            var showTransactionDeletionDialog by rememberSaveable { mutableStateOf(false) }

            if (showTransactionBottomSheet && walletState.selectedTransactionCategory != null) {
                TransactionBottomSheet(
                    transactionCategory = walletState.selectedTransactionCategory,
                    currency = walletState.userWallet.currency,
                    onDismiss = { showTransactionBottomSheet = false },
                    onIgnoreClick = onUpdateTransactionIgnoring,
                    onRepeatClick = { transactionId ->
                        navigateToTransactionDialog(walletState.userWallet.id, transactionId, true)
                    },
                    onEdit = { transactionId ->
                        navigateToTransactionDialog(walletState.userWallet.id, transactionId, false)
                    },
                    onDelete = { showTransactionDeletionDialog = true },
                )
            }
            if (showTransactionDeletionDialog) {
                CsAlertDialog(
                    titleRes = localesR.string.permanently_delete,
                    confirmButtonTextRes = localesR.string.delete,
                    dismissButtonTextRes = localesR.string.cancel,
                    icon = CsIcons.Outlined.Delete,
                    onConfirm = {
                        onDeleteTransaction()
                        showTransactionDeletionDialog = false
                    },
                    onDismiss = { showTransactionDeletionDialog = false },
                )
            }

            Scaffold(
                topBar = {
                    WalletTopBar(
                        userWallet = walletState.userWallet,
                        showNavigationIcon = showNavigationIcon,
                        onBackClick = onBackClick,
                        onNewTransactionClick = {
                            navigateToTransactionDialog(walletState.userWallet.id, null, false)
                        },
                        onPrimaryClick = onPrimaryClick,
                        onTransferClick = onTransfer,
                        onEditClick = onEditWallet,
                        onDeleteClick = onDeleteWallet,
                    )
                },
            ) { innerPadding ->
                LazyColumn(
                    contentPadding = PaddingValues(
                        bottom = 110.dp,
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                ) {
                    item {
                        FinancePanel(
                            availableCategories = walletState.availableCategories,
                            currency = walletState.userWallet.currency,
                            expenses = walletState.expenses,
                            income = walletState.income,
                            graphData = walletState.graphData,
                            transactionFilter = walletState.transactionFilter,
                            onDateTypeUpdate = onDateTypeUpdate,
                            onFinanceTypeUpdate = onFinanceTypeUpdate,
                            onSelectedDateUpdate = onSelectedDateUpdate,
                            onCategorySelect = onCategorySelect,
                            onCategoryDeselect = onCategoryDeselect,
                        )
                    }
                    transactions(
                        transactionsCategories = walletState.transactionsCategories,
                        onTransactionClick = {
                            updateTransactionId(it)
                            showTransactionBottomSheet = true
                        },
                    )
                }
            }
        }
    }
    TrackScreenViewEvent(screenName = "Wallet")
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
private fun WalletTopBar(
    userWallet: UserWallet,
    showNavigationIcon: Boolean,
    onBackClick: () -> Unit,
    onNewTransactionClick: () -> Unit,
    onPrimaryClick: (walletId: String, isPrimary: Boolean) -> Unit,
    onTransferClick: (String) -> Unit,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = userWallet.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        subtitle = {
            AnimatedAmount(
                targetState = userWallet.currentBalance,
                label = "WalletBalance",
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = it.formatAmount(userWallet.currency),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        navigationIcon = {
            if (showNavigationIcon) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = CsIcons.Outlined.ArrowBack,
                        contentDescription = null,
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = onNewTransactionClick) {
                Icon(
                    imageVector = CsIcons.Outlined.Add,
                    contentDescription = stringResource(localesR.string.add_transaction),
                )
            }
            PrimaryToggleButton(userWallet, onPrimaryClick)
            WalletDropdownMenu(
                onTransferClick = { onTransferClick(userWallet.id) },
                onEditClick = { onEditClick(userWallet.id) },
                onDeleteClick = { onDeleteClick(userWallet.id) },
            )
        },
    )
}

@Composable
private fun PrimaryToggleButton(
    userWallet: UserWallet,
    onPrimaryClick: (walletId: String, isPrimary: Boolean) -> Unit,
) {
    val (primaryIcon, @StringRes primaryIconContentDescriptionRes) = if (userWallet.isPrimary) {
        CsIcons.Filled.Star to localesR.string.primary_icon_description
    } else {
        CsIcons.Outlined.Star to localesR.string.non_primary_icon_description
    }
    val hapticFeedback = LocalHapticFeedback.current
    IconToggleButton(
        checked = userWallet.isPrimary,
        onCheckedChange = { isChecked ->
            hapticFeedback.performHapticFeedback(
                if (isChecked) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff
            )
            onPrimaryClick(userWallet.id, isChecked)
        },
    ) {
        Icon(
            imageVector = primaryIcon,
            contentDescription = stringResource(primaryIconContentDescriptionRes),
        )
    }
}