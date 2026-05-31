package ru.resodostudios.cashsense.feature.transaction.csvimport.impl

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
import ru.resodostudios.cashsense.feature.transaction.csvimport.api.ImportNavKey

@HiltViewModel(assistedFactory = ImportViewModel.Factory::class)
class ImportViewModel @AssistedInject constructor(
    private val importTransactionsUseCase: ImportTransactionsUseCase,
    @Assisted private val key: ImportNavKey,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ImportUiState())
    val uiState = _uiState.asStateFlow()

    fun handleFileSelected(lines: List<String>) {
        _uiState.update {
            val columns = if (lines.isNotEmpty()) {
                csvReader {
                    dialect = CsvDialect(delimiter = it.config.columnSeparator.firstOrNull() ?: ';')
                }.readAll(lines.first()).firstOrNull() ?: emptyList<String>()
            } else {
                emptyList()
            }
            it.copy(
                lines = lines,
                columns = columns
            )
        }
    }

    fun updateConfig(config: CsvConfig) {
        _uiState.update {
            val columns = if (it.lines.isNotEmpty()) {
                csvReader {
                    dialect = CsvDialect(delimiter = config.columnSeparator.firstOrNull() ?: ';')
                }.readAll(it.lines.first()).firstOrNull() ?: emptyList<String>()
            } else {
                emptyList()
            }
            it.copy(
                config = config,
                columns = columns
            )
        }
    }

    fun importTransactions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = importTransactionsUseCase(
                walletId = key.walletId,
                lines = _uiState.value.lines,
                config = _uiState.value.config
            )
            _uiState.update {
                it.copy(
                    isLoading = false,
                    importFinished = true,
                    importedCount = result.getOrDefault(0)
                )
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(key: ImportNavKey): ImportViewModel
    }
}

data class ImportUiState(
    val lines: List<String> = emptyList(),
    val columns: List<String> = emptyList(),
    val config: CsvConfig = CsvConfig(),
    val isLoading: Boolean = false,
    val importFinished: Boolean = false,
    val importedCount: Int = 0,
)
