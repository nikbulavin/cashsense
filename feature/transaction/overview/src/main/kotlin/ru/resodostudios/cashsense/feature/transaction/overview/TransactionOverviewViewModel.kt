package ru.resodostudios.cashsense.feature.transaction.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
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
import ru.resodostudios.cashsense.core.model.data.TransactionFilter
import ru.resodostudios.cashsense.core.model.data.TransactionWithCategory
import ru.resodostudios.cashsense.core.network.CsDispatchers.Default
import ru.resodostudios.cashsense.core.network.Dispatcher
import ru.resodostudios.cashsense.core.ui.groupByDate
import ru.resodostudios.cashsense.core.ui.util.applyTransactionFilter
import ru.resodostudios.cashsense.core.ui.util.getCurrentZonedDateTime
import ru.resodostudios.cashsense.core.ui.util.getGraphData
import ru.resodostudios.cashsense.core.ui.util.isInCurrentMonthAndYear
import java.math.BigDecimal
import java.util.Currency
import javax.inject.Inject
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

    private val transactionFilterState = MutableStateFlow(
        TransactionFilter(
            selectedCategories = emptySet(),
            financeType = NOT_SET,
            dateType = ALL,
            selectedDate = getCurrentZonedDateTime().date,
        )
    )

    private val selectedTransactionState = MutableStateFlow<TransactionWithCategory?>(null)

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
                        .flatMap { wallet -> wallet.transactionsWithCategories }
                        .applyTransactionFilter(transactionFilter)

                    val filteredTransactions = filterableTransactions.transactionsCategories
                        .filter {
                            !it.transaction.ignored && if (transactionFilter.dateType == ALL) {
                                it.transaction.timestamp.isInCurrentMonthAndYear()
                            } else true
                        }

                    val totalBalance = wallets.sumOf {
                        if (userCurrency == it.userWallet.currency) return@sumOf it.userWallet.currentBalance
                        val exchangeRate = exchangeRateMap[it.userWallet.currency]
                            ?: return@combine FinancePanelUiState.NotShown

                        it.userWallet.currentBalance * exchangeRate
                    }

                    val (expenses, income) = filteredTransactions
                        .fold(BigDecimal.ZERO to BigDecimal.ZERO) { (expenses, income), transactionCategory ->
                            val transaction = transactionCategory.transaction
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
                        income = income,
                        expenses = expenses.abs(),
                        graphData = graphData,
                        userCurrency = userCurrency,
                        availableCategories = filterableTransactions.availableCategories,
                        totalBalance = totalBalance,
                        shouldShowApproximately = shouldShowApproximately,
                    )
                }
                    .catch { FinancePanelUiState.NotShown }
            }
        }
        .flowOn(defaultDispatcher)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FinancePanelUiState.Loading,
        )

    val transactionOverviewUiState: StateFlow<TransactionOverviewUiState> = combine(
        getExtendedUserWallets.invoke(),
        transactionFilterState,
        selectedTransactionState,
    ) { extendedUserWallets, transactionFilter, selectedTransaction ->
        val transactions = extendedUserWallets
            .flatMap { it.transactionsWithCategories }
            .sortedByDescending { it.transaction.timestamp }
            .applyTransactionFilter(transactionFilter)
            .transactionsCategories

        TransactionOverviewUiState.Success(
            selectedTransaction = selectedTransaction,
            transactionsCategories = transactions.groupByDate(),
        )
    }
        .flowOn(defaultDispatcher)
        .catch { TransactionOverviewUiState.Loading }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TransactionOverviewUiState.Loading,
        )

    fun updateSelectedTransaction(transaction: TransactionWithCategory?) {
        selectedTransactionState.value = transaction
    }

    fun deleteTransaction() {
        viewModelScope.launch {
            selectedTransactionState.value?.let { selectedTransaction ->
                if (selectedTransaction.transaction.transferId != null) {
                    transactionsRepository.deleteTransfer(selectedTransaction.transaction.transferId!!)
                } else {
                    transactionsRepository.deleteTransaction(selectedTransaction.transaction.id)
                }
            }
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
        val totalBalance: BigDecimal,
        val shouldShowApproximately: Boolean,
    ) : FinancePanelUiState
}

sealed interface TransactionOverviewUiState {

    data object Loading : TransactionOverviewUiState

    data class Success(
        val selectedTransaction: TransactionWithCategory?,
        val transactionsCategories: Map<Instant, List<TransactionWithCategory>>,
    ) : TransactionOverviewUiState
}