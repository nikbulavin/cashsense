package ru.resodostudios.cashsense.feature.transfer

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
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import ru.resodostudios.cashsense.core.data.repository.TransactionsRepository
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.model.data.StatusType
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.network.di.ApplicationScope
import ru.resodostudios.cashsense.core.util.getUsdCurrency
import ru.resodostudios.cashsense.feature.transfer.navigation.TransferDialogRoute
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Currency
import javax.inject.Inject
import kotlin.uuid.Uuid

@HiltViewModel
class TransferDialogViewModel @Inject constructor(
    private val walletsRepository: WalletsRepository,
    private val transactionsRepository: TransactionsRepository,
    savedStateHandle: SavedStateHandle,
    @ApplicationScope private val appScope: CoroutineScope,
) : ViewModel() {

    private val transferDialogDestination: TransferDialogRoute = savedStateHandle.toRoute()

    private val _transferDialogState = MutableStateFlow(TransferDialogUiState())
    val transferDialogState = _transferDialogState.asStateFlow()

    init {
        transferDialogDestination.walletId?.let(::loadTransfer)
    }

    private fun loadTransfer(walletId: String) {
        viewModelScope.launch {
            _transferDialogState.update { TransferDialogUiState(isLoading = true) }
            val transferWallets = walletsRepository.getWalletsWithTransactionsAndCategories()
                .first()
                .map { extendedWallet ->
                    val currentBalance = extendedWallet.transactionsWithCategories
                        .sumOf { it.transaction.amount }
                        .plus(extendedWallet.wallet.initialBalance)
                    TransferWallet(
                        id = extendedWallet.wallet.id,
                        title = extendedWallet.wallet.title,
                        currentBalance = currentBalance,
                        currency = extendedWallet.wallet.currency,
                    )
                }
            val sendingWallet = transferWallets.first { it.id == walletId }
            val receivingWallet = if (transferWallets.size == 2) {
                transferWallets.first { it != sendingWallet }
            } else {
                TransferWallet()
            }
            val exchangeRate = if (sendingWallet.currency == receivingWallet.currency) "1" else ""
            _transferDialogState.update {
                it.copy(
                    sendingWallet = sendingWallet,
                    receivingWallet = receivingWallet,
                    exchangeRate = exchangeRate,
                    transferWallets = transferWallets,
                    isLoading = false,
                )
            }
        }
    }

    private fun calculateAmount(convertedAmount: String, exchangeRate: String): String {
        return if (convertedAmount.isNotBlank() && exchangeRate.isNotBlank() && BigDecimal(exchangeRate) != BigDecimal.ZERO) {
            BigDecimal(convertedAmount)
                .divide(BigDecimal(exchangeRate), 2, RoundingMode.HALF_UP)
                .toString()
        } else {
            ""
        }
    }

    private fun calculateConvertedAmount(amount: String, exchangeRate: String): String {
        return if (amount.isNotBlank() && exchangeRate.isNotBlank()) {
            BigDecimal(amount)
                .multiply(BigDecimal(exchangeRate))
                .divide(BigDecimal.ONE, 2, RoundingMode.HALF_UP)
                .toString()
        } else {
            ""
        }
    }

    fun saveTransfer(state: TransferDialogUiState) {
        appScope.launch {
            state.asTransfer().forEach {
                transactionsRepository.upsertTransaction(it)
            }
        }
    }

    fun updateSendingWallet(transferWallet: TransferWallet) {
        _transferDialogState.update {
            it.copy(sendingWallet = transferWallet)
        }
        if (transferWallet.currency == _transferDialogState.value.receivingWallet.currency) {
            _transferDialogState.update {
                it.copy(exchangeRate = "1")
            }
        } else {
            _transferDialogState.update {
                it.copy(exchangeRate = "")
            }
        }
    }

    fun updateReceivingWallet(transferWallet: TransferWallet) {
        _transferDialogState.update {
            it.copy(receivingWallet = transferWallet)
        }
        if (transferWallet.currency == _transferDialogState.value.sendingWallet.currency) {
            _transferDialogState.update {
                it.copy(exchangeRate = "1")
            }
        } else {
            _transferDialogState.update {
                it.copy(exchangeRate = "")
            }
        }
    }

    fun updateAmount(amount: String) {
        val convertedAmount = calculateConvertedAmount(amount, _transferDialogState.value.exchangeRate)
        _transferDialogState.update {
            it.copy(amount = amount, convertedAmount = convertedAmount)
        }
    }

    fun updateExchangingRate(exchangeRate: String) {
        val convertedAmount = calculateConvertedAmount(_transferDialogState.value.amount, exchangeRate)
        _transferDialogState.update {
            it.copy(exchangeRate = exchangeRate, convertedAmount = convertedAmount)
        }
    }

    fun updateConvertedAmount(convertedAmount: String) {
        val amount = calculateAmount(convertedAmount, _transferDialogState.value.exchangeRate)
        _transferDialogState.update {
            it.copy(convertedAmount = convertedAmount, amount = amount)
        }
    }

    fun updateDate(date: Instant) {
        _transferDialogState.update {
            it.copy(date = date)
        }
    }
}

@Immutable
data class TransferDialogUiState(
    val sendingWallet: TransferWallet = TransferWallet(),
    val receivingWallet: TransferWallet = TransferWallet(),
    val amount: String = "",
    val exchangeRate: String = "",
    val convertedAmount: String = "",
    val transferWallets: List<TransferWallet> = emptyList(),
    val isLoading: Boolean = false,
    val date: Instant = Clock.System.now(),
)

data class TransferWallet(
    val id: String = "",
    val title: String = "",
    val currentBalance: BigDecimal = BigDecimal.ZERO,
    val currency: Currency? = null,
)

fun TransferDialogUiState.asTransfer(): List<Transaction> {
    val transferId = Uuid.random()
    val timestamp = Clock.System.now()
    val withdrawalAmount = BigDecimal(amount)

    val withdrawalTransaction = Transaction(
        id = Uuid.random().toHexString(),
        walletOwnerId = sendingWallet.id,
        description = null,
        amount = withdrawalAmount.negate(),
        timestamp = timestamp,
        status = StatusType.COMPLETED,
        ignored = true,
        transferId = transferId,
        currency = getUsdCurrency(),
    )
    val depositTransaction = Transaction(
        id = Uuid.random().toHexString(),
        walletOwnerId = receivingWallet.id,
        description = null,
        amount = BigDecimal(convertedAmount),
        timestamp = timestamp,
        status = StatusType.COMPLETED,
        ignored = true,
        transferId = transferId,
        currency = getUsdCurrency(),
    )

    return buildList { add(withdrawalTransaction); add(depositTransaction) }
}