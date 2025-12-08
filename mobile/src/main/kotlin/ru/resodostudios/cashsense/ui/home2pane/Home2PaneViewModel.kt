package ru.resodostudios.cashsense.ui.home2pane

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.data.repository.TransactionsRepository
import ru.resodostudios.cashsense.core.data.repository.UserDataRepository
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.domain.GetExtendedUserWalletUseCase
import ru.resodostudios.cashsense.core.model.data.ExtendedUserWallet
import ru.resodostudios.cashsense.core.model.data.Wallet
import ru.resodostudios.cashsense.core.util.Constants.WALLET_ID_KEY
import ru.resodostudios.cashsense.feature.home.impl.navigation.HomeRoute
import javax.inject.Inject

@HiltViewModel
class Home2PaneViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val walletsRepository: WalletsRepository,
    private val transactionsRepository: TransactionsRepository,
    private val userDataRepository: UserDataRepository,
    private val getExtendedUserWallet: GetExtendedUserWalletUseCase,
) : ViewModel() {

    private val homeDestination: HomeRoute = savedStateHandle.toRoute()

    val selectedWalletId: StateFlow<String?> = savedStateHandle.getStateFlow(
        key = WALLET_ID_KEY,
        initialValue = homeDestination.walletId,
    )

    private val lastRemovedWalletState = MutableStateFlow<ExtendedUserWallet?>(null)
    private val _shouldDisplayUndoWalletState = MutableStateFlow(false)
    val shouldDisplayUndoWalletState = _shouldDisplayUndoWalletState.asStateFlow()

    fun onWalletSelect(walletId: String?) {
        savedStateHandle[WALLET_ID_KEY] = walletId
    }

    fun deleteWallet(walletId: String) {
        viewModelScope.launch {
            lastRemovedWalletState.value = getExtendedUserWallet.invoke(walletId).first()
            _shouldDisplayUndoWalletState.value = true
            walletsRepository.deleteWallet(walletId)
            onWalletSelect(null)
        }
    }

    fun undoWalletRemoval() {
        viewModelScope.launch {
            lastRemovedWalletState.value?.let {
                val wallet = Wallet(
                    id = it.wallet.id,
                    title = it.wallet.title,
                    initialBalance = it.wallet.initialBalance,
                    currency = it.wallet.currency,
                )
                walletsRepository.upsertWallet(wallet)
                if (it.isPrimary) userDataRepository.setPrimaryWallet(wallet.id, true)
                it.transactions.forEach { transactionWithCategory ->
                    transactionsRepository.upsertTransaction(transactionWithCategory)
                }
            }
            clearUndoState()
        }
    }

    fun clearUndoState() {
        _shouldDisplayUndoWalletState.value = false
        lastRemovedWalletState.value = null
    }
}
