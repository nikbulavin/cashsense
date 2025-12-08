package ru.resodostudios.cashsense.feature.transaction.overview.impl

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
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
import ru.resodostudios.cashsense.core.designsystem.component.AnimatedIcon
import ru.resodostudios.cashsense.core.designsystem.component.button.CsIconButton
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.ArrowBack
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.SentimentCalm
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.SentimentExcited
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.SentimentFrustrated
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.SentimentNeutral
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.SentimentSad
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.DateType
import ru.resodostudios.cashsense.core.model.data.FinanceType
import ru.resodostudios.cashsense.core.model.data.Transaction
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
    onTransactionSelect: (Transaction?) -> Unit = {},
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

    if (transactionOverviewState is TransactionOverviewUiState.Loading ||
        financePanelUiState is FinancePanelUiState.Loading
    ) {
        LoadingState(Modifier.fillMaxSize())
    }

    when (transactionOverviewState) {
        TransactionOverviewUiState.Loading -> Unit
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
                    val transaction = transactionOverviewState.selectedTransaction
                    transactions(
                        selectedTransaction = transactionOverviewState.selectedTransaction,
                        groupedTransactions = transactionOverviewState.groupedTransactions,
                        hazeState = hazeState,
                        hazeStyle = hazeStyle,
                        onClick = onTransactionSelect,
                        onRepeatClick = { transactionId ->
                            transaction?.walletOwnerId?.let { walletId ->
                                navigateToTransactionDialog(walletId, transactionId, true)
                            }
                        },
                        onEditClick = { transactionId ->
                            transaction?.walletOwnerId?.let { walletId ->
                                navigateToTransactionDialog(walletId, transactionId, false)
                            }
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
                        formattedAmount = financePanelUiState.formattedTotalBalance,
                        label = "TotalBalance",
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                navigationIcon = {
                    if (shouldShowNavigationIcon) {
                        CsIconButton(
                            onClick = onBackClick,
                            icon = CsIcons.Outlined.ArrowBack,
                            contentDescription = stringResource(localesR.string.navigation_back_icon_description),
                            tooltipPosition = TooltipAnchorPosition.Right,
                        )
                    }
                },
                actions = {
                    FinancialHealthIcon(
                        financialHealth = financePanelUiState.financialHealth,
                        modifier = Modifier.padding(end = 8.dp),
                    )
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
        FinancePanelUiState.Loading -> Unit
        FinancePanelUiState.NotShown -> Unit
        is FinancePanelUiState.Shown -> {
            item {
                FinancePanel(
                    availableCategories = financePanelUiState.availableCategories,
                    currency = financePanelUiState.userCurrency,
                    formattedExpenses = financePanelUiState.formattedExpenses,
                    formattedIncome = financePanelUiState.formattedIncome,
                    graphData = financePanelUiState.graphData,
                    transactionFilter = financePanelUiState.transactionFilter,
                    onDateTypeUpdate = onDateTypeUpdate,
                    onFinanceTypeUpdate = onFinanceTypeUpdate,
                    onSelectedDateUpdate = onSelectedDateUpdate,
                    onCategoryFilterUpdate = onCategoryFilterUpdate,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun FinancialHealthIcon(
    financialHealth: FinancialHealth,
    modifier: Modifier = Modifier,
) {
    val badColor = MaterialTheme.colorScheme.errorContainer
    val neutralColor = MaterialTheme.colorScheme.surfaceVariant
    val goodColor = MaterialTheme.colorScheme.primaryContainer

    val (icon, targetColor) = when (financialHealth) {
        FinancialHealth.VERY_BAD -> CsIcons.Outlined.SentimentFrustrated to badColor
        FinancialHealth.BAD -> CsIcons.Outlined.SentimentSad to badColor
        FinancialHealth.NEUTRAL -> CsIcons.Outlined.SentimentNeutral to neutralColor
        FinancialHealth.GOOD -> CsIcons.Outlined.SentimentCalm to goodColor
        FinancialHealth.VERY_GOOD -> CsIcons.Outlined.SentimentExcited to goodColor
    }
    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        label = "FinancialHealthColor",
        animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
    )
    Surface(
        shape = CircleShape,
        color = animatedColor,
        modifier = modifier,
    ) {
        AnimatedIcon(
            icon = icon,
            contentDescription = stringResource(localesR.string.financial_health),
            modifier = Modifier.padding(4.dp),
        )
    }
}