package ru.resodostudios.cashsense.feature.wallet.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus
import ru.resodostudios.cashsense.core.data.repository.TransactionsRepository
import ru.resodostudios.cashsense.core.data.repository.UserDataRepository
import ru.resodostudios.cashsense.core.domain.GetExtendedUserWalletUseCase
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.DateType
import ru.resodostudios.cashsense.core.model.data.DateType.ALL
import ru.resodostudios.cashsense.core.model.data.DateType.MONTH
import ru.resodostudios.cashsense.core.model.data.DateType.WEEK
import ru.resodostudios.cashsense.core.model.data.DateType.YEAR
import ru.resodostudios.cashsense.core.model.data.FinanceType
import ru.resodostudios.cashsense.core.model.data.FinanceType.NOT_SET
import ru.resodostudios.cashsense.core.model.data.TransactionFilter
import ru.resodostudios.cashsense.core.model.data.TransactionWithCategory
import ru.resodostudios.cashsense.core.model.data.Transfer
import ru.resodostudios.cashsense.core.model.data.UserWallet
import ru.resodostudios.cashsense.core.network.CsDispatchers.Default
import ru.resodostudios.cashsense.core.network.Dispatcher
import ru.resodostudios.cashsense.core.ui.groupByDate
import ru.resodostudios.cashsense.core.ui.util.applyTransactionFilter
import ru.resodostudios.cashsense.core.ui.util.getCurrentZonedDateTime
import ru.resodostudios.cashsense.core.ui.util.getGraphData
import ru.resodostudios.cashsense.core.ui.util.isInCurrentMonthAndYear
import java.math.BigDecimal
import kotlin.time.Instant

@HiltViewModel(assistedFactory = WalletViewModel.Factory::class)
class WalletViewModel @AssistedInject constructor(
    private val transactionsRepository: TransactionsRepository,
    private val userDataRepository: UserDataRepository,
    getExtendedUserWallet: GetExtendedUserWalletUseCase,
    @Assisted val walletId: String,
    @Dispatcher(Default) private val defaultDispatcher: CoroutineDispatcher,
) : ViewModel() {

    var shouldDisplayUndoTransaction by mutableStateOf(false)
    private var lastRemovedTransaction: TransactionWithCategory? = null

    var shouldDisplayUndoTransfer by mutableStateOf(false)
    private var lastRemovedTransfer: Transfer? = null

    private val transactionFilterState = MutableStateFlow(
        TransactionFilter(
            selectedCategories = emptySet(),
            financeType = NOT_SET,
            dateType = ALL,
            selectedDate = getCurrentZonedDateTime().date,
        )
    )

    private val selectedTransactionState = MutableStateFlow<TransactionWithCategory?>(null)

    val walletUiState: StateFlow<WalletUiState> = combine(
        getExtendedUserWallet.invoke(walletId),
        transactionFilterState,
        selectedTransactionState,
    ) { extendedUserWallet, transactionFilter, selectedTransaction ->
        val filterableTransactions = extendedUserWallet.transactionsWithCategories
            .applyTransactionFilter(transactionFilter)

        val filteredTransactions = filterableTransactions.transactionsCategories
            .filter {
                !it.transaction.ignored && if (transactionFilter.dateType == ALL) {
                    it.transaction.timestamp.isInCurrentMonthAndYear()
                } else true
            }
        val (expenses, income) = filteredTransactions.partition { it.transaction.amount.signum() < 0 }

        val graphData = filteredTransactions.getGraphData(transactionFilter.dateType)

        WalletUiState.Success(
            transactionFilter = transactionFilter,
            income = income.sumOf { it.transaction.amount },
            expenses = expenses.sumOf { it.transaction.amount }.abs(),
            graphData = graphData,
            userWallet = extendedUserWallet.userWallet,
            selectedTransaction = selectedTransaction,
            transactionsCategories = filterableTransactions.transactionsCategories.groupByDate(),
            availableCategories = filterableTransactions.availableCategories,
        )
    }
        .flowOn(defaultDispatcher)
        .catch { WalletUiState.Loading }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = WalletUiState.Loading,
        )

    fun updateSelectedTransaction(transaction: TransactionWithCategory?) {
        selectedTransactionState.value = transaction
    }

    fun deleteTransaction() {
        viewModelScope.launch {
            selectedTransactionState.value?.let { selectedTransaction ->
                if (selectedTransaction.transaction.transferId != null) {
                    val depositTransaction = transactionsRepository.getTransfer(
                        selectedTransaction.transaction.transferId!!,
                        selectedTransaction.transaction.walletOwnerId,
                    ).first().depositTransaction
                    lastRemovedTransfer = Transfer(selectedTransaction, depositTransaction)
                    transactionsRepository.deleteTransfer(selectedTransaction.transaction.transferId!!)
                    shouldDisplayUndoTransfer = true
                } else {
                    lastRemovedTransaction = selectedTransactionState.value
                    transactionsRepository.deleteTransaction(selectedTransaction.transaction.id)
                    shouldDisplayUndoTransaction = true
                }
            }
            updateSelectedTransaction(null)
        }
    }

    fun setPrimaryWalletId(id: String, isPrimary: Boolean) {
        viewModelScope.launch {
            userDataRepository.setPrimaryWallet(id, isPrimary)
        }
    }

    fun updateSelectedCategories(category: Category, selected: Boolean) {
        transactionFilterState.update {
            it.copy(
                selectedCategories = if (selected) {
                    it.selectedCategories + category
                } else {
                    it.selectedCategories - category
                },
            )
        }
    }

    fun updateFinanceType(financeType: FinanceType) {
        transactionFilterState.update {
            it.copy(financeType = financeType)
        }
        if (financeType == NOT_SET) {
            transactionFilterState.update {
                it.copy(
                    selectedCategories = emptySet(),
                )
            }
        }
    }

    fun updateDateType(dateType: DateType) {
        transactionFilterState.update {
            it.copy(
                dateType = dateType,
                selectedDate = getCurrentZonedDateTime().date,
            )
        }
    }

    fun updateSelectedDate(dateOffset: Int) {
        when (transactionFilterState.value.dateType) {
            MONTH -> {
                transactionFilterState.update {
                    it.copy(
                        selectedDate = it.selectedDate.plus(dateOffset, DateTimeUnit.MONTH),
                    )
                }
            }

            YEAR -> {
                transactionFilterState.update {
                    it.copy(
                        selectedDate = it.selectedDate.plus(dateOffset, DateTimeUnit.YEAR),
                    )
                }
            }

            ALL, WEEK -> {}
        }
    }

    fun undoTransactionRemoval() {
        viewModelScope.launch {
            lastRemovedTransaction?.let {
                transactionsRepository.upsertTransaction(it)
            }
            clearUndoState()
        }
    }

    fun undoTransferRemoval() {
        viewModelScope.launch {
            lastRemovedTransfer?.let {
                transactionsRepository.upsertTransfer(it)
            }
            clearUndoState()
        }
    }

    fun clearUndoState() {
        lastRemovedTransaction = null
        lastRemovedTransfer = null
        shouldDisplayUndoTransaction = false
        shouldDisplayUndoTransfer = false
    }

    @AssistedFactory
    interface Factory {
        fun create(
            walletId: String,
        ): WalletViewModel
    }
}

sealed interface WalletUiState {

    data object Loading : WalletUiState

    data class Success(
        val transactionFilter: TransactionFilter,
        val userWallet: UserWallet,
        val selectedTransaction: TransactionWithCategory?,
        val transactionsCategories: Map<Instant, List<TransactionWithCategory>>,
        val availableCategories: List<Category>,
        val expenses: BigDecimal,
        val income: BigDecimal,
        val graphData: Map<Int, BigDecimal>,
    ) : WalletUiState
}