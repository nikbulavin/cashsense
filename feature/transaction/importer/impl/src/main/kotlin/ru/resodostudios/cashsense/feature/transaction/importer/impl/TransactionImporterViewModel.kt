package ru.resodostudios.cashsense.feature.transaction.importer.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsoizo.kotlincsv.CsvDialect
import com.jsoizo.kotlincsv.csvReader
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.domain.ImportTransactionsUseCase
import ru.resodostudios.cashsense.core.model.data.CsvConfig
import ru.resodostudios.cashsense.feature.transaction.importer.api.TransactionImporterNavKey

@HiltViewModel(assistedFactory = TransactionImporterViewModel.Factory::class)
internal class TransactionImporterViewModel @AssistedInject constructor(
    private val importTransactionsUseCase: ImportTransactionsUseCase,
    @Assisted private val key: TransactionImporterNavKey,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionImporterUiState())
    val uiState = _uiState.asStateFlow()

    fun handleFileSelected(lines: List<String>) {
        _uiState.update {
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
                lines = lines,
                columns = columns,
            )
        }
    }

    fun updateConfig(config: CsvConfig) {
        _uiState.update {
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
    }

    fun importTransactions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = importTransactionsUseCase(
                walletId = key.walletId,
                lines = _uiState.value.lines,
                config = _uiState.value.config,
            )
            _uiState.update {
                it.copy(
                    isLoading = false,
                    importFinished = true,
                    importedCount = result.getOrDefault(0),
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
    val lines: List<String> = emptyList(),
    val columns: List<String> = emptyList(),
    val config: CsvConfig = CsvConfig(),
    val isLoading: Boolean = false,
    val importFinished: Boolean = false,
    val importedCount: Int = 0,
)
