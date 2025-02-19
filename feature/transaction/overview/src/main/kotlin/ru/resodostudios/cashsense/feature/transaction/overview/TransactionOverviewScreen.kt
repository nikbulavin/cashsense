package ru.resodostudios.cashsense.feature.transaction.overview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.designsystem.component.CsAlertDialog
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.ArrowBack
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Delete
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.DateType
import ru.resodostudios.cashsense.core.model.data.FinanceType
import ru.resodostudios.cashsense.core.ui.component.AnimatedAmount
import ru.resodostudios.cashsense.core.ui.component.FinancePanel
import ru.resodostudios.cashsense.core.ui.component.LoadingState
import ru.resodostudios.cashsense.core.ui.component.TransactionBottomSheet
import ru.resodostudios.cashsense.core.ui.transactions
import ru.resodostudios.cashsense.core.ui.util.formatAmount
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
internal fun TransactionOverviewScreen(
    shouldShowTopBar: Boolean,
    onBackClick: () -> Unit,
    onTransactionClick: (walletId: String, transactionId: String?, repeated: Boolean) -> Unit,
    viewModel: TransactionOverviewViewModel = hiltViewModel(),
) {
    val financePanelUiState by viewModel.financePanelUiState.collectAsStateWithLifecycle()
    val transactionOverviewState by viewModel.transactionOverviewUiState.collectAsStateWithLifecycle()

    TransactionOverviewScreen(
        shouldShowTopBar = shouldShowTopBar,
        onBackClick = onBackClick,
        onTransactionClick = onTransactionClick,
        financePanelUiState = financePanelUiState,
        onDateTypeUpdate = viewModel::updateDateType,
        onFinanceTypeUpdate = viewModel::updateFinanceType,
        onSelectedDateUpdate = viewModel::updateSelectedDate,
        onCategorySelect = viewModel::addToSelectedCategories,
        onCategoryDeselect = viewModel::removeFromSelectedCategories,
        transactionOverviewState = transactionOverviewState,
        updateTransactionId = viewModel::updateTransactionId,
        onUpdateTransactionIgnoring = viewModel::updateTransactionIgnoring,
        onDeleteTransaction = viewModel::deleteTransaction,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransactionOverviewScreen(
    financePanelUiState: FinancePanelUiState,
    transactionOverviewState: TransactionOverviewUiState,
    shouldShowTopBar: Boolean,
    onBackClick: () -> Unit,
    onDateTypeUpdate: (DateType) -> Unit,
    onFinanceTypeUpdate: (FinanceType) -> Unit,
    onSelectedDateUpdate: (Int) -> Unit,
    onCategorySelect: (Category) -> Unit,
    onCategoryDeselect: (Category) -> Unit,
    onTransactionClick: (walletId: String, transactionId: String?, repeated: Boolean) -> Unit,
    updateTransactionId: (String) -> Unit = {},
    onUpdateTransactionIgnoring: (Boolean) -> Unit = {},
    onDeleteTransaction: () -> Unit = {},
) {
    when (transactionOverviewState) {
        TransactionOverviewUiState.Loading -> LoadingState(Modifier.fillMaxSize())
        is TransactionOverviewUiState.Success -> {
            var showTransactionBottomSheet by rememberSaveable { mutableStateOf(false) }
            var showTransactionDeletionDialog by rememberSaveable { mutableStateOf(false) }

            if (showTransactionBottomSheet && transactionOverviewState.selectedTransactionCategory != null) {
                val transaction = transactionOverviewState.selectedTransactionCategory.transaction
                TransactionBottomSheet(
                    transactionCategory = transactionOverviewState.selectedTransactionCategory,
                    currency = transactionOverviewState.selectedTransactionCategory.transaction.currency,
                    onDismiss = { showTransactionBottomSheet = false },
                    onIgnoreClick = onUpdateTransactionIgnoring,
                    onRepeatClick = { transactionId ->
                        onTransactionClick(transaction.walletOwnerId, transactionId, true)
                    },
                    onEdit = { transactionId ->
                        onTransactionClick(transaction.walletOwnerId, transactionId, false)
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

            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    TopBar(
                        financePanelUiState = financePanelUiState,
                        shouldShowTopBar = shouldShowTopBar,
                        onBackClick = onBackClick,
                        scrollBehavior = scrollBehavior,
                        modifier = Modifier.padding(bottom = 6.dp),
                    )
                },
            ) { paddingValues ->
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 88.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                ) {
                    header(
                        financePanelUiState = financePanelUiState,
                        onDateTypeUpdate = onDateTypeUpdate,
                        onFinanceTypeUpdate = onFinanceTypeUpdate,
                        onSelectedDateUpdate = onSelectedDateUpdate,
                        onCategorySelect = onCategorySelect,
                        onCategoryDeselect = onCategoryDeselect,
                    )
                    transactions(
                        transactionsCategories = transactionOverviewState.transactionsCategories,
                        onTransactionClick = {
                            updateTransactionId(it)
                            showTransactionBottomSheet = true
                        },
                    )
                }
            }
        }
    }
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
private fun TopBar(
    financePanelUiState: FinancePanelUiState,
    shouldShowTopBar: Boolean,
    onBackClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier,
) {
    when (financePanelUiState) {
        FinancePanelUiState.Loading -> Unit
        FinancePanelUiState.NotShown -> Unit
        is FinancePanelUiState.Shown -> {
            AnimatedVisibility(shouldShowTopBar) {
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
                            targetState = financePanelUiState.totalBalance,
                            label = "TotalBalance",
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = financePanelUiState.totalBalance.formatAmount(
                                    currency = financePanelUiState.userCurrency,
                                    withApproximately = financePanelUiState.shouldShowApproximately,
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = CsIcons.Outlined.ArrowBack,
                                contentDescription = null,
                            )
                        }
                    },
                    windowInsets = WindowInsets(0, 0, 0, 0),
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.topAppBarColors().copy(
                        scrolledContainerColor = MaterialTheme.colorScheme.surface,
                    ),
                    modifier = modifier,
                )
            }
        }
    }
}

private fun LazyListScope.header(
    financePanelUiState: FinancePanelUiState,
    onDateTypeUpdate: (DateType) -> Unit,
    onFinanceTypeUpdate: (FinanceType) -> Unit,
    onSelectedDateUpdate: (Int) -> Unit,
    onCategorySelect: (Category) -> Unit,
    onCategoryDeselect: (Category) -> Unit,
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
                    onCategorySelect = onCategorySelect,
                    onCategoryDeselect = onCategoryDeselect,
                    modifier = Modifier.fillMaxWidth(),
                    shouldShowApproximately = financePanelUiState.shouldShowApproximately,
                )
            }
        }
    }
}