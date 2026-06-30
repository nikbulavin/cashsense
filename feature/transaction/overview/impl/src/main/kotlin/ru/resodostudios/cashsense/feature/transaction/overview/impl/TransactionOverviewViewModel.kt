package ru.resodostudios.cashsense.feature.transaction.overview.impl

import androidx.annotation.IntRange
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
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
import ru.resodostudios.cashsense.core.common.CsDispatchers.Default
import ru.resodostudios.cashsense.core.common.Dispatcher
import ru.resodostudios.cashsense.core.common.di.ApplicationScope
import ru.resodostudios.cashsense.core.data.repository.CurrencyConversionRepository
import ru.resodostudios.cashsense.core.data.repository.UserDataRepository
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.domain.GetExtendedUserWalletUseCase
import ru.resodostudios.cashsense.core.domain.GetExtendedUserWalletsUseCase
import ru.resodostudios.cashsense.core.model.Category
import ru.resodostudios.cashsense.core.model.DateType
import ru.resodostudios.cashsense.core.model.DateType.ALL
import ru.resodostudios.cashsense.core.model.DateType.MONTH
import ru.resodostudios.cashsense.core.model.DateType.WEEK
import ru.resodostudios.cashsense.core.model.DateType.YEAR
import ru.resodostudios.cashsense.core.model.FinanceType
import ru.resodostudios.cashsense.core.model.FinanceType.NOT_SET
import ru.resodostudios.cashsense.core.model.Transaction
import ru.resodostudios.cashsense.core.model.TransactionFilter
import ru.resodostudios.cashsense.core.model.Wallet
import ru.resodostudios.cashsense.core.ui.groupByDate
import ru.resodostudios.cashsense.core.ui.util.filterTransactions
import ru.resodostudios.cashsense.core.ui.util.formatAmount
import ru.resodostudios.cashsense.core.ui.util.getCurrentZonedDateTime
import ru.resodostudios.cashsense.core.ui.util.getGraphData
import ru.resodostudios.cashsense.core.ui.util.isInCurrentMonthAndYear
import ru.resodostudios.cashsense.feature.transaction.overview.api.TransactionOverviewNavKey
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Currency
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

@HiltViewModel(assistedFactory = TransactionOverviewViewModel.Factory::class)
internal class TransactionOverviewViewModel @AssistedInject constructor(
    private val currencyConversionRepository: CurrencyConversionRepository,
    private val walletsRepository: WalletsRepository,
    private val userDataRepository: UserDataRepository,
    private val getExtendedUserWallets: GetExtendedUserWalletsUseCase,
    private val getExtendedUserWallet: GetExtendedUserWalletUseCase,
    @Assisted private val key: TransactionOverviewNavKey,
    @Dispatcher(Default) private val defaultDispatcher: CoroutineDispatcher,
    @ApplicationScope private val appScope: CoroutineScope,
) : ViewModel() {

    private val walletId = key.walletId

    private val transactionFilterState = MutableStateFlow(
        TransactionFilter(
            selectedCategories = emptySet(),
            financeType = NOT_SET,
            dateType = ALL,
            selectedDate = getCurrentZonedDateTime().date,
        ),
    )

    private val selectedTransactionState = MutableStateFlow<Transaction?>(null)

    val financePanelUiState: StateFlow<FinancePanelUiState> = if (walletId == null) {
        combine(
            walletsRepository.getDistinctCurrencies(),
            userDataRepository.userData,
        ) { currencies, userData ->
            currencies to Currency.getInstance(userData.currency)
        }
            .flatMapLatest { (baseCurrencies, userCurrency) ->
                if (baseCurrencies.isEmpty()) {
                    flowOf(FinancePanelUiState.NotShown)
                } else {
                    combine(
                        currencyConversionRepository.getConvertedCurrencies(
                            baseCurrencies = baseCurrencies.toSet(),
                            targetCurrency = userCurrency,
                        ),
                        getExtendedUserWallets.invoke(),
                        transactionFilterState,
                    ) { exchangeRates, wallets, transactionFilter ->
                        val allTransactions = wallets.flatMap { wallet -> wallet.transactions }
                        val filterableTransactions =
                            allTransactions.filterTransactions(transactionFilter)
                        val filteredTransactions = filterableTransactions.transactions
                            .filter {
                                !it.ignored && if (transactionFilter.dateType == ALL) {
                                    it.timestamp.isInCurrentMonthAndYear()
                                } else true
                            }

                        val totalBalance = wallets.sumOf {
                            if (userCurrency == it.wallet.currency) return@sumOf it.currentBalance
                            exchangeRates[it.wallet.currency]
                                ?.times(it.currentBalance)
                                ?: return@combine FinancePanelUiState.NotShown
                        }

                        val isMultiCurrencyExpenses = !filteredTransactions
                            .asSequence()
                            .filter { it.amount.signum() < 0 }
                            .map { it.currency }
                            .distinct()
                            .all { it == userCurrency }
                        val isMultiCurrencyIncome = !filteredTransactions
                            .asSequence()
                            .filter { it.amount.signum() > 0 }
                            .map { it.currency }
                            .distinct()
                            .all { it == userCurrency }

                        val (expenses, income) = calculateExpensesAndIncome(
                            transactions = filteredTransactions,
                            userCurrency = userCurrency,
                            currencyExchangeRates = exchangeRates,
                        ) ?: return@combine FinancePanelUiState.NotShown

                        FinancePanelUiState.Shown(
                            transactionFilter = transactionFilter,
                            formattedIncome = income.formatAmount(
                                currency = userCurrency,
                                approximatelyPrefix = isMultiCurrencyIncome,
                            ),
                            formattedExpenses = expenses.abs().formatAmount(
                                currency = userCurrency,
                                approximatelyPrefix = isMultiCurrencyExpenses,
                            ),
                            graphData = filteredTransactions.getGraphData(
                                dateType = transactionFilter.dateType,
                                userCurrency = userCurrency,
                                currencyExchangeRates = exchangeRates,
                            ),
                            userCurrency = userCurrency,
                            availableCategories = filterableTransactions.availableCategories,
                            formattedTotalBalance = totalBalance.formatAmount(
                                currency = userCurrency,
                                approximatelyPrefix = !baseCurrencies.all { it == userCurrency },
                            ),
                            financialHealth = calculateFinancialHealth(
                                transactions = allTransactions,
                                userCurrency = userCurrency,
                                currencyExchangeRates = exchangeRates,
                            ) ?: return@combine FinancePanelUiState.NotShown,
                        )
                    }
                }
            }
    } else {
        combine(
            getExtendedUserWallet.invoke(walletId),
            transactionFilterState,
        ) { extendedUserWallet, transactionFilter ->
            val filterableTransactions = extendedUserWallet.transactions
                .filterTransactions(transactionFilter)
            val filteredTransactions = filterableTransactions.transactions
                .filter {
                    !it.ignored && if (transactionFilter.dateType == ALL) {
                        it.timestamp.isInCurrentMonthAndYear()
                    } else true
                }
            val (expenses, income) = filteredTransactions.partition { it.amount.signum() < 0 }

            val graphData = filteredTransactions.getGraphData(transactionFilter.dateType)
            val currency = extendedUserWallet.wallet.currency

            FinancePanelUiState.Shown(
                transactionFilter = transactionFilter,
                formattedIncome = income.sumOf { it.amount }.formatAmount(currency),
                formattedExpenses = expenses.sumOf { it.amount }.abs().formatAmount(currency),
                graphData = graphData,
                availableCategories = filterableTransactions.availableCategories,
                userCurrency = currency,
                formattedTotalBalance = extendedUserWallet.currentBalance.formatAmount(currency),
                financialHealth = FinancialHealth.NEUTRAL,
                wallet = extendedUserWallet.wallet,
                isPrimary = extendedUserWallet.isPrimary,
            )
        }
    }
        .catch { emit(FinancePanelUiState.NotShown) }
        .flowOn(defaultDispatcher)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = FinancePanelUiState.Loading,
        )

    val transactionOverviewUiState: StateFlow<TransactionOverviewUiState> = if (walletId == null) {
        combine(
            getExtendedUserWallets.invoke(),
            transactionFilterState,
            selectedTransactionState,
        ) { extendedUserWallets, transactionFilter, selectedTransaction ->
            val transactions = extendedUserWallets
                .asSequence()
                .flatMap { it.transactions }
                .sortedByDescending { it.timestamp }
                .toList()
                .filterTransactions(transactionFilter).transactions

            TransactionOverviewUiState.Success(
                selectedTransaction = selectedTransaction,
                groupedTransactions = transactions.groupByDate(),
                walletIdsAndTitles = extendedUserWallets.associate { it.wallet.id to it.wallet.title },
            )
        }
    } else {
        combine(
            getExtendedUserWallet.invoke(walletId),
            transactionFilterState,
            selectedTransactionState,
        ) { extendedUserWallet, transactionFilter, selectedTransaction ->
            val transactions = extendedUserWallet.transactions
                .filterTransactions(transactionFilter).transactions

            TransactionOverviewUiState.Success(
                selectedTransaction = selectedTransaction,
                groupedTransactions = transactions.groupByDate(),
                walletIdsAndTitles = mapOf(extendedUserWallet.wallet.id to extendedUserWallet.wallet.title),
            )
        }
    }
        .flowOn(defaultDispatcher)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = TransactionOverviewUiState.Loading,
        )

    fun updateSelectedTransaction(transaction: Transaction?) {
        selectedTransactionState.value = transaction
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

            WEEK -> {
                transactionFilterState.update {
                    it.copy(
                        selectedDate = it.selectedDate.plus(dateOffset, DateTimeUnit.WEEK),
                    )
                }
            }

            ALL -> {}
        }
    }

    fun deleteWallet(walletId: String) {
        appScope.launch {
            walletsRepository.deleteWallet(walletId)
        }
    }

    fun setPrimaryWalletId(id: String, isPrimary: Boolean) {
        viewModelScope.launch {
            userDataRepository.setPrimaryWallet(id, isPrimary)
        }
    }

    private fun calculateFinancialHealth(
        transactions: List<Transaction>,
        userCurrency: Currency,
        currencyExchangeRates: Map<Currency, BigDecimal>,
    ): FinancialHealth? {
        val monthlyTransactions = transactions
            .filter { !it.ignored && it.timestamp.isInCurrentMonthAndYear() }
        val (monthlyExpenses, monthlyIncome) = calculateExpensesAndIncome(
            transactions = monthlyTransactions,
            userCurrency = userCurrency,
            currencyExchangeRates = currencyExchangeRates,
        ) ?: return null
        if (monthlyExpenses == BigDecimal.ZERO) {
            return if (monthlyIncome > BigDecimal.ZERO) FinancialHealth.VERY_GOOD else FinancialHealth.NEUTRAL
        }
        val ratio = monthlyIncome.divide(monthlyExpenses, 2, RoundingMode.HALF_UP).toDouble()
        return when {
            ratio < 0.5 -> FinancialHealth.VERY_BAD
            ratio < 0.9 -> FinancialHealth.BAD
            ratio < 1.1 -> FinancialHealth.NEUTRAL
            ratio < 1.5 -> FinancialHealth.GOOD
            else -> FinancialHealth.VERY_GOOD
        }
    }

    private fun calculateExpensesAndIncome(
        transactions: List<Transaction>,
        userCurrency: Currency,
        currencyExchangeRates: Map<Currency, BigDecimal>,
    ): Pair<BigDecimal, BigDecimal>? {
        var expenses = BigDecimal.ZERO
        var income = BigDecimal.ZERO
        for (transaction in transactions) {
            val amount = transaction.amount
            val currency = transaction.currency

            val convertedAmount = if (userCurrency == currency) {
                amount
            } else {
                currencyExchangeRates[currency]?.times(amount) ?: return null
            }

            if (amount.signum() < 0) {
                expenses += convertedAmount
            } else {
                income += convertedAmount
            }
        }
        return expenses to income
    }

    @AssistedFactory
    interface Factory {
        fun create(key: TransactionOverviewNavKey): TransactionOverviewViewModel
    }
}

internal sealed interface FinancePanelUiState {

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
        val wallet: Wallet? = null,
        val isPrimary: Boolean = false,
    ) : FinancePanelUiState
}

internal sealed interface TransactionOverviewUiState {

    data object Loading : TransactionOverviewUiState

    data class Success(
        val selectedTransaction: Transaction?,
        val groupedTransactions: Map<Instant, List<Transaction>>,
        val walletIdsAndTitles: Map<String, String>,
    ) : TransactionOverviewUiState
}
