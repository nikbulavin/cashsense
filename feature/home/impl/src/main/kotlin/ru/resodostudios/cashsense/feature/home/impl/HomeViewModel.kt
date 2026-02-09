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

    val searchResultUiState = searchQuery.flatMapLatest { query ->
        if (query.isBlank()) {
            flowOf(SearchResultUiState.EmptyQuery)
        } else {
            getExtendedUserWallets.invoke()
                .map { extendedUserWallets ->
                    SearchResultUiState.Success(
                        transactions = extendedUserWallets
                            .flatMap { it.transactions }
                            .filter { transaction ->
                                transaction.description?.contains(query, true) == true ||
                                        transaction.amount.toPlainString().contains(query)
                            },
                    )
                }
                .catch { SearchResultUiState.LoadFailed }
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
        if (extendedUserWallets.isEmpty()) {
            WalletsUiState.Empty
        } else {
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

    @AssistedFactory
    interface Factory {
        fun create(key: HomeNavKey): HomeViewModel
    }
}

sealed interface WalletsUiState {

    data object Loading : WalletsUiState

    data object Empty : WalletsUiState

    data class Success(
        val selectedWalletId: String?,
        val uiWallets: List<UiWallet>,
    ) : WalletsUiState
}

sealed interface SearchResultUiState {

    data object Loading : SearchResultUiState

    data object EmptyQuery : SearchResultUiState

    data object LoadFailed : SearchResultUiState

    data class Success(
        val transactions: List<Transaction> = emptyList(),
    ) : SearchResultUiState
}

private const val SELECTED_WALLET_ID_KEY = "selectedWalletId"
