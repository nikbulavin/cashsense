package ru.resodostudios.cashsense.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.designsystem.component.CsTopAppBar
import ru.resodostudios.cashsense.core.designsystem.component.button.CsIconButton
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.AccountBalance
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Settings
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.model.data.ExtendedUserWallet
import ru.resodostudios.cashsense.core.ui.component.EmptyState
import ru.resodostudios.cashsense.core.ui.component.LoadingState
import ru.resodostudios.cashsense.core.ui.util.TrackScreenViewEvent
import ru.resodostudios.cashsense.core.ui.util.formatAmount
import ru.resodostudios.cashsense.core.ui.util.isInCurrentMonthAndYear
import ru.resodostudios.cashsense.feature.home.WalletsUiState.Empty
import ru.resodostudios.cashsense.feature.home.WalletsUiState.Loading
import ru.resodostudios.cashsense.feature.home.WalletsUiState.Success
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
fun HomeScreen(
    onWalletClick: (String?) -> Unit,
    onTransfer: (String) -> Unit,
    highlightSelectedWallet: Boolean = false,
    onTransactionCreate: (String) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    shouldDisplayUndoWallet: Boolean,
    undoWalletRemoval: () -> Unit,
    clearUndoState: () -> Unit,
    onTotalBalanceClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val walletsState by viewModel.walletsUiState.collectAsStateWithLifecycle()

    HomeScreen(
        walletsState = walletsState,
        onWalletClick = {
            viewModel.onWalletClick(it)
            onWalletClick(it)
        },
        onTransfer = onTransfer,
        onTransactionCreate = onTransactionCreate,
        highlightSelectedWallet = highlightSelectedWallet,
        onShowSnackbar = onShowSnackbar,
        shouldDisplayUndoWallet = shouldDisplayUndoWallet,
        undoWalletRemoval = undoWalletRemoval,
        clearUndoState = clearUndoState,
        onTotalBalanceClick = onTotalBalanceClick,
        onSettingsClick = onSettingsClick,
    )
}

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3Api::class,
)
@Composable
internal fun HomeScreen(
    walletsState: WalletsUiState,
    onWalletClick: (String?) -> Unit,
    onTransfer: (String) -> Unit,
    onTransactionCreate: (String) -> Unit,
    highlightSelectedWallet: Boolean,
    onShowSnackbar: suspend (String, String?) -> Boolean = { _, _ -> false },
    shouldDisplayUndoWallet: Boolean = false,
    undoWalletRemoval: () -> Unit = {},
    clearUndoState: () -> Unit = {},
    onTotalBalanceClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
) {
    val walletDeletedMessage = stringResource(localesR.string.wallet_deleted)
    val undoText = stringResource(localesR.string.undo)

    LaunchedEffect(shouldDisplayUndoWallet) {
        if (shouldDisplayUndoWallet) {
            val snackBarResult = onShowSnackbar(walletDeletedMessage, undoText)
            if (snackBarResult) {
                undoWalletRemoval()
            } else {
                clearUndoState()
            }
        }
    }
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        clearUndoState()
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            CsTopAppBar(
                titleRes = R.string.app_name,
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    scrolledContainerColor = Color.Transparent,
                    containerColor = Color.Transparent,
                ),
                navigationIcon = {
                    CsIconButton(
                        onClick = onTotalBalanceClick,
                        icon = CsIcons.Outlined.AccountBalance,
                        contentDescription = stringResource(localesR.string.total_balance),
                        tooltipPosition = TooltipAnchorPosition.Right,
                    )
                },
                actions = {
                    CsIconButton(
                        onClick = onSettingsClick,
                        icon = CsIcons.Outlined.Settings,
                        contentDescription = stringResource(localesR.string.settings_title),
                        tooltipPosition = TooltipAnchorPosition.Left,
                    )
                },
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { innerPadding ->
        when (walletsState) {
            Loading -> LoadingState(Modifier.fillMaxSize())
            Empty -> EmptyState(localesR.string.home_empty, R.raw.anim_wallets_empty)
            is Success -> {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Adaptive(300.dp),
                    verticalItemSpacing = 16.dp,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 110.dp + innerPadding.calculateBottomPadding(),
                        top = innerPadding.calculateTopPadding(),
                    ),
                ) {
                    wallets(
                        extendedUserWallets = walletsState.extendedUserWallets,
                        selectedWalletId = walletsState.selectedWalletId,
                        onWalletClick = onWalletClick,
                        onTransactionCreate = onTransactionCreate,
                        onTransferClick = onTransfer,
                        highlightSelectedWallet = highlightSelectedWallet,
                    )
                }
            }
        }
    }
    TrackScreenViewEvent(screenName = "Home")
}

private fun LazyStaggeredGridScope.wallets(
    extendedUserWallets: List<ExtendedUserWallet>,
    selectedWalletId: String?,
    onWalletClick: (String) -> Unit,
    onTransactionCreate: (String) -> Unit,
    onTransferClick: (String) -> Unit,
    highlightSelectedWallet: Boolean = false,
) {
    items(
        items = extendedUserWallets,
        key = { it.wallet.id },
        contentType = { "WalletCard" },
    ) { walletData ->
        val selected = highlightSelectedWallet && walletData.wallet.id == selectedWalletId
        val (expensesList, incomeList) = walletData.transactions
            .asSequence()
            .filter { !it.ignored && it.timestamp.isInCurrentMonthAndYear() }
            .partition { it.amount.signum() < 0 }
        val expenses = expensesList.sumOf { it.amount }.abs()
        val income = incomeList.sumOf { it.amount }

        val shouldShowExpensesTag by remember { derivedStateOf { expenses.signum() > 0 } }
        val shouldShowIncomeTag by remember { derivedStateOf { income.signum() > 0 } }
        val currency = walletData.wallet.currency

        WalletCard(
            wallet = walletData.wallet,
            formattedCurrentBalance = walletData.currentBalance.formatAmount(currency),
            isPrimary = walletData.isPrimary,
            formattedExpenses = expenses.formatAmount(currency),
            formattedIncome = income.formatAmount(currency),
            shouldShowExpensesTag = shouldShowExpensesTag,
            shouldShowIncomeTag = shouldShowIncomeTag,
            onWalletClick = onWalletClick,
            onNewTransactionClick = onTransactionCreate,
            onTransferClick = onTransferClick,
            modifier = Modifier.animateItem(),
            selected = selected,
        )
    }
}

@Preview
@Composable
fun HomeScreenPopulatedPreview(
    @PreviewParameter(ExtendedUserWalletPreviewParameterProvider::class)
    extendedUserWallets: List<ExtendedUserWallet>,
) {
    CsTheme {
        Surface {
            HomeScreen(
                walletsState = Success(
                    selectedWalletId = null,
                    extendedUserWallets = extendedUserWallets,
                ),
                onWalletClick = { },
                onTransfer = { },
                onTransactionCreate = { },
                highlightSelectedWallet = false,
            )
        }
    }
}