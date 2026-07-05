package ru.resodostudios.cashsense.feature.transaction.overview.impl

import androidx.annotation.IntRange
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.Lazy
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
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
import ru.resodostudios.cashsense.core.model.ExtendedUserWallet
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
    getExtendedUserWallets: Lazy<GetExtendedUserWalletsUseCase>,
    getExtendedUserWallet: Lazy<GetExtendedUserWalletUseCase>,
    @Assisted private val key: TransactionOverviewNavKey,
    @Dispatcher(Default) private val defaultDispatcher: CoroutineDispatcher,
    @ApplicationScope private val appScope: CoroutineScope,
) : ViewModel() {

    private val walletId = key.walletId
    private val isOverviewMode = walletId == null

    private val walletsFlow: Flow<List<ExtendedUserWallet>> = walletId?.let { id ->
        getExtendedUserWallet.get().invoke(id).map { listOf(it) }
    } ?: getExtendedUserWallets.get().invoke()

    private val transactionFilterState = MutableStateFlow(
        TransactionFilter(
            selectedCategories = emptySet(),
            financeType = NOT_SET,
            dateType = ALL,
            selectedDate = getCurrentZonedDateTime().date,
        ),
    )

    private val selectedTransactionState = MutableStateFlow<Transaction?>(null)

    private val targetCurrencyFlow: Flow<Currency> = combine(
        userDataRepository.userData,
        walletsFlow,
    ) { userData, wallets ->
        if (isOverviewMode) {
            Currency.getInstance(userData.currency)
        } else {
            wallets.firstOrNull()?.wallet?.currency ?: Currency.getInstance(userData.currency)
        }
    }.distinctUntilChanged()

    private val baseCurrenciesFlow = walletsRepository.getDistinctCurrencies()

    private val exchangeRatesFlow: Flow<Map<Currency, BigDecimal>> = combine(
        baseCurrenciesFlow,
        targetCurrencyFlow,
    ) { baseCurrencies, targetCurrency ->
        baseCurrencies to targetCurrency
    }.flatMapLatest { (baseCurrencies, targetCurrency) ->
        if (baseCurrencies.isEmpty() && isOverviewMode) {
            flowOf(emptyMap())
        } else {
            currencyConversionRepository.getConvertedCurrencies(
                baseCurrencies = baseCurrencies.toSet(),
                targetCurrency = targetCurrency,
            )
        }
    }

    private val totalBalanceFlow: Flow<BigDecimal?> = combine(
        walletsFlow,
        targetCurrencyFlow,
        exchangeRatesFlow,
    ) { wallets, targetCurrency, exchangeRates ->
        calculateTotalBalance(wallets, targetCurrency, exchangeRates)
    }.flowOn(defaultDispatcher)

    private val panelDataFlow: Flow<PanelData> = combine(
        walletsFlow,
        targetCurrencyFlow,
        exchangeRatesFlow,
        totalBalanceFlow,
        baseCurrenciesFlow,
    ) { wallets, targetCurrency, exchangeRates, totalBalance, baseCurrencies ->
        PanelData(wallets, targetCurrency, exchangeRates, totalBalance, baseCurrencies)
    }.flowOn(defaultDispatcher)

    val financePanelUiState: StateFlow<FinancePanelUiState> = combine(
        panelDataFlow,
        transactionFilterState,
    ) { data, filter ->
        if (data.baseCurrencies.isEmpty() && isOverviewMode ||
            data.totalBalance == null
        ) {
            return@combine FinancePanelUiState.Error(data.targetCurrency)
        }

        val allTransactions = data.wallets.flatMap { it.transactions }
        val filterableTransactions = allTransactions.filterTransactions(filter)
        val filteredTransactions = filterableTransactions.transactions.filter {
            !it.ignored && if (filter.dateType == ALL) it.timestamp.isInCurrentMonthAndYear() else true
        }

        val metrics = calculatePeriodMetrics(
            filteredTransactions = filteredTransactions,
            allTransactions = allTransactions,
            targetCurrency = data.targetCurrency,
            exchangeRates = data.exchangeRates,
            isOverviewMode = isOverviewMode,
        ) ?: return@combine FinancePanelUiState.Error(data.targetCurrency)

        val singleWallet = if (isOverviewMode) null else data.wallets.singleOrNull()

        FinancePanelUiState.Success(
            transactionFilter = filter,
            formattedIncome = metrics.income.formatAmount(
                currency = data.targetCurrency,
                approximatelyPrefix = metrics.isMultiCurrencyIncome && isOverviewMode,
            ),
            formattedExpenses = metrics.expenses.abs().formatAmount(
                currency = data.targetCurrency,
                approximatelyPrefix = metrics.isMultiCurrencyExpenses && isOverviewMode,
            ),
            graphData = filteredTransactions.getGraphData(
                dateType = filter.dateType,
                userCurrency = data.targetCurrency,
                currencyExchangeRates = data.exchangeRates,
            ),
            userCurrency = data.targetCurrency,
            availableCategories = filterableTransactions.availableCategories,
            formattedTotalBalance = data.totalBalance.formatAmount(
                currency = data.targetCurrency,
                approximatelyPrefix = !data.baseCurrencies.all { it == data.targetCurrency } && isOverviewMode,
            ),
            financialHealth = metrics.financialHealth,
            wallet = singleWallet?.wallet,
            isPrimary = singleWallet?.isPrimary ?: false,
        )
    }
        .flowOn(defaultDispatcher)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = FinancePanelUiState.Loading,
        )

    val transactionOverviewUiState: StateFlow<TransactionOverviewUiState> = combine(
        walletsFlow,
        transactionFilterState,
        selectedTransactionState,
    ) { wallets, transactionFilter, selectedTransaction ->
        val transactions = wallets
            .asSequence()
            .flatMap { it.transactions }
            .sortedByDescending { it.timestamp }
            .toList()
            .filterTransactions(transactionFilter).transactions

        TransactionOverviewUiState.Success(
            selectedTransaction = selectedTransaction,
            groupedTransactions = transactions.groupByDate(),
            walletIdsAndTitles = wallets.associate { it.wallet.id to it.wallet.title },
        )
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
            MONTH -> transactionFilterState.update {
                it.copy(
                    selectedDate = it.selectedDate.plus(
                        dateOffset,
                        DateTimeUnit.MONTH,
                    )
                )
            }

            YEAR -> transactionFilterState.update {
                it.copy(
                    selectedDate = it.selectedDate.plus(
                        dateOffset,
                        DateTimeUnit.YEAR,
                    )
                )
            }

            WEEK -> transactionFilterState.update {
                it.copy(
                    selectedDate = it.selectedDate.plus(
                        dateOffset,
                        DateTimeUnit.WEEK,
                    )
                )
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

    @AssistedFactory
    interface Factory {
        fun create(key: TransactionOverviewNavKey): TransactionOverviewViewModel
    }
}

private data class PanelData(
    val wallets: List<ExtendedUserWallet>,
    val targetCurrency: Currency,
    val exchangeRates: Map<Currency, BigDecimal>,
    val totalBalance: BigDecimal?,
    val baseCurrencies: List<Currency>,
)

private data class PeriodMetricsResult(
    val expenses: BigDecimal,
    val income: BigDecimal,
    val isMultiCurrencyExpenses: Boolean,
    val isMultiCurrencyIncome: Boolean,
    val financialHealth: FinancialHealth,
)

private fun calculateTotalBalance(
    wallets: List<ExtendedUserWallet>,
    targetCurrency: Currency,
    exchangeRates: Map<Currency, BigDecimal>,
): BigDecimal? {
    var total = BigDecimal.ZERO
    for (userWallet in wallets) {
        val balance = userWallet.currentBalance
        val currency = userWallet.wallet.currency

        if (targetCurrency == currency) {
            total += balance
        } else {
            val rate = exchangeRates[currency] ?: return null
            total += rate * balance
        }
    }
    return total
}

private fun calculatePeriodMetrics(
    filteredTransactions: List<Transaction>,
    allTransactions: List<Transaction>,
    targetCurrency: Currency,
    exchangeRates: Map<Currency, BigDecimal>,
    isOverviewMode: Boolean,
): PeriodMetricsResult? {
    var expenses = BigDecimal.ZERO
    var income = BigDecimal.ZERO
    val expenseCurrencies = mutableSetOf<Currency>()
    val incomeCurrencies = mutableSetOf<Currency>()

    for (transaction in filteredTransactions) {
        val amount = transaction.amount
        val currency = transaction.currency

        val convertedAmount = if (targetCurrency == currency) {
            amount
        } else {
            exchangeRates[currency]?.times(amount) ?: return null
        }

        if (amount.signum() < 0) {
            expenses += convertedAmount
            expenseCurrencies.add(currency)
        } else {
            income += convertedAmount
            incomeCurrencies.add(currency)
        }
    }

    val health = if (isOverviewMode) {
        calculateFinancialHealth(allTransactions, targetCurrency, exchangeRates) ?: return null
    } else {
        FinancialHealth.NEUTRAL
    }

    return PeriodMetricsResult(
        expenses = expenses,
        income = income,
        isMultiCurrencyExpenses = !expenseCurrencies.all { it == targetCurrency },
        isMultiCurrencyIncome = !incomeCurrencies.all { it == targetCurrency },
        financialHealth = health,
    )
}

private fun calculateFinancialHealth(
    transactions: List<Transaction>,
    userCurrency: Currency,
    currencyExchangeRates: Map<Currency, BigDecimal>,
): FinancialHealth? {
    val monthlyTransactions = transactions
        .filter { !it.ignored && it.timestamp.isInCurrentMonthAndYear() }
    var expenses = BigDecimal.ZERO
    var income = BigDecimal.ZERO

    for (transaction in monthlyTransactions) {
        val convertedAmount = if (userCurrency == transaction.currency) {
            transaction.amount
        } else {
            currencyExchangeRates[transaction.currency]?.times(transaction.amount) ?: return null
        }
        if (transaction.amount.signum() < 0) {
            expenses += convertedAmount
        } else {
            income += convertedAmount
        }
    }

    if (expenses.signum() == 0) {
        return if (income > BigDecimal.ZERO) FinancialHealth.VERY_GOOD else FinancialHealth.NEUTRAL
    }
    val ratio = income.divide(expenses.abs(), 2, RoundingMode.HALF_UP).toDouble()
    return when {
        ratio < 0.5 -> FinancialHealth.VERY_BAD
        ratio < 0.9 -> FinancialHealth.BAD
        ratio < 1.1 -> FinancialHealth.NEUTRAL
        ratio < 1.5 -> FinancialHealth.GOOD
        else -> FinancialHealth.VERY_GOOD
    }
}

internal sealed interface FinancePanelUiState {

    data object Loading : FinancePanelUiState

    data class Error(val currency: Currency) : FinancePanelUiState

    data class Success(
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
