package ru.resodostudios.cashsense.feature.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import ru.resodostudios.cashsense.core.designsystem.component.CsTopAppBar
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.designsystem.theme.dropShadow
import ru.resodostudios.cashsense.core.model.data.ExtendedUserWallet
import ru.resodostudios.cashsense.core.ui.component.EmptyState
import ru.resodostudios.cashsense.core.ui.component.LoadingState
import ru.resodostudios.cashsense.core.ui.util.TrackScreenViewEvent
import ru.resodostudios.cashsense.core.ui.util.isInCurrentMonthAndYear
import ru.resodostudios.cashsense.core.util.getUsdCurrency
import ru.resodostudios.cashsense.feature.home.WalletsUiState.Empty
import ru.resodostudios.cashsense.feature.home.WalletsUiState.Loading
import ru.resodostudios.cashsense.feature.home.WalletsUiState.Success
import java.math.BigDecimal
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
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val walletsState by viewModel.walletsUiState.collectAsStateWithLifecycle()
    val totalBalanceState by viewModel.totalBalanceUiState.collectAsStateWithLifecycle()

    HomeScreen(
        walletsState = walletsState,
        totalBalanceState = totalBalanceState,
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
    )
}

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalHazeApi::class,
    ExperimentalHazeMaterialsApi::class,
    ExperimentalMaterial3Api::class,
)
@Composable
internal fun HomeScreen(
    walletsState: WalletsUiState,
    totalBalanceState: TotalBalanceUiState,
    onWalletClick: (String?) -> Unit,
    onTransfer: (String) -> Unit,
    onTransactionCreate: (String) -> Unit,
    highlightSelectedWallet: Boolean,
    onShowSnackbar: suspend (String, String?) -> Boolean = { _, _ -> false },
    shouldDisplayUndoWallet: Boolean = false,
    undoWalletRemoval: () -> Unit = {},
    clearUndoState: () -> Unit = {},
    onTotalBalanceClick: () -> Unit = {},
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
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { innerPadding ->
        Box {
            val hazeState = rememberHazeState()
            val hazeStyle = HazeMaterials.ultraThin(MaterialTheme.colorScheme.surface)

            TotalBalanceCard(
                totalBalanceState = totalBalanceState,
                onClick = onTotalBalanceClick,
                modifier = Modifier
                    .zIndex(1f)
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .dropShadow(MaterialTheme.shapes.extraLarge)
                    .clip(MaterialTheme.shapes.extraLarge)
                    .hazeEffect(hazeState, hazeStyle) {
                        blurEnabled = true
                        inputScale = HazeInputScale.Auto
                        noiseFactor = 0f
                    },
            )
            when (walletsState) {
                Loading -> LoadingState(Modifier.fillMaxSize())
                Empty -> EmptyState(localesR.string.home_empty, R.raw.anim_wallets_empty)
                is Success -> {
                    val topPadding by animateDpAsState(if (totalBalanceState !is TotalBalanceUiState.NotShown) 88.dp else 0.dp)
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Adaptive(300.dp),
                        verticalItemSpacing = 16.dp,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .hazeSource(hazeState),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 110.dp + innerPadding.calculateBottomPadding(),
                            top = topPadding + innerPadding.calculateTopPadding(),
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
        key = { it.userWallet.id },
        contentType = { "WalletCard" },
    ) { walletData ->
        val selected = highlightSelectedWallet && walletData.userWallet.id == selectedWalletId
        val (expenses, income) = walletData.transactionsWithCategories
            .asSequence()
            .map { it.transaction }
            .filter { !it.ignored && it.timestamp.isInCurrentMonthAndYear() }
            .partition { it.amount.signum() < 0 }

        val totalExpenses by remember(expenses) {
            derivedStateOf {
                expenses.sumOf { it.amount }.abs()
            }
        }
        val totalIncome by remember(income) {
            derivedStateOf {
                income.sumOf { it.amount }
            }
        }

        WalletCard(
            userWallet = walletData.userWallet,
            expenses = totalExpenses,
            income = totalIncome,
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
fun HomeScreenLoadingPreview() {
    CsTheme {
        Surface {
            HomeScreen(
                walletsState = Loading,
                totalBalanceState = TotalBalanceUiState.Loading,
                onWalletClick = { },
                onTransfer = { },
                onTransactionCreate = { },
                highlightSelectedWallet = false,
            )
        }
    }
}

@Preview
@Composable
fun HomeScreenEmptyPreview() {
    CsTheme {
        Surface {
            HomeScreen(
                walletsState = Empty,
                totalBalanceState = TotalBalanceUiState.NotShown,
                onWalletClick = { },
                onTransfer = { },
                onTransactionCreate = { },
                highlightSelectedWallet = false,
            )
        }
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
                totalBalanceState = TotalBalanceUiState.Shown(
                    amount = BigDecimal(5000),
                    userCurrency = getUsdCurrency(),
                    shouldShowApproximately = true,
                    financialHealth = FinancialHealth.NEUTRAL,
                ),
                onWalletClick = { },
                onTransfer = { },
                onTransactionCreate = { },
                highlightSelectedWallet = false,
            )
        }
    }
}