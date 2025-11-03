package ru.resodostudios.cashsense.feature.transaction.overview

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import ru.resodostudios.cashsense.core.designsystem.component.button.CsIconButton
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.ArrowBack
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.DateType
import ru.resodostudios.cashsense.core.model.data.FinanceType
import ru.resodostudios.cashsense.core.model.data.TransactionWithCategory
import ru.resodostudios.cashsense.core.ui.component.AnimatedAmount
import ru.resodostudios.cashsense.core.ui.component.FinancePanel
import ru.resodostudios.cashsense.core.ui.component.LoadingState
import ru.resodostudios.cashsense.core.ui.transactions
import ru.resodostudios.cashsense.core.ui.util.TrackScreenViewEvent
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
fun TransactionOverviewScreen(
    shouldShowNavigationIcon: Boolean,
    onBackClick: () -> Unit,
    navigateToTransactionDialog: (walletId: String, transactionId: String?, repeated: Boolean) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    viewModel: TransactionOverviewViewModel = hiltViewModel(),
) {
    val financePanelUiState by viewModel.financePanelUiState.collectAsStateWithLifecycle()
    val transactionOverviewState by viewModel.transactionOverviewUiState.collectAsStateWithLifecycle()

    TransactionOverviewScreen(
        shouldShowNavigationIcon = shouldShowNavigationIcon,
        onBackClick = onBackClick,
        financePanelUiState = financePanelUiState,
        onDateTypeUpdate = viewModel::updateDateType,
        onFinanceTypeUpdate = viewModel::updateFinanceType,
        onSelectedDateUpdate = viewModel::updateSelectedDate,
        onCategoryFilterUpdate = viewModel::updateSelectedCategories,
        transactionOverviewState = transactionOverviewState,
        navigateToTransactionDialog = navigateToTransactionDialog,
        onTransactionDelete = viewModel::deleteTransaction,
        onTransactionSelect = viewModel::updateSelectedTransaction,
        onShowSnackbar = onShowSnackbar,
        shouldDisplayUndoTransaction = viewModel.shouldDisplayUndoTransaction,
        undoTransactionRemoval = viewModel::undoTransactionRemoval,
        shouldDisplayUndoTransfer = viewModel.shouldDisplayUndoTransfer,
        undoTransferRemoval = viewModel::undoTransferRemoval,
        clearUndoState = viewModel::clearUndoState,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
@Composable
private fun TransactionOverviewScreen(
    financePanelUiState: FinancePanelUiState,
    transactionOverviewState: TransactionOverviewUiState,
    shouldShowNavigationIcon: Boolean,
    onBackClick: () -> Unit,
    onDateTypeUpdate: (DateType) -> Unit,
    onFinanceTypeUpdate: (FinanceType) -> Unit,
    onSelectedDateUpdate: (Int) -> Unit,
    onCategoryFilterUpdate: (Category, Boolean) -> Unit,
    navigateToTransactionDialog: (walletId: String, transactionId: String?, repeated: Boolean) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    onTransactionDelete: () -> Unit = {},
    onTransactionSelect: (TransactionWithCategory?) -> Unit = {},
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

    when (transactionOverviewState) {
        TransactionOverviewUiState.Loading -> LoadingState(Modifier.fillMaxSize())
        is TransactionOverviewUiState.Success -> {
            val hazeState = rememberHazeState()
            val hazeStyle = HazeMaterials.ultraThin(MaterialTheme.colorScheme.secondaryContainer)
            Scaffold(
                topBar = {
                    TopBar(
                        financePanelUiState = financePanelUiState,
                        shouldShowNavigationIcon = shouldShowNavigationIcon,
                        onBackClick = onBackClick,
                        modifier = Modifier.padding(bottom = 6.dp),
                    )
                },
            ) { paddingValues ->
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 110.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                ) {
                    header(
                        financePanelUiState = financePanelUiState,
                        onDateTypeUpdate = onDateTypeUpdate,
                        onFinanceTypeUpdate = onFinanceTypeUpdate,
                        onSelectedDateUpdate = onSelectedDateUpdate,
                        onCategoryFilterUpdate = onCategoryFilterUpdate,
                    )
                    val transaction = transactionOverviewState.selectedTransaction?.transaction
                    transactions(
                        selectedTransaction = transactionOverviewState.selectedTransaction,
                        transactionsCategories = transactionOverviewState.transactionsCategories,
                        hazeState = hazeState,
                        hazeStyle = hazeStyle,
                        onClick = onTransactionSelect,
                        onRepeatClick = { transactionId ->
                            transaction?.walletOwnerId?.let { navigateToTransactionDialog(it, transactionId, true) }
                        },
                        onEditClick = { transactionId ->
                            transaction?.walletOwnerId?.let { navigateToTransactionDialog(it, transactionId, false) }
                        },
                        onDeleteClick = onTransactionDelete,
                    )
                }
            }
        }
    }
    TrackScreenViewEvent(screenName = "TransactionOverview")
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
private fun TopBar(
    financePanelUiState: FinancePanelUiState,
    shouldShowNavigationIcon: Boolean,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (financePanelUiState) {
        FinancePanelUiState.Loading -> Unit
        FinancePanelUiState.NotShown -> Unit
        is FinancePanelUiState.Shown -> {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(localesR.string.total_balance),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                subtitle = {
                    AnimatedAmount(
                        amount = financePanelUiState.totalBalance,
                        label = "TotalBalance",
                        modifier = Modifier.fillMaxWidth(),
                        currency = financePanelUiState.userCurrency,
                        withApproximatelySign = financePanelUiState.shouldShowApproximately,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                navigationIcon = {
                    if (shouldShowNavigationIcon) {
                        CsIconButton(
                            onClick = onBackClick,
                            icon = CsIcons.Outlined.ArrowBack,
                            contentDescription = stringResource(localesR.string.navigation_back_icon_description),
                        )
                    }
                },
                modifier = modifier,
            )
        }
    }
}

private fun LazyListScope.header(
    financePanelUiState: FinancePanelUiState,
    onDateTypeUpdate: (DateType) -> Unit,
    onFinanceTypeUpdate: (FinanceType) -> Unit,
    onSelectedDateUpdate: (Int) -> Unit,
    onCategoryFilterUpdate: (Category, Boolean) -> Unit,
) {
    when (financePanelUiState) {
        FinancePanelUiState.Loading -> item {
            LoadingState(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
            )
        }

        FinancePanelUiState.NotShown -> Unit
        is FinancePanelUiState.Shown -> {
            item {
                FinancePanel(
                    availableCategories = financePanelUiState.availableCategories,
                    currency = financePanelUiState.userCurrency,
                    expenses = financePanelUiState.expenses,
                    income = financePanelUiState.income,
                    graphData = financePanelUiState.graphData,
                    transactionFilter = financePanelUiState.transactionFilter,
                    onDateTypeUpdate = onDateTypeUpdate,
                    onFinanceTypeUpdate = onFinanceTypeUpdate,
                    onSelectedDateUpdate = onSelectedDateUpdate,
                    onCategoryFilterUpdate = onCategoryFilterUpdate,
                    modifier = Modifier.fillMaxWidth(),
                    shouldShowApproximately = financePanelUiState.shouldShowApproximately,
                )
            }
        }
    }
}