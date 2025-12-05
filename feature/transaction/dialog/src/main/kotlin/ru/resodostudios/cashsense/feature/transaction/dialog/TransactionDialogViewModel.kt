package ru.resodostudios.cashsense.feature.transaction.dialog

import android.app.Activity
import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.data.repository.CategoriesRepository
import ru.resodostudios.cashsense.core.data.repository.TransactionsRepository
import ru.resodostudios.cashsense.core.data.util.InAppReviewManager
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.network.di.ApplicationScope
import ru.resodostudios.cashsense.core.ui.CategoriesUiState
import ru.resodostudios.cashsense.core.ui.CategoriesUiState.Loading
import ru.resodostudios.cashsense.core.ui.CategoriesUiState.Success
import ru.resodostudios.cashsense.core.ui.util.cleanAmount
import ru.resodostudios.cashsense.core.util.Constants.WALLET_ID_KEY
import ru.resodostudios.cashsense.core.util.getUsdCurrency
import ru.resodostudios.cashsense.feature.transaction.dialog.TransactionDialogEvent.Repeat
import ru.resodostudios.cashsense.feature.transaction.dialog.TransactionDialogEvent.Save
import ru.resodostudios.cashsense.feature.transaction.dialog.TransactionDialogEvent.UpdateAmount
import ru.resodostudios.cashsense.feature.transaction.dialog.TransactionDialogEvent.UpdateCategory
import ru.resodostudios.cashsense.feature.transaction.dialog.TransactionDialogEvent.UpdateCompletionStatus
import ru.resodostudios.cashsense.feature.transaction.dialog.TransactionDialogEvent.UpdateCurrency
import ru.resodostudios.cashsense.feature.transaction.dialog.TransactionDialogEvent.UpdateDate
import ru.resodostudios.cashsense.feature.transaction.dialog.TransactionDialogEvent.UpdateDescription
import ru.resodostudios.cashsense.feature.transaction.dialog.TransactionDialogEvent.UpdateTransactionId
import ru.resodostudios.cashsense.feature.transaction.dialog.TransactionDialogEvent.UpdateTransactionIgnoring
import ru.resodostudios.cashsense.feature.transaction.dialog.TransactionDialogEvent.UpdateTransactionType
import ru.resodostudios.cashsense.feature.transaction.dialog.TransactionDialogEvent.UpdateWalletId
import ru.resodostudios.cashsense.feature.transaction.dialog.TransactionType.EXPENSE
import ru.resodostudios.cashsense.feature.transaction.dialog.TransactionType.INCOME
import ru.resodostudios.cashsense.feature.transaction.dialog.navigation.TransactionDialogRoute
import java.math.BigDecimal.ZERO
import java.util.Currency
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import kotlin.uuid.Uuid

@HiltViewModel
internal class TransactionDialogViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val transactionsRepository: TransactionsRepository,
    categoriesRepository: CategoriesRepository,
    @ApplicationScope private val appScope: CoroutineScope,
    private val inAppReviewManager: InAppReviewManager,
) : ViewModel() {

    private val transactionDestination: TransactionDialogRoute = savedStateHandle.toRoute()

    private val _transactionDialogUiState = MutableStateFlow(TransactionDialogUiState())
    val transactionDialogUiState = _transactionDialogUiState.asStateFlow()

    val categoriesUiState: StateFlow<CategoriesUiState> =
        categoriesRepository.getCategories()
            .map { Success(it, null) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5.seconds),
                initialValue = Loading,
            )

    init {
        updateWalletId(transactionDestination.walletId)
        transactionDestination.transactionId?.let(::loadTransaction)
    }

    fun onTransactionEvent(event: TransactionDialogEvent) {
        when (event) {
            Repeat -> repeatTransaction()
            is Save -> saveTransaction(event.state, event.activity)
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

    private fun saveTransaction(state: TransactionDialogUiState, activity: Activity) {
        appScope.launch {
            val transaction = state.asTransaction(transactionDestination.walletId, state.category)
            transactionsRepository.upsertTransaction(transaction)
            inAppReviewManager.openReviewDialog(activity)
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
            _transactionDialogUiState.update {
                TransactionDialogUiState(
                    transactionId = if (transactionDestination.repeated) "" else id,
                    isLoading = true,
                )
            }
            val transaction = transactionsRepository.getTransaction(id).first()
            val date = if (transactionDestination.repeated) {
                Clock.System.now()
            } else {
                transaction.timestamp
            }
            _transactionDialogUiState.update {
                it.copy(
                    description = transaction.description ?: "",
                    amount = transaction.amount.abs().toString(),
                    transactionType = if (transaction.amount < ZERO) EXPENSE else INCOME,
                    date = date,
                    category = transaction.category,
                    completed = transaction.completed,
                    ignored = transaction.ignored,
                    isLoading = false,
                    isTransfer = transaction.transferId != null,
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
    val transactionType: TransactionType = EXPENSE,
    val completed: Boolean = true,
    val ignored: Boolean = false,
    val isLoading: Boolean = false,
    val isTransfer: Boolean = false,
)

fun TransactionDialogUiState.asTransaction(walletId: String, category: Category?): Transaction {
    return Transaction(
        id = transactionId.ifBlank { Uuid.random().toHexString() },
        walletOwnerId = walletId,
        description = description.ifBlank { null }?.trim(),
        amount = amount
            .toBigDecimal()
            .run { if (transactionType == EXPENSE) negate() else abs() },
        timestamp = date,
        completed = completed,
        ignored = ignored,
        transferId = null,
        currency = getUsdCurrency(),
        category = category,
    )
}