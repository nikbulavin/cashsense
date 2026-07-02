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
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ru.resodostudios.cashsense.core.common.CsDispatchers.Default
import ru.resodostudios.cashsense.core.common.Dispatcher
import ru.resodostudios.cashsense.core.domain.GetExtendedUserWalletsUseCase
import ru.resodostudios.cashsense.core.model.Transaction
import ru.resodostudios.cashsense.core.ui.groupByDate
import ru.resodostudios.cashsense.core.ui.util.isInCurrentMonthAndYear
import ru.resodostudios.cashsense.feature.home.api.HomeNavKey
import ru.resodostudios.cashsense.feature.home.impl.model.UiWallet
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

@HiltViewModel(assistedFactory = HomeViewModel.Factory::class)
internal class HomeViewModel @AssistedInject constructor(
    private val savedStateHandle: SavedStateHandle,
    getExtendedUserWallets: GetExtendedUserWalletsUseCase,
    @Dispatcher(Default) private val defaultDispatcher: CoroutineDispatcher,
    @Assisted private val key: HomeNavKey,
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
        getExtendedUserWallets.invoke()
    ) { query, filterState, extendedUserWallets ->
        if (query.isBlank()) return@combine SearchResultUiState.EmptyQuery
        runCatching {
            val timeZone = TimeZone.currentSystemDefault()

            val dateRangeStart = filterState.selectedDateRange?.first
            val dateRangeEnd = filterState.selectedDateRange?.second
            val filterWallets = filterState.selectedWalletIds.isNotEmpty()

            val filteredTransactions = extendedUserWallets
                .asSequence()
                .filter { !filterWallets || it.wallet.id in filterState.selectedWalletIds }
                .flatMap { it.transactions }
                .filter { transaction ->
                    val inDateRange = if (dateRangeStart != null && dateRangeEnd != null) {
                        val transactionDate = transaction.timestamp.toLocalDateTime(timeZone).date
                        transactionDate in dateRangeStart..dateRangeEnd
                    } else {
                        true
                    }

                    if (!inDateRange) return@filter false

                    val matchesDescription = transaction.description
                        ?.contains(query, ignoreCase = true) == true
                    matchesDescription || query in transaction.amount.toPlainString()
                }
                .toList()

            SearchResultUiState.Success(
                groupedTransactions = filteredTransactions.groupByDate(timeZone),
            )
        }.getOrDefault(SearchResultUiState.LoadFailed)
    }
        .catch { emit(SearchResultUiState.LoadFailed) }
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
            walletIdsAndTitles = extendedUserWallets.associate { it.wallet.id to it.wallet.title },
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
        val walletIdsAndTitles: Map<String, String>,
    ) : WalletsUiState
}

internal sealed interface SearchResultUiState {

    data object Loading : SearchResultUiState

    data object EmptyQuery : SearchResultUiState

    data object LoadFailed : SearchResultUiState

    data class Success(
        val groupedTransactions: Map<Instant, List<Transaction>>,
    ) : SearchResultUiState
}

internal data class SearchFilterState(
    val selectedWalletIds: List<String> = emptyList(),
    val selectedDateRange: Pair<LocalDate, LocalDate>? = null,
)

private const val SELECTED_WALLET_ID_KEY = "selectedWalletId"
