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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
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
    getExtendedUserWallets: GetExtendedUserWalletsUseCase,
    getExtendedUserWallet: GetExtendedUserWalletUseCase,
    @Assisted private val key: TransactionOverviewNavKey,
    @Dispatcher(Default) private val defaultDispatcher: CoroutineDispatcher,
    @ApplicationScope private val appScope: CoroutineScope,
) : ViewModel() {

    private val walletId = key.walletId
    private val walletsFlow: Flow<List<ExtendedUserWallet>> = if (walletId == null) {
        getExtendedUserWallets()
    } else {
        getExtendedUserWallet(walletId).map { listOf(it) }
    }

    private val transactionFilterState = MutableStateFlow(
        TransactionFilter(
            selectedCategories = emptySet(),
            financeType = NOT_SET,
            dateType = ALL,
            selectedDate = getCurrentZonedDateTime().date,
        ),
    )

    private val selectedTransactionState = MutableStateFlow<Transaction?>(null)

    val financePanelUiState: StateFlow<FinancePanelUiState> = combine(
        walletsRepository.getDistinctCurrencies(),
        userDataRepository.userData,
        walletsFlow
    ) { baseCurrencies, userData, wallets ->
        Triple(baseCurrencies, Currency.getInstance(userData.currency), wallets)
    }
        .flatMapLatest { (baseCurrencies, appCurrency, wallets) ->
            val targetCurrency = if (walletId != null) {
                wallets.firstOrNull()?.wallet?.currency ?: appCurrency
            } else {
                appCurrency
            }

            if (baseCurrencies.isEmpty() && walletId == null) {
                return@flatMapLatest flowOf(FinancePanelUiState.NotShown)
            }

            combine(
                currencyConversionRepository.getConvertedCurrencies(
                    baseCurrencies = baseCurrencies.toSet(),
                    targetCurrency = targetCurrency,
                ),
                transactionFilterState,
            ) { exchangeRates, transactionFilter ->
                val allTransactions = wallets.flatMap { it.transactions }
                val filterableTransactions = allTransactions.filterTransactions(transactionFilter)
                val filteredTransactions = filterableTransactions.transactions.filter {
                    !it.ignored && if (transactionFilter.dateType == ALL) {
                        it.timestamp.isInCurrentMonthAndYear()
                    } else true
                }

                val singleWallet = wallets.singleOrNull()

                val totalBalance = wallets.sumOf {
                    if (targetCurrency == it.wallet.currency) return@sumOf it.currentBalance
                    exchangeRates[it.wallet.currency]?.times(it.currentBalance)
                        ?: return@combine FinancePanelUiState.NotShown
                }

                val isMultiCurrencyExpenses = !filteredTransactions.asSequence()
                    .filter { it.amount.signum() < 0 }
                    .map { it.currency }
                    .distinct()
                    .all { it == targetCurrency }

                val isMultiCurrencyIncome = !filteredTransactions.asSequence()
                    .filter { it.amount.signum() > 0 }
                    .map { it.currency }
                    .distinct()
                    .all { it == targetCurrency }

                val (expenses, income) = calculateExpensesAndIncome(
                    transactions = filteredTransactions,
                    userCurrency = targetCurrency,
                    currencyExchangeRates = exchangeRates,
                ) ?: return@combine FinancePanelUiState.NotShown

                FinancePanelUiState.Shown(
                    transactionFilter = transactionFilter,
                    formattedIncome = income.formatAmount(
                        currency = targetCurrency,
                        approximatelyPrefix = isMultiCurrencyIncome && walletId == null,
                    ),
                    formattedExpenses = expenses.abs().formatAmount(
                        currency = targetCurrency,
                        approximatelyPrefix = isMultiCurrencyExpenses && walletId == null,
                    ),
                    graphData = filteredTransactions.getGraphData(
                        dateType = transactionFilter.dateType,
                        userCurrency = targetCurrency,
                        currencyExchangeRates = exchangeRates,
                    ),
                    userCurrency = targetCurrency,
                    availableCategories = filterableTransactions.availableCategories,
                    formattedTotalBalance = totalBalance.formatAmount(
                        currency = targetCurrency,
                        approximatelyPrefix = !baseCurrencies.all { it == targetCurrency } && walletId == null,
                    ),
                    financialHealth = if (walletId == null) {
                        calculateFinancialHealth(
                            transactions = allTransactions,
                            userCurrency = targetCurrency,
                            currencyExchangeRates = exchangeRates,
                        ) ?: return@combine FinancePanelUiState.NotShown
                    } else {
                        FinancialHealth.NEUTRAL
                    },
                    wallet = singleWallet?.wallet,
                    isPrimary = singleWallet?.isPrimary ?: false,
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
