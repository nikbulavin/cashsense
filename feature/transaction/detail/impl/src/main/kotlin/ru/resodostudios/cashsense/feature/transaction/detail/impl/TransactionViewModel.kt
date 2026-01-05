package ru.resodostudios.cashsense.feature.transaction.detail.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.data.repository.TransactionsRepository
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.network.di.ApplicationScope
import ru.resodostudios.cashsense.feature.transaction.detail.api.TransactionNavKey
import kotlin.time.Duration.Companion.seconds

@HiltViewModel(assistedFactory = TransactionViewModel.Factory::class)
internal class TransactionViewModel @AssistedInject constructor(
    @Assisted val key: TransactionNavKey,
    private val transactionsRepository: TransactionsRepository,
    @ApplicationScope private val appScope: CoroutineScope,
) : ViewModel() {

    val transactionUiState: StateFlow<TransactionUiState> = transactionsRepository.getTransaction(key.transactionId)
        .map { TransactionUiState.Success(it) }
        .catch { TransactionUiState.Loading }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = TransactionUiState.Loading,
        )

    fun deleteTransaction(transaction: Transaction) {
        appScope.launch {
            if (transaction.transferId != null) {
                transactionsRepository.deleteTransfer(transaction.transferId!!)
            } else {
                transactionsRepository.deleteTransaction(transaction.id)
            }
        }
    }

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