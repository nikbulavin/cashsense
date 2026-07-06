package ru.resodostudios.cashsense.feature.transfer.impl

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
import ru.resodostudios.cashsense.core.common.di.ApplicationScope
import ru.resodostudios.cashsense.core.common.getUsdCurrency
import ru.resodostudios.cashsense.core.data.repository.TransactionsRepository
import ru.resodostudios.cashsense.core.domain.GetMenuWalletsUseCase
import ru.resodostudios.cashsense.core.model.MenuWallet
import ru.resodostudios.cashsense.core.model.Transaction
import ru.resodostudios.cashsense.core.model.Transfer
import ru.resodostudios.cashsense.feature.transfer.api.TransferEditorNavKey
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid

@HiltViewModel(assistedFactory = TransferEditorViewModel.Factory::class)
internal class TransferEditorViewModel @AssistedInject constructor(
    private val transactionsRepository: TransactionsRepository,
    private val getMenuWalletsUseCase: GetMenuWalletsUseCase,
    @ApplicationScope private val appScope: CoroutineScope,
    @Assisted val key: TransferEditorNavKey,
) : ViewModel() {

    private val _transferEditorState = MutableStateFlow(TransferEditorState())
    val transferEditorState = _transferEditorState.asStateFlow()

    init {
        loadTransfer(key.walletId)
    }

    private fun loadTransfer(walletId: String?) {
        viewModelScope.launch {
            _transferEditorState.update { it.copy(isLoading = true) }
            val transferWallets = getMenuWalletsUseCase().first()
            val sendingWallet = transferWallets.find { it.id == walletId } ?: MenuWallet()
            val receivingWallet = if (transferWallets.size == 2) {
                transferWallets.first { it != sendingWallet }
            } else {
                MenuWallet()
            }
            val exchangeRate = if (sendingWallet.currency == receivingWallet.currency) "1" else ""
            _transferEditorState.update {
                it.copy(
                    sendingWallet = sendingWallet,
                    receivingWallet = receivingWallet,
                    exchangeRate = exchangeRate,
                    availableWallets = transferWallets,
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

    fun saveTransfer() {
        appScope.launch {
            transactionsRepository.upsertTransfer(_transferEditorState.value.asTransfer())
        }
    }

    fun updateSendingWallet(wallet: MenuWallet) {
        _transferEditorState.update {
            it.copy(sendingWallet = wallet)
        }
        if (wallet.currency == _transferEditorState.value.receivingWallet.currency) {
            _transferEditorState.update {
                it.copy(exchangeRate = "1")
            }
        } else {
            _transferEditorState.update {
                it.copy(exchangeRate = "")
            }
        }
    }

    fun updateReceivingWallet(wallet: MenuWallet) {
        _transferEditorState.update {
            it.copy(receivingWallet = wallet)
        }
        if (wallet.currency == _transferEditorState.value.sendingWallet.currency) {
            _transferEditorState.update {
                it.copy(exchangeRate = "1")
            }
        } else {
            _transferEditorState.update {
                it.copy(exchangeRate = "")
            }
        }
    }

    fun updateAmount(amount: String) {
        val convertedAmount = calculateConvertedAmount(
            amount = amount,
            exchangeRate = _transferEditorState.value.exchangeRate,
        )
        _transferEditorState.update {
            it.copy(amount = amount, convertedAmount = convertedAmount)
        }
    }

    fun updateExchangingRate(exchangeRate: String) {
        val convertedAmount = calculateConvertedAmount(
            amount = _transferEditorState.value.amount,
            exchangeRate = exchangeRate,
        )
        _transferEditorState.update {
            it.copy(exchangeRate = exchangeRate, convertedAmount = convertedAmount)
        }
    }

    fun updateConvertedAmount(convertedAmount: String) {
        val amount = calculateAmount(
            convertedAmount = convertedAmount,
            exchangeRate = _transferEditorState.value.exchangeRate,
        )
        _transferEditorState.update {
            it.copy(convertedAmount = convertedAmount, amount = amount)
        }
    }

    fun updateDate(date: Instant) {
        _transferEditorState.update {
            it.copy(date = date)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(key: TransferEditorNavKey): TransferEditorViewModel
    }
}

@Immutable
data class TransferEditorState(
    val sendingWallet: MenuWallet = MenuWallet(),
    val receivingWallet: MenuWallet = MenuWallet(),
    val amount: String = "",
    val exchangeRate: String = "",
    val convertedAmount: String = "",
    val availableWallets: List<MenuWallet> = emptyList(),
    val isLoading: Boolean = false,
    val date: Instant = Clock.System.now(),
)

fun TransferEditorState.asTransfer(): Transfer {
    val transferId = Uuid.random()
    val withdrawalTransaction = Transaction(
        id = Uuid.random().toHexString(),
        walletOwnerId = sendingWallet.id,
        description = null,
        amount = BigDecimal(amount).negate(),
        timestamp = date,
        completed = true,
        ignored = true,
        transferId = transferId,
        currency = getUsdCurrency(),
        category = null,
    )
    val depositTransaction = Transaction(
        id = Uuid.random().toHexString(),
        walletOwnerId = receivingWallet.id,
        description = null,
        amount = BigDecimal(convertedAmount),
        timestamp = date,
        completed = true,
        ignored = true,
        transferId = transferId,
        currency = getUsdCurrency(),
        category = null,
    )

    return Transfer(
        withdrawalTransaction = withdrawalTransaction,
        depositTransaction = depositTransaction,
    )
}
