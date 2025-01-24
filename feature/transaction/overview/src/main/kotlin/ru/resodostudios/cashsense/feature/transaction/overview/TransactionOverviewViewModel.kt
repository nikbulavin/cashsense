package ru.resodostudios.cashsense.feature.transaction.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.data.repository.CurrencyConversionRepository
import ru.resodostudios.cashsense.core.data.repository.TransactionsRepository
import ru.resodostudios.cashsense.core.data.repository.UserDataRepository
import ru.resodostudios.cashsense.core.domain.GetExtendedUserWalletsUseCase
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.DateType
import ru.resodostudios.cashsense.core.model.data.DateType.ALL
import ru.resodostudios.cashsense.core.model.data.DateType.MONTH
import ru.resodostudios.cashsense.core.model.data.DateType.WEEK
import ru.resodostudios.cashsense.core.model.data.DateType.YEAR
import ru.resodostudios.cashsense.core.model.data.FilterableTransactions
import ru.resodostudios.cashsense.core.model.data.FinanceType
import ru.resodostudios.cashsense.core.model.data.FinanceType.EXPENSES
import ru.resodostudios.cashsense.core.model.data.FinanceType.INCOME
import ru.resodostudios.cashsense.core.model.data.FinanceType.NOT_SET
import ru.resodostudios.cashsense.core.model.data.TransactionFilter
import ru.resodostudios.cashsense.core.model.data.TransactionWithCategory
import ru.resodostudios.cashsense.core.ui.util.getCurrentMonth
import ru.resodostudios.cashsense.core.ui.util.getCurrentYear
import ru.resodostudios.cashsense.core.ui.util.getCurrentZonedDateTime
import ru.resodostudios.cashsense.core.ui.util.getZonedDateTime
import ru.resodostudios.cashsense.core.ui.util.isInCurrentMonthAndYear
import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.Currency
import javax.inject.Inject

@HiltViewModel
class TransactionOverviewViewModel @Inject constructor(
    private val transactionsRepository: TransactionsRepository,
    private val currencyConversionRepository: CurrencyConversionRepository,
    userDataRepository: UserDataRepository,
    getExtendedUserWallets: GetExtendedUserWalletsUseCase,
) : ViewModel() {

    private val transactionFilterState = MutableStateFlow(
        TransactionFilter(
            selectedCategories = emptySet(),
            financeType = NOT_SET,
            dateType = ALL,
            selectedYearMonth = YearMonth.of(getCurrentYear(), getCurrentMonth()),
        )
    )

    private val selectedTransactionIdState = MutableStateFlow<String?>(null)

    val transactionOverviewUiState: StateFlow<TransactionOverviewUiState> = combine(
        getExtendedUserWallets.invoke(),
        transactionFilterState,
        selectedTransactionIdState,
    ) { extendedUserWallets, transactionFilter, selectedTransactionId ->
        val transactions = extendedUserWallets
            .flatMap { it.transactionsWithCategories }
            .sortedByDescending { it.transaction.timestamp }
            .applyTransactionFilter(transactionFilter)
            .transactions

        TransactionOverviewUiState.Success(
            selectedTransactionCategory = selectedTransactionId?.let { id ->
                transactions.find { it.transaction.id == id }
            },
            transactionsCategories = transactions,
        )
    }
        .catch { TransactionOverviewUiState.Loading }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TransactionOverviewUiState.Loading,
        )

    fun updateTransactionId(id: String) {
        selectedTransactionIdState.value = id
    }

    fun deleteTransaction() {
        viewModelScope.launch {
            selectedTransactionIdState.value?.let { id ->
                val transactionCategory = transactionsRepository.getTransactionWithCategory(id)
                    .first()
                if (transactionCategory.transaction.transferId != null) {
                    transactionsRepository.deleteTransfer(transactionCategory.transaction.transferId!!)
                } else {
                    transactionsRepository.deleteTransaction(id)
                }
            }
        }
    }

    fun updateTransactionIgnoring(ignored: Boolean) {
        viewModelScope.launch {
            selectedTransactionIdState.value?.let { id ->
                val transactionCategory = transactionsRepository.getTransactionWithCategory(id)
                    .first()
                val transaction = transactionCategory.transaction.copy(ignored = ignored)
                transactionsRepository.upsertTransaction(transaction)
            }
        }
    }

    fun addToSelectedCategories(category: Category) {
        transactionFilterState.update {
            it.copy(
                selectedCategories = it.selectedCategories.plus(category),
            )
        }
    }

    fun removeFromSelectedCategories(category: Category) {
        transactionFilterState.update {
            it.copy(
                selectedCategories = it.selectedCategories.minus(category),
            )
        }
    }

    fun updateFinanceType(financeType: FinanceType) {
        transactionFilterState.update {
            it.copy(financeType = financeType)
        }
        if (financeType == NOT_SET) {
            transactionFilterState.update { it.copy(selectedCategories = emptySet()) }
        }
    }

    fun updateDateType(dateType: DateType) {
        transactionFilterState.update {
            it.copy(
                dateType = dateType,
                selectedYearMonth = YearMonth.of(getCurrentYear(), getCurrentMonth()),
            )
        }
    }

    fun updateSelectedDate(increment: Int) {
        when (transactionFilterState.value.dateType) {
            MONTH -> {
                transactionFilterState.update {
                    it.copy(
                        selectedYearMonth = it.selectedYearMonth.plusMonths(increment.toLong()),
                    )
                }
            }

            YEAR -> {
                transactionFilterState.update {
                    it.copy(
                        selectedYearMonth = it.selectedYearMonth.plusYears(increment.toLong()),
                    )
                }
            }

            ALL, WEEK -> {}
        }
    }
}

sealed interface TransactionOverviewUiState {

    data object Loading : TransactionOverviewUiState

    data class Success(
        val selectedTransactionCategory: TransactionWithCategory?,
        val transactionsCategories: List<TransactionWithCategory>,
    ) : TransactionOverviewUiState
}

sealed interface FinancePanelUiState {

    data object Loading : FinancePanelUiState

    data object NotShown : FinancePanelUiState

    data class Shown(
        val transactionFilter: TransactionFilter,
        val availableCategories: List<Category>,
        val userCurrency: Currency,
        val expenses: BigDecimal,
        val income: BigDecimal,
        val graphData: Map<Int, BigDecimal>,
    ) : FinancePanelUiState
}

private fun List<TransactionWithCategory>.applyTransactionFilter(transactionFilter: TransactionFilter): FilterableTransactions {
    val filteredTransactions = filter { transactionCategory ->
        val transaction = transactionCategory.transaction

        val financeTypeMatch = when (transactionFilter.financeType) {
            NOT_SET -> true
            EXPENSES -> transaction.amount < ZERO
            INCOME -> transaction.amount > ZERO
        }

        val dateTypeMatch = when (transactionFilter.dateType) {
            ALL -> true
            WEEK -> {
                val weekOfTransaction = transaction.timestamp.getZonedDateTime()
                    .get(WeekFields.ISO.weekOfWeekBasedYear())
                weekOfTransaction == getCurrentZonedDateTime().get(WeekFields.ISO.weekOfWeekBasedYear())
            }

            MONTH -> {
                val transactionZonedDateTime = transaction.timestamp.getZonedDateTime()
                transactionZonedDateTime.year == transactionFilter.selectedYearMonth.year &&
                        transactionZonedDateTime.monthValue == transactionFilter.selectedYearMonth.monthValue
            }

            YEAR -> transaction.timestamp.getZonedDateTime().year == transactionFilter.selectedYearMonth.year
        }

        financeTypeMatch && dateTypeMatch
    }

    val availableCategories = filteredTransactions
        .mapNotNull { it.category }
        .distinct()

    val filteredByCategories = if (transactionFilter.selectedCategories.isNotEmpty()) {
        filteredTransactions
            .filter { transactionFilter.selectedCategories.contains(it.category) }
    } else filteredTransactions

    return FilterableTransactions(
        transactions = filteredByCategories,
        availableCategories = availableCategories,
    )
}