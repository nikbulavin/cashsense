package ru.resodostudios.cashsense.feature.wallet.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AppBarRow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarDefaults.ScreenOffset
import androidx.compose.material3.FloatingToolbarDefaults.floatingToolbarVerticalNestedScroll
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import ru.resodostudios.cashsense.core.designsystem.component.button.CsIconButton
import ru.resodostudios.cashsense.core.designsystem.component.button.CsIconToggleButton
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.filled.Star
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Add
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.ArrowBack
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Delete
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Edit
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.MoreVert
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.SendMoney
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Star
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.DateType
import ru.resodostudios.cashsense.core.model.data.FinanceType
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.model.data.TransactionFilter
import ru.resodostudios.cashsense.core.model.data.Wallet
import ru.resodostudios.cashsense.core.ui.TransactionPreviewParameterProvider
import ru.resodostudios.cashsense.core.ui.component.AnimatedAmount
import ru.resodostudios.cashsense.core.ui.component.FinancePanel
import ru.resodostudios.cashsense.core.ui.component.LoadingState
import ru.resodostudios.cashsense.core.ui.groupByDate
import ru.resodostudios.cashsense.core.ui.transactions
import ru.resodostudios.cashsense.core.ui.util.TrackScreenViewEvent
import ru.resodostudios.cashsense.core.ui.util.getCurrentZonedDateTime
import ru.resodostudios.cashsense.core.util.getUsdCurrency
import ru.resodostudios.cashsense.feature.wallet.detail.WalletUiState.Loading
import ru.resodostudios.cashsense.feature.wallet.detail.WalletUiState.Success
import java.math.BigDecimal
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
fun WalletScreen(
    onBackClick: () -> Unit,
    onTransfer: (String) -> Unit,
    onEditWallet: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    showNavigationIcon: Boolean,
    navigateToTransactionDialog: (walletId: String, transactionId: String?, repeated: Boolean) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
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
        onTransactionSelect = viewModel::updateSelectedTransaction,
        onTransactionDelete = viewModel::deleteTransaction,
        onDateTypeUpdate = viewModel::updateDateType,
        onFinanceTypeUpdate = viewModel::updateFinanceType,
        onSelectedDateUpdate = viewModel::updateSelectedDate,
        onCategoryFilterUpdate = viewModel::updateSelectedCategories,
        onShowSnackbar = onShowSnackbar,
        shouldDisplayUndoTransaction = viewModel.shouldDisplayUndoTransaction,
        undoTransactionRemoval = viewModel::undoTransactionRemoval,
        shouldDisplayUndoTransfer = viewModel.shouldDisplayUndoTransfer,
        undoTransferRemoval = viewModel::undoTransferRemoval,
        clearUndoState = viewModel::clearUndoState,
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalHazeMaterialsApi::class,
)
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
    onCategoryFilterUpdate: (Category, Boolean) -> Unit,
    navigateToTransactionDialog: (walletId: String, transactionId: String?, repeated: Boolean) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    onTransactionSelect: (Transaction?) -> Unit = {},
    onTransactionDelete: () -> Unit = {},
    shouldDisplayUndoTransaction: Boolean = false,
    undoTransactionRemoval: () -> Unit = {},
    shouldDisplayUndoTransfer: Boolean = false,
    undoTransferRemoval: () -> Unit = {},
    clearUndoState: () -> Unit = {},
) {
    val transactionDeletedMessage = stringResource(localesR.string.transaction_deleted)
    val transferDeletedMessage = stringResource(localesR.string.transfer_deleted)
    val undoText = stringResource(localesR.string.undo)

    LaunchedEffect(shouldDisplayUndoTransaction) {
        if (shouldDisplayUndoTransaction) {
            val snackBarResult = onShowSnackbar(transactionDeletedMessage, undoText)
            if (snackBarResult) undoTransactionRemoval() else clearUndoState()
        }
    }
    LaunchedEffect(shouldDisplayUndoTransfer) {
        if (shouldDisplayUndoTransfer) {
            val snackBarResult = onShowSnackbar(transferDeletedMessage, undoText)
            if (snackBarResult) undoTransferRemoval() else clearUndoState()
        }
    }
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        clearUndoState()
    }

    when (walletState) {
        Loading -> LoadingState(Modifier.fillMaxSize())
        is Success -> {
            Scaffold(
                topBar = {
                    WalletTopBar(
                        wallet = walletState.wallet,
                        formattedCurrentBalance = walletState.formattedCurrentBalance,
                        isPrimary = walletState.isPrimary,
                        showNavigationIcon = showNavigationIcon,
                        onBackClick = onBackClick,
                        onPrimaryClick = onPrimaryClick,
                    )
                },
            ) { paddingValues ->
                var expanded by rememberSaveable { mutableStateOf(true) }
                val hazeState = rememberHazeState()
                val hazeStyle =
                    HazeMaterials.ultraThin(MaterialTheme.colorScheme.secondaryContainer)

                Box(modifier = Modifier.padding(paddingValues)) {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 96.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .floatingToolbarVerticalNestedScroll(
                                expanded = expanded,
                                onExpand = { expanded = true },
                                onCollapse = { expanded = false },
                            ),
                    ) {
                        item {
                            FinancePanel(
                                availableCategories = walletState.availableCategories,
                                currency = walletState.wallet.currency,
                                formattedExpenses = walletState.formattedExpenses,
                                formattedIncome = walletState.formattedIncome,
                                graphData = walletState.graphData,
                                transactionFilter = walletState.transactionFilter,
                                onDateTypeUpdate = onDateTypeUpdate,
                                onFinanceTypeUpdate = onFinanceTypeUpdate,
                                onSelectedDateUpdate = onSelectedDateUpdate,
                                onCategoryFilterUpdate = onCategoryFilterUpdate,
                                modifier = Modifier.padding(top = 6.dp),
                            )
                        }
                        transactions(
                            groupedTransactions = walletState.groupedTransactions,
                            hazeState = hazeState,
                            hazeStyle = hazeStyle,
                            onClick = onTransactionSelect,
                            selectedTransaction = walletState.selectedTransaction,
                            onRepeatClick = { transactionId ->
                                navigateToTransactionDialog(
                                    walletState.wallet.id,
                                    transactionId,
                                    true,
                                )
                            },
                            onEditClick = { transactionId ->
                                navigateToTransactionDialog(
                                    walletState.wallet.id,
                                    transactionId,
                                    false,
                                )
                            },
                            onDeleteClick = onTransactionDelete,
                        )
                    }
                    WalletToolbar(
                        expanded = expanded,
                        onTransfer = onTransfer,
                        walletId = walletState.wallet.id,
                        onEditWallet = onEditWallet,
                        onDeleteWallet = onDeleteWallet,
                        navigateToTransactionDialog = navigateToTransactionDialog,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = -ScreenOffset),
                    )
                }
            }
        }
    }
    TrackScreenViewEvent(screenName = "Wallet")
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun WalletToolbar(
    expanded: Boolean,
    onTransfer: (String) -> Unit,
    walletId: String,
    onEditWallet: (String) -> Unit,
    onDeleteWallet: (String) -> Unit,
    navigateToTransactionDialog: (String, String?, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    HorizontalFloatingToolbar(
        colors = FloatingToolbarDefaults.vibrantFloatingToolbarColors(),
        modifier = modifier,
        expanded = expanded,
        leadingContent = {
            IconButton(
                onClick = { onTransfer(walletId) },
            ) {
                Icon(
                    imageVector = CsIcons.Outlined.SendMoney,
                    contentDescription = stringResource(localesR.string.transfer),
                )
            }
        },
        trailingContent = {
            val editButtonLabel = stringResource(localesR.string.edit)
            val deleteButtonLabel = stringResource(localesR.string.delete)
            AppBarRow(
                maxItemCount = 1,
                overflowIndicator = { menuState ->
                    IconButton(
                        onClick = {
                            if (menuState.isShowing) menuState.dismiss() else menuState.show()
                        },
                    ) {
                        Icon(
                            imageVector = CsIcons.Outlined.MoreVert,
                            contentDescription = stringResource(localesR.string.wallet_menu_icon_description),
                        )
                    }
                },
            ) {
                clickableItem(
                    onClick = { onEditWallet(walletId) },
                    icon = {
                        Icon(
                            imageVector = CsIcons.Outlined.Edit,
                            contentDescription = stringResource(localesR.string.edit),
                        )
                    },
                    label = editButtonLabel,
                )
                clickableItem(
                    onClick = { onDeleteWallet(walletId) },
                    icon = {
                        Icon(
                            imageVector = CsIcons.Outlined.Delete,
                            contentDescription = deleteButtonLabel,
                        )
                    },
                    label = deleteButtonLabel,
                )
            }
        },
        content = {
            FilledIconButton(
                modifier = Modifier.width(64.dp),
                onClick = { navigateToTransactionDialog(walletId, null, false) },
            ) {
                Icon(
                    imageVector = CsIcons.Outlined.Add,
                    contentDescription = stringResource(localesR.string.add_transaction),
                )
            }
        }
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
private fun WalletTopBar(
    wallet: Wallet,
    formattedCurrentBalance: String,
    isPrimary: Boolean,
    showNavigationIcon: Boolean,
    onBackClick: () -> Unit,
    onPrimaryClick: (walletId: String, isPrimary: Boolean) -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = wallet.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        subtitle = {
            AnimatedAmount(
                formattedAmount = formattedCurrentBalance,
                label = "WalletBalance",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        navigationIcon = {
            if (showNavigationIcon) {
                CsIconButton(
                    onClick = onBackClick,
                    icon = CsIcons.Outlined.ArrowBack,
                    contentDescription = stringResource(localesR.string.navigation_back_icon_description),
                )
            }
        },
        actions = {
            PrimaryToggleButton(
                isPrimary = isPrimary,
                onPrimaryClick = { onPrimaryClick(wallet.id, !isPrimary) },
            )
        },
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun PrimaryToggleButton(
    isPrimary: Boolean,
    onPrimaryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (icon, contentDescription) = if (isPrimary) {
        CsIcons.Filled.Star to stringResource(localesR.string.non_primary_icon_description)
    } else {
        CsIcons.Outlined.Star to stringResource(localesR.string.primary_icon_description)
    }
    CsIconToggleButton(
        checked = isPrimary,
        onCheckedChange = { onPrimaryClick() },
        icon = icon,
        contentDescription = contentDescription,
        modifier = modifier,
        tooltipPosition = TooltipAnchorPosition.Left,
    )
}

@PreviewLightDark
@Composable
private fun WalletScreenPopulatedPreview(
    @PreviewParameter(TransactionPreviewParameterProvider::class)
    transactions: List<Transaction>,
) {
    CsTheme {
        WalletScreen(
            walletState = Success(
                transactionFilter = TransactionFilter(
                    selectedCategories = emptySet(),
                    financeType = FinanceType.NOT_SET,
                    dateType = DateType.ALL,
                    selectedDate = getCurrentZonedDateTime().date,
                ),
                wallet = Wallet(
                    id = "1",
                    title = "Credit",
                    initialBalance = BigDecimal(1000),
                    currency = getUsdCurrency(),
                ),
                selectedTransaction = null,
                groupedTransactions = transactions.groupByDate(),
                availableCategories = emptyList(),
                formattedExpenses = "$495.9",
                formattedIncome = "1,000",
                graphData = emptyMap(),
                isPrimary = true,
                formattedCurrentBalance = "$57,500",
            ),
            showNavigationIcon = true,
            onPrimaryClick = { _, _ -> },
            onTransfer = {},
            onEditWallet = {},
            onDeleteWallet = {},
            onBackClick = {},
            onDateTypeUpdate = {},
            onFinanceTypeUpdate = {},
            onSelectedDateUpdate = {},
            onCategoryFilterUpdate = { _, _ -> },
            navigateToTransactionDialog = { _, _, _ -> },
            onShowSnackbar = { _, _ -> false },
        )
    }
}

@PreviewLightDark
@Composable
private fun WalletScreenEmptyPreview() {
    CsTheme {
        WalletScreen(
            walletState = Success(
                transactionFilter = TransactionFilter(
                    selectedCategories = emptySet(),
                    financeType = FinanceType.NOT_SET,
                    dateType = DateType.ALL,
                    selectedDate = getCurrentZonedDateTime().date,
                ),
                wallet = Wallet(
                    id = "1",
                    title = "Credit",
                    initialBalance = BigDecimal(1000),
                    currency = getUsdCurrency(),
                ),
                selectedTransaction = null,
                groupedTransactions = emptyMap(),
                availableCategories = emptyList(),
                formattedExpenses = "$495.9",
                formattedIncome = "1,000",
                graphData = emptyMap(),
                isPrimary = true,
                formattedCurrentBalance = "$57,500",
            ),
            showNavigationIcon = true,
            onPrimaryClick = { _, _ -> },
            onTransfer = {},
            onEditWallet = {},
            onDeleteWallet = {},
            onBackClick = {},
            onDateTypeUpdate = {},
            onFinanceTypeUpdate = {},
            onSelectedDateUpdate = {},
            onCategoryFilterUpdate = { _, _ -> },
            navigateToTransactionDialog = { _, _, _ -> },
            onShowSnackbar = { _, _ -> false },
        )
    }
}