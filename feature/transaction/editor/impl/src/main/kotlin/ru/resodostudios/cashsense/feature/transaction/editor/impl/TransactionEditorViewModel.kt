package ru.resodostudios.cashsense.feature.transaction.editor.impl

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.data.repository.CategoriesRepository
import ru.resodostudios.cashsense.core.data.repository.TransactionsRepository
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.network.di.ApplicationScope
import ru.resodostudios.cashsense.core.ui.util.cleanAmount
import ru.resodostudios.cashsense.core.util.getUsdCurrency
import ru.resodostudios.cashsense.feature.transaction.editor.api.TransactionEditorNavKey
import java.util.Currency
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid

@HiltViewModel(assistedFactory = TransactionDialogViewModel.Factory::class)
internal class TransactionDialogViewModel @AssistedInject constructor(
    private val transactionsRepository: TransactionsRepository,
    private val categoriesRepository: CategoriesRepository,
    @ApplicationScope private val appScope: CoroutineScope,
    @Assisted val key: TransactionEditorNavKey,
) : ViewModel() {

    private val _transactionEditorState = MutableStateFlow(TransactionDialogUiState())
    val transactionEditorState = _transactionEditorState.asStateFlow()

    init {
        _transactionEditorState.update { it.copy(isLoading = true) }
        val transactionId = key.transactionId
        if (transactionId == null) {
            loadCategories()
        } else {
            loadTransaction(transactionId)
        }
    }

    fun saveTransaction() {
        appScope.launch {
            val state = _transactionEditorState.value
            transactionsRepository.upsertTransaction(
                state.asTransaction(key.walletId, state.category)
            )
        }
    }

    fun updateTransactionType(type: TransactionType) {
        _transactionEditorState.update {
            it.copy(transactionType = type)
        }
    }

    fun updateAmount(amount: String) {
        _transactionEditorState.update {
            it.copy(amount = amount.cleanAmount())
        }
    }

    fun updateCategory(category: Category?) {
        _transactionEditorState.update {
            it.copy(category = category)
        }
    }

    fun updateCompletionStatus(completed: Boolean) {
        _transactionEditorState.update {
            it.copy(completed = completed)
        }
    }

    fun updateDate(date: Instant) {
        _transactionEditorState.update {
            it.copy(date = date)
        }
    }

    fun updateDescription(description: String) {
        _transactionEditorState.update {
            it.copy(description = description)
        }
    }

    fun updateIgnoredState(ignored: Boolean) {
        _transactionEditorState.update {
            it.copy(ignored = ignored)
        }
    }

    private fun loadTransaction(id: String) {
        viewModelScope.launch {
            val transaction = transactionsRepository.getTransaction(id).first()
            val date = if (key.repeated) {
                Clock.System.now()
            } else {
                transaction.timestamp
            }
            _transactionEditorState.update {
                it.copy(
                    transactionId = if (key.repeated) "" else transaction.id,
                    description = transaction.description ?: "",
                    amount = transaction.amount.abs().toString(),
                    transactionType = if (transaction.amount.signum() < 0) TransactionType.EXPENSE else TransactionType.INCOME,
                    date = date,
                    category = transaction.category,
                    completed = transaction.completed,
                    ignored = transaction.ignored,
                    isLoading = false,
                    isTransfer = transaction.transferId != null,
                    categories = buildList {
                        add(null)
                        addAll(categoriesRepository.getCategories().first())
                    },
                )
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _transactionEditorState.update {
                it.copy(
                    categories = buildList {
                        add(null)
                        addAll(categoriesRepository.getCategories().first())
                    },
                    isLoading = false,
                )
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(key: TransactionEditorNavKey): TransactionDialogViewModel
    }
}

enum class TransactionType {
    EXPENSE,
    INCOME,
}

@Immutable
data class TransactionDialogUiState(
    val transactionId: String = "",
    val description: String = "",
    val amount: String = "",
    val currency: Currency = getUsdCurrency(),
    val date: Instant = Clock.System.now(),
    val category: Category? = null,
    val transactionType: TransactionType = TransactionType.EXPENSE,
    val completed: Boolean = true,
    val ignored: Boolean = false,
    val isLoading: Boolean = false,
    val isTransfer: Boolean = false,
    val categories: List<Category?> = emptyList(),
)

fun TransactionDialogUiState.asTransaction(walletId: String, category: Category?): Transaction {
    return Transaction(
        id = transactionId.ifBlank { Uuid.random().toHexString() },
        walletOwnerId = walletId,
        description = description.ifBlank { null }?.trim(),
        amount = amount
            .toBigDecimal()
            .run { if (transactionType == TransactionType.EXPENSE) negate() else abs() },
        timestamp = date,
        completed = completed,
        ignored = ignored,
        transferId = null,
        currency = getUsdCurrency(),
        category = category,
    )
}