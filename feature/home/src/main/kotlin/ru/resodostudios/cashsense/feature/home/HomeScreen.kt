package ru.resodostudios.cashsense.feature.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import ru.resodostudios.cashsense.core.designsystem.component.CsListItem
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.AccountBalance
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.model.data.ExtendedUserWallet
import ru.resodostudios.cashsense.core.ui.component.AnimatedAmount
import ru.resodostudios.cashsense.core.ui.component.EmptyState
import ru.resodostudios.cashsense.core.ui.component.LoadingState
import ru.resodostudios.cashsense.core.ui.util.formatAmount
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
    onEditWallet: (String) -> Unit,
    onDeleteWallet: (String) -> Unit,
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
        onEditWallet = onEditWallet,
        onDeleteWallet = onDeleteWallet,
        onTransactionCreate = onTransactionCreate,
        highlightSelectedWallet = highlightSelectedWallet,
        onShowSnackbar = onShowSnackbar,
        shouldDisplayUndoWallet = shouldDisplayUndoWallet,
        undoWalletRemoval = undoWalletRemoval,
        clearUndoState = clearUndoState,
        onTotalBalanceClick = onTotalBalanceClick,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun HomeScreen(
    walletsState: WalletsUiState,
    totalBalanceState: TotalBalanceUiState,
    onWalletClick: (String?) -> Unit,
    onTransfer: (String) -> Unit,
    onEditWallet: (String) -> Unit,
    onDeleteWallet: (String) -> Unit,
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

    Box {
        val hazeState = remember { HazeState() }
        var totalBalanceShown by remember { mutableStateOf(false) }

        when (totalBalanceState) {
            TotalBalanceUiState.NotShown -> totalBalanceShown = false
            TotalBalanceUiState.Loading, is TotalBalanceUiState.Shown -> {
                totalBalanceShown = true
                val brushColor =
                    if (totalBalanceState is TotalBalanceUiState.Shown &&
                        totalBalanceState.shouldShowBadIndicator
                    ) {
                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.75f)
                    } else {
                        Color.Transparent
                    }

                val hazeBackgroundColor = MaterialTheme.colorScheme.surface
                TotalBalanceCard(
                    onClick = onTotalBalanceClick,
                    modifier = Modifier
                        .zIndex(1f)
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                        .hazeEffect(hazeState) {
                            blurEnabled = true
                            blurRadius = 22.dp
                            backgroundColor = hazeBackgroundColor
                            progressive = HazeProgressive.verticalGradient(
                                startIntensity = 1f,
                                endIntensity = 0.25f,
                            )
                        }
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(MaterialTheme.colorScheme.surface, brushColor),
                                startY = 95f,
                            )
                        ),
                ) {
                    if (totalBalanceState is TotalBalanceUiState.Shown) {
                        AnimatedAmount(
                            targetState = totalBalanceState.amount,
                            label = "TotalBalance",
                        ) {
                            Text(
                                text = totalBalanceState.amount.formatAmount(
                                    currency = totalBalanceState.userCurrency,
                                    withApproximately = totalBalanceState.shouldShowApproximately,
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    } else {
                        LinearWavyProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 6.dp),
                        )
                    }
                }
            }
        }
        when (walletsState) {
            Loading -> LoadingState(Modifier.fillMaxSize())
            Empty -> EmptyState(localesR.string.home_empty, R.raw.anim_wallets_empty)
            is Success -> {
                val topPadding by animateDpAsState(if (totalBalanceShown) 88.dp else 16.dp)
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
                        bottom = 110.dp,
                        top = topPadding,
                    ),
                ) {
                    wallets(
                        extendedUserWallets = walletsState.extendedUserWallets,
                        selectedWalletId = walletsState.selectedWalletId,
                        onWalletClick = onWalletClick,
                        onTransactionCreate = onTransactionCreate,
                        onTransferClick = onTransfer,
                        onEditClick = onEditWallet,
                        onDeleteClick = { walletId ->
                            onDeleteWallet(walletId)
                            onWalletClick(null)
                        },
                        highlightSelectedWallet = highlightSelectedWallet,
                    )
                }
            }
        }
    }
}

private fun LazyStaggeredGridScope.wallets(
    extendedUserWallets: List<ExtendedUserWallet>,
    selectedWalletId: String?,
    onWalletClick: (String) -> Unit,
    onTransactionCreate: (String) -> Unit,
    onTransferClick: (String) -> Unit,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
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
            onEditClick = onEditClick,
            onDeleteClick = onDeleteClick,
            modifier = Modifier.animateItem(),
            selected = selected,
        )
    }
}

@Composable
private fun TotalBalanceCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    headlineContent: @Composable () -> Unit,
) {
    val borderBrush = Brush.verticalGradient(
        colors = listOf(Color.Transparent, MaterialTheme.colorScheme.outlineVariant),
        startY = 75f,
    )
    OutlinedCard(
        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
        border = BorderStroke(1.dp, borderBrush),
        modifier = modifier,
        onClick = onClick,
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.25f),
        ),
    ) {
        CsListItem(
            leadingContent = {
                Icon(
                    imageVector = CsIcons.Outlined.AccountBalance,
                    contentDescription = null,
                )
            },
            headlineContent = headlineContent,
            overlineContent = {
                Text(
                    text = stringResource(localesR.string.total_balance),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
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
                onWalletClick = {},
                onTransfer = {},
                onEditWallet = {},
                onDeleteWallet = {},
                onTransactionCreate = {},
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
                onWalletClick = {},
                onTransfer = {},
                onEditWallet = {},
                onDeleteWallet = {},
                onTransactionCreate = {},
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
                    shouldShowBadIndicator = true,
                    shouldShowApproximately = true,
                ),
                onWalletClick = {},
                onTransfer = {},
                onEditWallet = {},
                onDeleteWallet = {},
                onTransactionCreate = {},
                highlightSelectedWallet = false,
            )
        }
    }
}