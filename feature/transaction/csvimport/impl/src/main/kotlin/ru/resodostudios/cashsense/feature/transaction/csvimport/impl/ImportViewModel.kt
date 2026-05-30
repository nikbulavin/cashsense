package ru.resodostudios.cashsense.feature.transaction.csvimport.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.common.CsvParser
import ru.resodostudios.cashsense.core.domain.ImportTransactionsUseCase
import ru.resodostudios.cashsense.core.model.data.CsvConfig
import ru.resodostudios.cashsense.feature.transaction.csvimport.api.ImportNavKey

@HiltViewModel(assistedFactory = ImportViewModel.Factory::class)
class ImportViewModel @AssistedInject constructor(
    private val importTransactionsUseCase: ImportTransactionsUseCase,
    private val csvParser: CsvParser,
    @Assisted private val key: ImportNavKey,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ImportUiState())
    val uiState = _uiState.asStateFlow()

    fun handleFileSelected(lines: List<String>) {
        _uiState.update {
            it.copy(
                lines = lines,
                columns = if (lines.isNotEmpty()) csvParser.parse(lines.first(), it.config.columnSeparator) else emptyList()
            )
        }
    }

    fun updateConfig(config: CsvConfig) {
        _uiState.update {
            it.copy(
                config = config,
                columns = if (it.lines.isNotEmpty()) csvParser.parse(it.lines.first(), config.columnSeparator) else emptyList()
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
