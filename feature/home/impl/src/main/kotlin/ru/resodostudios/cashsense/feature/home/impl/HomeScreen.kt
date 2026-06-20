package ru.resodostudios.cashsense.feature.home.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.datetime.LocalDate
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.model.ExtendedUserWallet
import ru.resodostudios.cashsense.core.ui.component.IllustratedMessage
import ru.resodostudios.cashsense.core.ui.component.LoadingState
import ru.resodostudios.cashsense.core.ui.util.TrackScreenViewEvent
import ru.resodostudios.cashsense.feature.home.impl.model.UiWallet
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
internal fun HomeScreen(
    onWalletClick: (String) -> Unit,
    onTransfer: (String) -> Unit,
    shouldHighlightSelectedWallet: Boolean = false,
    onTransactionCreate: (String) -> Unit,
    onTransactionClick: (String) -> Unit,
    onTotalBalanceClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val walletsState by viewModel.walletsUiState.collectAsStateWithLifecycle()
    val searchResultState by viewModel.searchResultUiState.collectAsStateWithLifecycle()
    val searchFilterState by viewModel.searchFilterState.collectAsStateWithLifecycle()

    HomeScreen(
        walletsState = walletsState,
        searchResultState = searchResultState,
        searchFilterState = searchFilterState,
        onSearch = viewModel::onSearch,
        onSearchFilterWalletToggle = viewModel::toggleWalletSelection,
        onSearchFilterDateRangeChange = viewModel::onDateRangeUpdate,
        onWalletClick = {
            viewModel.onWalletClick(it)
            onWalletClick(it)
        },
        onTransfer = onTransfer,
        onTransactionCreate = onTransactionCreate,
        onTransactionClick = onTransactionClick,
        shouldHighlightSelectedWallet = shouldHighlightSelectedWallet,
        onTotalBalanceClick = onTotalBalanceClick,
        onSettingsClick = onSettingsClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    walletsState: WalletsUiState,
    searchResultState: SearchResultUiState,
    searchFilterState: SearchFilterState,
    onSearch: (String) -> Unit,
    onSearchFilterWalletToggle: (String) -> Unit,
    onSearchFilterDateRangeChange: (LocalDate?, LocalDate?) -> Unit,
    onWalletClick: (String) -> Unit,
    onTransfer: (String) -> Unit,
    onTransactionCreate: (String) -> Unit,
    onTransactionClick: (String) -> Unit,
    shouldHighlightSelectedWallet: Boolean,
    onTotalBalanceClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
) {
    val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()

    Scaffold(
        topBar = {
            CsAppBarWithSearch(
                scrollBehavior = scrollBehavior,
                searchResultState = searchResultState,
                searchFilterState = searchFilterState,
                walletIdsAndTitles = if (walletsState is WalletsUiState.Success) {
                    walletsState.walletIdsAndTitles
                } else {
                    emptyMap()
                },
                onSearch = onSearch,
                onSearchFilterWalletToggle = onSearchFilterWalletToggle,
                onSearchFilterDateRangeChange = onSearchFilterDateRangeChange,
                onTransactionClick = onTransactionClick,
                onTotalBalanceClick = onTotalBalanceClick,
                onSettingsClick = onSettingsClick,
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { innerPadding ->
        when (walletsState) {
            WalletsUiState.Loading -> LoadingState(Modifier.fillMaxSize())
            WalletsUiState.Empty -> IllustratedMessage(
                localesR.string.home_empty,
                R.raw.anim_wallets_empty,
            )

            is WalletsUiState.Success -> {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Adaptive(300.dp),
                    verticalItemSpacing = 16.dp,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 110.dp + innerPadding.calculateBottomPadding(),
                        top = 12.dp + innerPadding.calculateTopPadding(),
                    ),
                ) {
                    wallets(
                        uiWallets = walletsState.uiWallets,
                        selectedWalletId = walletsState.selectedWalletId,
                        onWalletClick = onWalletClick,
                        onTransactionCreate = onTransactionCreate,
                        onTransferClick = onTransfer,
                        shouldHighlightSelectedWallet = shouldHighlightSelectedWallet,
                    )
                }
            }
        }
    }
    TrackScreenViewEvent(screenName = "Home")
}

private fun LazyStaggeredGridScope.wallets(
    uiWallets: List<UiWallet>,
    selectedWalletId: String?,
    onWalletClick: (String) -> Unit,
    onTransactionCreate: (String) -> Unit,
    onTransferClick: (String) -> Unit,
    shouldHighlightSelectedWallet: Boolean = false,
) {
    items(
        items = uiWallets,
        key = { it.extendedUserWallet.wallet.id },
        contentType = { "WalletCard" },
    ) { uiWallet ->
        WalletCard(
            uiWallet = uiWallet,
            onWalletClick = onWalletClick,
            onNewTransactionClick = onTransactionCreate,
            onTransferClick = onTransferClick,
            modifier = Modifier.animateItem(),
            selected = shouldHighlightSelectedWallet && uiWallet.extendedUserWallet.wallet.id == selectedWalletId,
        )
    }
}

@Preview
@Composable
private fun HomeScreenPopulatedPreview(
    @PreviewParameter(ExtendedUserWalletPreviewParameterProvider::class)
    extendedUserWallets: List<ExtendedUserWallet>,
) {
    CsTheme {
        Surface {
            HomeScreen(
                walletsState = WalletsUiState.Success(
                    selectedWalletId = null,
                    uiWallets = extendedUserWallets.map {
                        UiWallet(
                            extendedUserWallet = it,
                            expenses = 500.toBigDecimal(),
                            income = 500.toBigDecimal(),
                        )
                    },
                    walletIdsAndTitles = emptyMap(),
                ),
                searchResultState = SearchResultUiState.EmptyQuery,
                searchFilterState = SearchFilterState(),
                onSearch = {},
                onSearchFilterWalletToggle = {},
                onSearchFilterDateRangeChange = { _, _ -> },
                onWalletClick = {},
                onTransfer = {},
                onTransactionCreate = {},
                onTransactionClick = { _ -> },
                shouldHighlightSelectedWallet = false,
            )
        }
    }
}
