package ru.resodostudios.cashsense.feature.transaction.detail.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ru.resodostudios.cashsense.core.data.repository.TransactionsRepository
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.feature.transaction.detail.api.TransactionNavKey
import kotlin.time.Duration.Companion.seconds

@HiltViewModel(assistedFactory = TransactionViewModel.Factory::class)
internal class TransactionViewModel @AssistedInject constructor(
    @Assisted val key: TransactionNavKey,
    transactionsRepository: TransactionsRepository,
) : ViewModel() {

    val transactionUiState: StateFlow<TransactionUiState> = transactionsRepository.getTransaction(key.transactionId)
        .map { TransactionUiState.Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = TransactionUiState.Loading,
        )

    @AssistedFactory
    interface Factory {
        fun create(key: TransactionNavKey): TransactionViewModel
    }
}

internal sealed interface TransactionUiState {

    data object Loading : TransactionUiState

    data class Success(
        val transaction: Transaction,
    ) : TransactionUiState
}