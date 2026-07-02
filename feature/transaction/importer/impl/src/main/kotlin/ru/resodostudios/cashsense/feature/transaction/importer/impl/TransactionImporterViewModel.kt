package ru.resodostudios.cashsense.feature.transaction.importer.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsoizo.kotlincsv.CsvDialect
import com.jsoizo.kotlincsv.csvReader
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.common.di.ApplicationScope
import ru.resodostudios.cashsense.core.data.repository.TransactionsRepository
import ru.resodostudios.cashsense.core.data.repository.UserDataRepository
import ru.resodostudios.cashsense.core.domain.GetMenuWalletsUseCase
import ru.resodostudios.cashsense.core.domain.ImportTransactionsUseCase
import ru.resodostudios.cashsense.core.model.CsvConfig
import ru.resodostudios.cashsense.core.model.MenuWallet
import ru.resodostudios.cashsense.core.model.Transaction
import ru.resodostudios.cashsense.feature.transaction.importer.api.TransactionImporterNavKey
import java.util.Currency

@HiltViewModel(assistedFactory = TransactionImporterViewModel.Factory::class)
internal class TransactionImporterViewModel @AssistedInject constructor(
    private val importTransactionsUseCase: ImportTransactionsUseCase,
    private val transactionsRepository: TransactionsRepository,
    private val userDataRepository: UserDataRepository,
    private val getMenuWalletsUseCase: GetMenuWalletsUseCase,
    @Assisted private val key: TransactionImporterNavKey,
    @ApplicationScope private val appScope: CoroutineScope,
) : ViewModel() {

    private val _transactionImporterUiState = MutableStateFlow(TransactionImporterUiState())
    val transactionImporterUiState = _transactionImporterUiState.asStateFlow()

    private var parseJob: Job? = null

    init {
        viewModelScope.launch {
            val userData = userDataRepository.userData.first()
            val initialWalletId = key.walletId ?: userData.primaryWalletId

            if (initialWalletId.isNotEmpty()) {
                _transactionImporterUiState.update { it.copy(walletId = initialWalletId) }
            }

            getMenuWalletsUseCase().collect { availableWallets ->
                val currentWalletId = _transactionImporterUiState.value.walletId
                val currency = availableWallets.find { it.id == currentWalletId }?.currency

                _transactionImporterUiState.update {
                    it.copy(
                        availableWallets = availableWallets,
                        walletIdsAndTitles = availableWallets.associate { wallet -> wallet.id to wallet.title },
                        currency = currency,
                    )
                }
            }
        }
    }

    fun handleFileSelected(
        fileName: String,
        lines: List<String>,
    ) {
        _transactionImporterUiState.update {
            it.copy(
                fileName = fileName,
                lines = lines,
                columns = extractColumns(lines, it.config.columnSeparator),
            )
        }
        parseTransactions()
    }

    fun updateConfig(config: CsvConfig) {
        _transactionImporterUiState.update {
            it.copy(
                config = config,
                columns = extractColumns(it.lines, config.columnSeparator),
            )
        }
        parseTransactions()
    }

    fun updateWallet(wallet: MenuWallet) {
        _transactionImporterUiState.update {
            it.copy(
                walletId = wallet.id,
                currency = wallet.currency,
            )
        }
        parseTransactions()
    }

    fun toggleTransactionSelection(id: String) {
        _transactionImporterUiState.update {
            val selectedTransactions = it.selectedTransactions.toMutableSet()
            if (selectedTransactions.contains(id)) {
                selectedTransactions.remove(id)
            } else {
                selectedTransactions.add(id)
            }
            it.copy(selectedTransactions = selectedTransactions)
        }
    }

    fun updateParsedTransaction(transaction: Transaction) {
        _transactionImporterUiState.update { state ->
            state.copy(
                parsedTransactions = state.parsedTransactions.map {
                    if (it.id == transaction.id) transaction else it
                },
            )
        }
    }

    fun importTransactions() {
        appScope.launch {
            val transactions = _transactionImporterUiState.value.parsedTransactions
                .filter { it.id in _transactionImporterUiState.value.selectedTransactions }
            transactionsRepository.upsertTransactions(transactions)
        }
    }

    private fun extractColumns(lines: List<String>, separator: String): List<String> {
        if (lines.isEmpty() || separator.isEmpty()) return emptyList()

        return runCatching {
            csvReader {
                dialect = CsvDialect(delimiter = separator.first())
            }.readAll(lines.first()).firstOrNull()
        }.getOrNull() ?: emptyList()
    }

    private fun parseTransactions() {
        parseJob?.cancel()
        val currentState = _transactionImporterUiState.value
        if (currentState.lines.isNotEmpty() && currentState.walletId.isNotEmpty()) {
            parseJob = viewModelScope.launch {
                importTransactionsUseCase(
                    walletId = currentState.walletId,
                    lines = currentState.lines,
                    config = currentState.config,
                ).fold(
                    onSuccess = { parsedTransactions ->
                        _transactionImporterUiState.update { state ->
                            state.copy(
                                parsedTransactions = parsedTransactions,
                                selectedTransactions = parsedTransactions
                                    .asSequence()
                                    .map { it.id }
                                    .toSet(),
                            )
                        }
                    },
                ) {
                    _transactionImporterUiState.update { state ->
                        state.copy(
                            parsedTransactions = emptyList(),
                            selectedTransactions = emptySet(),
                        )
                    }
                }
            }
        } else {
            _transactionImporterUiState.update { state ->
                state.copy(
                    parsedTransactions = emptyList(),
                    selectedTransactions = emptySet(),
                )
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(key: TransactionImporterNavKey): TransactionImporterViewModel
    }
}

internal data class TransactionImporterUiState(
    val walletId: String = "",
    val fileName: String = "",
    val lines: List<String> = emptyList(),
    val columns: List<String> = emptyList(),
    val config: CsvConfig = CsvConfig(),
    val parsedTransactions: List<Transaction> = emptyList(),
    val selectedTransactions: Set<String> = emptySet(),
    val availableWallets: List<MenuWallet> = emptyList(),
    val walletIdsAndTitles: Map<String, String> = emptyMap(),
    val currency: Currency? = null,
    val isLoading: Boolean = false,
)
