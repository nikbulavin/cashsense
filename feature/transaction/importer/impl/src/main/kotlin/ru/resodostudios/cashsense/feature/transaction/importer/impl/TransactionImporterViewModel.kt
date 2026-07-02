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
            val walletId = key.walletId ?: userData.primaryWalletId
            if (walletId.isNotEmpty()) {
                _transactionImporterUiState.update {
                    it.copy(walletId = walletId)
                }
            }
        }
        viewModelScope.launch {
            getMenuWalletsUseCase().collect { availableWallets ->
                val currency = availableWallets
                    .find { it.id == _transactionImporterUiState.value.walletId }
                    ?.currency
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
            val columns = if (lines.isNotEmpty() && it.config.columnSeparator.isNotEmpty()) {
                runCatching {
                    csvReader {
                        dialect = CsvDialect(delimiter = it.config.columnSeparator.first())
                    }.readAll(lines.first()).firstOrNull()
                }.getOrNull() ?: emptyList()
            } else {
                emptyList()
            }
            it.copy(
                fileName = fileName,
                lines = lines,
                columns = columns,
            )
        }
        parseTransactions()
    }

    fun updateConfig(config: CsvConfig) {
        _transactionImporterUiState.update {
            val columns = if (it.lines.isNotEmpty() && config.columnSeparator.isNotEmpty()) {
                runCatching {
                    csvReader {
                        dialect = CsvDialect(delimiter = config.columnSeparator.first())
                    }.readAll(it.lines.first()).firstOrNull()
                }.getOrNull() ?: emptyList()
            } else {
                it.columns
            }
            it.copy(
                config = config,
                columns = columns,
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

    fun importTransactions() {
        appScope.launch {
            _transactionImporterUiState.value.parsedTransactions
                .filter { it.id in _transactionImporterUiState.value.selectedTransactions }
                .forEach { transactionsRepository.upsertTransaction(it) }
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
