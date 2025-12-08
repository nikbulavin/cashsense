package ru.resodostudios.cashsense.feature.transaction.dialog.impl

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
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
import ru.resodostudios.cashsense.core.util.Constants.WALLET_ID_KEY
import ru.resodostudios.cashsense.core.util.getUsdCurrency
import ru.resodostudios.cashsense.feature.transaction.dialog.impl.TransactionDialogEvent.Repeat
import ru.resodostudios.cashsense.feature.transaction.dialog.impl.TransactionDialogEvent.Save
import ru.resodostudios.cashsense.feature.transaction.dialog.impl.TransactionDialogEvent.UpdateAmount
import ru.resodostudios.cashsense.feature.transaction.dialog.impl.TransactionDialogEvent.UpdateCategory
import ru.resodostudios.cashsense.feature.transaction.dialog.impl.TransactionDialogEvent.UpdateCompletionStatus
import ru.resodostudios.cashsense.feature.transaction.dialog.impl.TransactionDialogEvent.UpdateCurrency
import ru.resodostudios.cashsense.feature.transaction.dialog.impl.TransactionDialogEvent.UpdateDate
import ru.resodostudios.cashsense.feature.transaction.dialog.impl.TransactionDialogEvent.UpdateDescription
import ru.resodostudios.cashsense.feature.transaction.dialog.impl.TransactionDialogEvent.UpdateTransactionId
import ru.resodostudios.cashsense.feature.transaction.dialog.impl.TransactionDialogEvent.UpdateTransactionIgnoring
import ru.resodostudios.cashsense.feature.transaction.dialog.impl.TransactionDialogEvent.UpdateTransactionType
import ru.resodostudios.cashsense.feature.transaction.dialog.impl.TransactionDialogEvent.UpdateWalletId
import ru.resodostudios.cashsense.feature.transaction.dialog.impl.navigation.TransactionDialogRoute
import java.util.Currency
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid

@HiltViewModel
internal class TransactionDialogViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val transactionsRepository: TransactionsRepository,
    private val categoriesRepository: CategoriesRepository,
    @ApplicationScope private val appScope: CoroutineScope,
) : ViewModel() {

    private val transactionDestination: TransactionDialogRoute = savedStateHandle.toRoute()

    private val _transactionDialogUiState = MutableStateFlow(TransactionDialogUiState())
    val transactionDialogUiState = _transactionDialogUiState.asStateFlow()

    init {
        _transactionDialogUiState.update { it.copy(isLoading = true) }
        updateWalletId(transactionDestination.walletId)
        val transactionId = transactionDestination.transactionId
        if (transactionId == null) {
            loadCategories()
        } else {
            loadTransaction(transactionId)
        }
    }

    fun onTransactionEvent(event: TransactionDialogEvent) {
        when (event) {
            Repeat -> repeatTransaction()
            is Save -> saveTransaction(event.state)
            is UpdateTransactionId -> updateTransactionId(event.id)
            is UpdateWalletId -> updateWalletId(event.id)
            is UpdateCurrency -> updateCurrency(event.currency)
            is UpdateDate -> updateDate(event.date)
            is UpdateAmount -> updateAmount(event.amount)
            is UpdateTransactionType -> updateTransactionType(event.type)
            is UpdateCompletionStatus -> updateCompletionState(event.completed)
            is UpdateCategory -> updateCategory(event.category)
            is UpdateDescription -> updateDescription(event.description)
            is UpdateTransactionIgnoring -> updateTransactionIgnoring(event.ignored)
        }
    }

    private fun saveTransaction(state: TransactionDialogUiState) {
        appScope.launch {
            val transaction = state.asTransaction(transactionDestination.walletId, state.category)
            transactionsRepository.upsertTransaction(transaction)
        }
    }

    private fun updateTransactionId(id: String) {
        _transactionDialogUiState.update {
            it.copy(transactionId = id)
        }
    }

    private fun updateWalletId(id: String) {
        savedStateHandle[WALLET_ID_KEY] = id
    }

    private fun updateCurrency(currency: Currency) {
        _transactionDialogUiState.update {
            it.copy(currency = currency)
        }
    }

    private fun updateDate(date: Instant) {
        _transactionDialogUiState.update {
            it.copy(date = date)
        }
    }

    private fun updateAmount(amount: String) {
        _transactionDialogUiState.update {
            it.copy(amount = amount.cleanAmount())
        }
    }

    private fun updateTransactionType(type: TransactionType) {
        _transactionDialogUiState.update {
            it.copy(transactionType = type)
        }
    }

    private fun updateCompletionState(completed: Boolean) {
        _transactionDialogUiState.update {
            it.copy(completed = completed)
        }
    }

    private fun updateCategory(category: Category?) {
        _transactionDialogUiState.update {
            it.copy(category = category)
        }
    }

    private fun updateDescription(description: String) {
        _transactionDialogUiState.update {
            it.copy(description = description)
        }
    }

    private fun updateTransactionIgnoring(ignored: Boolean) {
        _transactionDialogUiState.update {
            it.copy(ignored = ignored)
        }
    }

    private fun repeatTransaction() {
        _transactionDialogUiState.update {
            it.copy(
                transactionId = "",
                date = Clock.System.now(),
            )
        }
    }

    private fun loadTransaction(id: String) {
        viewModelScope.launch {
            val transaction = transactionsRepository.getTransaction(id).first()
            val date = if (transactionDestination.repeated) {
                Clock.System.now()
            } else {
                transaction.timestamp
            }
            _transactionDialogUiState.update {
                it.copy(
                    transactionId = if (transactionDestination.repeated) "" else transaction.id,
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
            _transactionDialogUiState.update {
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