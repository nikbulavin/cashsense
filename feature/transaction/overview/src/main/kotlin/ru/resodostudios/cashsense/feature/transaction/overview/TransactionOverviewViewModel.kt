package ru.resodostudios.cashsense.feature.transaction.overview

import androidx.annotation.IntRange
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus
import ru.resodostudios.cashsense.core.data.repository.CurrencyConversionRepository
import ru.resodostudios.cashsense.core.data.repository.TransactionsRepository
import ru.resodostudios.cashsense.core.data.repository.UserDataRepository
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.domain.GetExtendedUserWalletsUseCase
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.DateType
import ru.resodostudios.cashsense.core.model.data.DateType.ALL
import ru.resodostudios.cashsense.core.model.data.DateType.MONTH
import ru.resodostudios.cashsense.core.model.data.DateType.WEEK
import ru.resodostudios.cashsense.core.model.data.DateType.YEAR
import ru.resodostudios.cashsense.core.model.data.FinanceType
import ru.resodostudios.cashsense.core.model.data.FinanceType.NOT_SET
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.model.data.TransactionFilter
import ru.resodostudios.cashsense.core.model.data.Transfer
import ru.resodostudios.cashsense.core.network.CsDispatchers.Default
import ru.resodostudios.cashsense.core.network.Dispatcher
import ru.resodostudios.cashsense.core.ui.groupByDate
import ru.resodostudios.cashsense.core.ui.util.applyTransactionFilter
import ru.resodostudios.cashsense.core.ui.util.formatAmount
import ru.resodostudios.cashsense.core.ui.util.getCurrentZonedDateTime
import ru.resodostudios.cashsense.core.ui.util.getGraphData
import ru.resodostudios.cashsense.core.ui.util.isInCurrentMonthAndYear
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Currency
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

@HiltViewModel
class TransactionOverviewViewModel @Inject constructor(
    private val currencyConversionRepository: CurrencyConversionRepository,
    private val transactionsRepository: TransactionsRepository,
    walletRepository: WalletsRepository,
    userDataRepository: UserDataRepository,
    getExtendedUserWallets: GetExtendedUserWalletsUseCase,
    @Dispatcher(Default) private val defaultDispatcher: CoroutineDispatcher,
) : ViewModel() {

    var shouldDisplayUndoTransaction by mutableStateOf(false)
    private var lastRemovedTransaction: Transaction? = null

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

    private val selectedTransactionState = MutableStateFlow<Transaction?>(null)

    val financePanelUiState: StateFlow<FinancePanelUiState> = combine(
        walletRepository.getDistinctCurrencies(),
        userDataRepository.userData,
    ) { currencies, userData ->
        val userCurrency = Currency.getInstance(userData.currency)
        currencies to userCurrency
    }
        .flatMapLatest { (baseCurrencies, userCurrency) ->
            if (baseCurrencies.isEmpty()) {
                flowOf(FinancePanelUiState.NotShown)
            } else {
                val shouldShowApproximately = !baseCurrencies.all { it == userCurrency }

                combine(
                    currencyConversionRepository.getConvertedCurrencies(
                        baseCurrencies = baseCurrencies.toSet(),
                        targetCurrency = userCurrency,
                    ),
                    getExtendedUserWallets.invoke(),
                    transactionFilterState,
                ) { exchangeRates, wallets, transactionFilter ->
                    val exchangeRateMap = exchangeRates
                        .associate { it.baseCurrency to it.exchangeRate }

                    val filterableTransactions = wallets
                        .flatMap { wallet -> wallet.transactions }
                        .applyTransactionFilter(transactionFilter)

                    val filteredTransactions = filterableTransactions.transactions
                        .filter {
                            !it.ignored && if (transactionFilter.dateType == ALL) {
                                it.timestamp.isInCurrentMonthAndYear()
                            } else true
                        }

                    val totalBalance = wallets.sumOf {
                        if (userCurrency == it.wallet.currency) return@sumOf it.currentBalance
                        val exchangeRate = exchangeRateMap[it.wallet.currency]
                            ?: return@combine FinancePanelUiState.NotShown

                        it.currentBalance * exchangeRate
                    }

                    val (expenses, income) = filteredTransactions
                        .fold(BigDecimal.ZERO to BigDecimal.ZERO) { (expenses, income), transaction ->
                            val amount = transaction.amount
                            val currency = transaction.currency

                            val convertedAmount = if (userCurrency == currency) {
                                amount
                            } else {
                                exchangeRateMap[currency]?.let { rate -> amount * rate }
                                    ?: return@combine FinancePanelUiState.NotShown
                            }

                            if (amount.signum() < 0) {
                                expenses + convertedAmount to income
                            } else {
                                expenses to income + convertedAmount
                            }
                        }

                    val graphData = filteredTransactions.getGraphData(transactionFilter.dateType)

                    FinancePanelUiState.Shown(
                        transactionFilter = transactionFilter,
                        formattedIncome = income.formatAmount(
                            currency = userCurrency,
                            withApproximately = shouldShowApproximately,
                        ),
                        formattedExpenses = expenses.abs().formatAmount(
                            currency = userCurrency,
                            withApproximately = shouldShowApproximately,
                        ),
                        graphData = graphData,
                        userCurrency = userCurrency,
                        availableCategories = filterableTransactions.availableCategories,
                        formattedTotalBalance = totalBalance.formatAmount(
                            currency = userCurrency,
                            withApproximately = shouldShowApproximately,
                        ),
                        financialHealth = calculateFinancialHealth(income, expenses.abs())
                    )
                }
                    .catch { FinancePanelUiState.NotShown }
            }
        }
        .flowOn(defaultDispatcher)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = FinancePanelUiState.Loading,
        )

    val transactionOverviewUiState: StateFlow<TransactionOverviewUiState> = combine(
        getExtendedUserWallets.invoke(),
        transactionFilterState,
        selectedTransactionState,
    ) { extendedUserWallets, transactionFilter, selectedTransaction ->
        val transactions = extendedUserWallets
            .flatMap { it.transactions }
            .sortedByDescending { it.timestamp }
            .applyTransactionFilter(transactionFilter).transactions

        TransactionOverviewUiState.Success(
            selectedTransaction = selectedTransaction,
            groupedTransactions = transactions.groupByDate(),
        )
    }
        .flowOn(defaultDispatcher)
        .catch { TransactionOverviewUiState.Loading }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = TransactionOverviewUiState.Loading,
        )

    fun updateSelectedTransaction(transaction: Transaction?) {
        selectedTransactionState.value = transaction
    }

    fun deleteTransaction() {
        viewModelScope.launch {
            selectedTransactionState.value?.let { selectedTransaction ->
                if (selectedTransaction.transferId != null) {
                    val depositTransaction = transactionsRepository.getTransfer(
                        selectedTransaction.transferId!!,
                        selectedTransaction.walletOwnerId,
                    ).first().depositTransaction
                    lastRemovedTransfer = Transfer(selectedTransaction, depositTransaction)
                    transactionsRepository.deleteTransfer(selectedTransaction.transferId!!)
                    shouldDisplayUndoTransfer = true
                } else {
                    lastRemovedTransaction = selectedTransactionState.value
                    transactionsRepository.deleteTransaction(selectedTransaction.id)
                    shouldDisplayUndoTransaction = true
                }
            }
            selectedTransactionState.value = null
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
            transactionFilterState.update { it.copy(selectedCategories = emptySet()) }
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

    fun updateSelectedDate(@IntRange(from = -1, to = 1) dateOffset: Int) {
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

    private fun calculateFinancialHealth(income: BigDecimal, expenses: BigDecimal): FinancialHealth {
        if (expenses == BigDecimal.ZERO) {
            return if (income > BigDecimal.ZERO) FinancialHealth.VERY_GOOD else FinancialHealth.NEUTRAL
        }
        val ratio = income.divide(expenses, 2, RoundingMode.HALF_UP).toDouble()
        return when {
            ratio < 0.5 -> FinancialHealth.VERY_BAD
            ratio < 0.9 -> FinancialHealth.BAD
            ratio < 1.1 -> FinancialHealth.NEUTRAL
            ratio < 1.5 -> FinancialHealth.GOOD
            else -> FinancialHealth.VERY_GOOD
        }
    }
}

sealed interface FinancePanelUiState {

    data object Loading : FinancePanelUiState

    data object NotShown : FinancePanelUiState

    data class Shown(
        val transactionFilter: TransactionFilter,
        val availableCategories: List<Category>,
        val userCurrency: Currency,
        val formattedExpenses: String,
        val formattedIncome: String,
        val graphData: Map<Int, BigDecimal>,
        val formattedTotalBalance: String,
        val financialHealth: FinancialHealth,
    ) : FinancePanelUiState
}

sealed interface TransactionOverviewUiState {

    data object Loading : TransactionOverviewUiState

    data class Success(
        val selectedTransaction: Transaction?,
        val groupedTransactions: Map<Instant, List<Transaction>>,
    ) : TransactionOverviewUiState
}