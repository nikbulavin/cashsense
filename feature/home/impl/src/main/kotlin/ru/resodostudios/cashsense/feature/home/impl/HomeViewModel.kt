package ru.resodostudios.cashsense.feature.home.impl

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ru.resodostudios.cashsense.core.domain.GetExtendedUserWalletsUseCase
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.network.CsDispatchers.Default
import ru.resodostudios.cashsense.core.network.Dispatcher
import ru.resodostudios.cashsense.core.ui.util.isInCurrentMonthAndYear
import ru.resodostudios.cashsense.feature.home.api.HomeNavKey
import ru.resodostudios.cashsense.feature.home.impl.model.UiWallet
import kotlin.time.Duration.Companion.seconds

@HiltViewModel(assistedFactory = HomeViewModel.Factory::class)
internal class HomeViewModel @AssistedInject constructor(
    private val savedStateHandle: SavedStateHandle,
    getExtendedUserWallets: GetExtendedUserWalletsUseCase,
    @Dispatcher(Default) private val defaultDispatcher: CoroutineDispatcher,
    @Assisted val key: HomeNavKey,
) : ViewModel() {

    private val selectedWalletId = savedStateHandle.getStateFlow(
        key = SELECTED_WALLET_ID_KEY,
        initialValue = key.walletId,
    )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _searchFilterState = MutableStateFlow(SearchFilterState())
    val searchFilterState = _searchFilterState.asStateFlow()

    val searchResultUiState = combine(
        searchQuery,
        searchFilterState,
    ) { query, filterState ->
        query to filterState
    }
        .flatMapLatest { (query, filterState) ->
            if (query.isBlank()) {
                flowOf(SearchResultUiState.EmptyQuery)
            } else {
                getExtendedUserWallets.invoke()
                    .map { extendedUserWallets ->
                        runCatching {
                            SearchResultUiState.Success(
                                transactions = extendedUserWallets
                                    .filter { it.wallet.id in filterState.selectedWalletIds || filterState.selectedWalletIds.isEmpty() }
                                    .flatMap { it.transactions }
                                    .filter { transaction ->
                                        val inDateRange =
                                            if (filterState.selectedDateRange != null) {
                                                val (start, end) = filterState.selectedDateRange
                                                transaction.timestamp
                                                    .toLocalDateTime(TimeZone.currentSystemDefault())
                                                    .date in start!!..end!!
                                            } else {
                                                true
                                            }
                                        inDateRange && (transaction.description?.contains(
                                            query,
                                            true,
                                        ) == true || query in transaction.amount.toPlainString())
                                    },
                            )
                        }.getOrDefault(SearchResultUiState.LoadFailed)
                    }
                    .catch { emit(SearchResultUiState.LoadFailed) }
            }
        }
        .flowOn(defaultDispatcher)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = SearchResultUiState.Loading,
        )

    val walletsUiState: StateFlow<WalletsUiState> = combine(
        selectedWalletId,
        getExtendedUserWallets.invoke(),
    ) { selectedWalletId, extendedUserWallets ->
        if (extendedUserWallets.isEmpty()) return@combine WalletsUiState.Empty
        val uiWallets = extendedUserWallets.map { walletData ->
            val (expenses, income) = walletData.transactions
                .asSequence()
                .filter { !it.ignored && it.timestamp.isInCurrentMonthAndYear() }
                .partition { it.amount.signum() < 0 }
                .let { (expensesList, incomeList) ->
                    val totalExpenses = expensesList.sumOf { it.amount }.abs()
                    val totalIncome = incomeList.sumOf { it.amount }
                    totalExpenses to totalIncome
                }

            UiWallet(
                extendedUserWallet = walletData,
                expenses = expenses,
                income = income,
            )
        }
        WalletsUiState.Success(
            selectedWalletId = selectedWalletId,
            uiWallets = uiWallets,
        )
    }
        .flowOn(defaultDispatcher)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = WalletsUiState.Loading,
        )

    fun onWalletClick(walletId: String?) {
        savedStateHandle[SELECTED_WALLET_ID_KEY] = walletId
    }

    fun onSearch(query: String) {
        _searchQuery.value = query
    }

    fun toggleWalletSelection(walletId: String) {
        _searchFilterState.update { state ->
            state.copy(
                selectedWalletIds = if (walletId in state.selectedWalletIds) {
                    state.selectedWalletIds - walletId
                } else {
                    state.selectedWalletIds + walletId
                },
            )
        }
    }

    fun onDateRangeUpdate(startDate: LocalDate?, endDate: LocalDate?) {
        _searchFilterState.update { state ->
            state.copy(
                selectedDateRange = if (startDate != null && endDate != null) {
                    startDate to endDate
                } else {
                    null
                },
            )
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(key: HomeNavKey): HomeViewModel
    }
}

internal sealed interface WalletsUiState {

    data object Loading : WalletsUiState

    data object Empty : WalletsUiState

    data class Success(
        val selectedWalletId: String?,
        val uiWallets: List<UiWallet>,
    ) : WalletsUiState
}

internal sealed interface SearchResultUiState {

    data object Loading : SearchResultUiState

    data object EmptyQuery : SearchResultUiState

    data object LoadFailed : SearchResultUiState

    data class Success(
        val transactions: List<Transaction>,
    ) : SearchResultUiState
}

internal data class SearchFilterState(
    val selectedWalletIds: List<String> = emptyList(),
    val selectedDateRange: Pair<LocalDate?, LocalDate?>? = null,
)

private const val SELECTED_WALLET_ID_KEY = "selectedWalletId"
