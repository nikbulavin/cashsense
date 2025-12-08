package ru.resodostudios.cashsense.feature.wallet.dialog.impl

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
import ru.resodostudios.cashsense.core.data.repository.UserDataRepository
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.domain.GetExtendedUserWalletUseCase
import ru.resodostudios.cashsense.core.model.data.Wallet
import ru.resodostudios.cashsense.core.network.di.ApplicationScope
import ru.resodostudios.cashsense.core.ui.util.cleanAmount
import ru.resodostudios.cashsense.core.util.getUsdCurrency
import ru.resodostudios.cashsense.feature.wallet.dialog.impl.navigation.WalletDialogRoute
import java.util.Currency
import javax.inject.Inject
import kotlin.uuid.Uuid

@HiltViewModel
internal class WalletDialogViewModel @Inject constructor(
    private val walletsRepository: WalletsRepository,
    private val userDataRepository: UserDataRepository,
    savedStateHandle: SavedStateHandle,
    @ApplicationScope private val appScope: CoroutineScope,
    private val getExtendedUserWallet: GetExtendedUserWalletUseCase,
) : ViewModel() {

    private val walletDialogDestination: WalletDialogRoute = savedStateHandle.toRoute()

    private val _walletDialogState = MutableStateFlow(WalletDialogUiState())
    val walletDialogState = _walletDialogState.asStateFlow()

    init {
        if (walletDialogDestination.walletId != null) {
            loadWallet(walletDialogDestination.walletId)
        } else {
            loadUserData()
        }
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _walletDialogState.update { WalletDialogUiState(isLoading = true) }
            val userData = userDataRepository.userData.first()
            _walletDialogState.update {
                WalletDialogUiState(
                    currency = Currency.getInstance(userData.currency),
                )
            }
        }
    }

    private fun loadWallet(id: String) {
        viewModelScope.launch {
            _walletDialogState.update { it.copy(isLoading = true) }
            val extendedUserWallet = getExtendedUserWallet.invoke(id).first()
            _walletDialogState.update {
                it.copy(
                    id = extendedUserWallet.wallet.id,
                    title = extendedUserWallet.wallet.title,
                    initialBalance = extendedUserWallet.wallet.initialBalance.toString(),
                    currency = extendedUserWallet.wallet.currency,
                    isPrimary = extendedUserWallet.isPrimary,
                    isLoading = false,
                    isCurrencyEditable = extendedUserWallet.transactions.isEmpty(),
                )
            }
        }
    }

    fun saveWallet(state: WalletDialogUiState) {
        appScope.launch {
            val wallet = state.asWallet()
            walletsRepository.upsertWallet(wallet)
            userDataRepository.setPrimaryWallet(wallet.id, state.isPrimary)
        }
    }

    fun updateTitle(title: String) {
        _walletDialogState.update {
            it.copy(title = title)
        }
    }

    fun updateInitialBalance(initialBalance: String) {
        _walletDialogState.update {
            it.copy(initialBalance = initialBalance.cleanAmount())
        }
    }

    fun updateCurrency(currency: Currency) {
        _walletDialogState.update {
            it.copy(currency = currency)
        }
    }

    fun updatePrimary(isPrimary: Boolean) {
        _walletDialogState.update {
            it.copy(isPrimary = isPrimary)
        }
    }
}

@Immutable
data class WalletDialogUiState(
    val id: String = "",
    val title: String = "",
    val initialBalance: String = "",
    val currency: Currency = getUsdCurrency(),
    val isPrimary: Boolean = false,
    val isLoading: Boolean = false,
    val isCurrencyEditable: Boolean = true,
)

fun WalletDialogUiState.asWallet(): Wallet {
    return Wallet(
        id = id.ifBlank { Uuid.random().toHexString() },
        title = title.trim(),
        initialBalance = if (initialBalance.isBlank()) {
            0.toBigDecimal()
        } else {
            initialBalance.toBigDecimal()
        },
        currency = currency,
    )
}