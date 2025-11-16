package ru.resodostudios.cashsense.core.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import ru.resodostudios.cashsense.core.data.repository.UserDataRepository
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.model.data.ExtendedUserWallet
import javax.inject.Inject

class GetExtendedUserWalletUseCase @Inject constructor(
    private val walletsRepository: WalletsRepository,
    private val userDataRepository: UserDataRepository,
) {

    operator fun invoke(walletId: String): Flow<ExtendedUserWallet> = combine(
        walletsRepository.getExtendedWallet(walletId),
        userDataRepository.userData,
    ) { extendedWallet, userData ->
        ExtendedUserWallet(
            wallet = extendedWallet.wallet,
            transactions = extendedWallet.transactions.sortedByDescending { it.timestamp },
            currentBalance = extendedWallet.currentBalance,
            isPrimary = extendedWallet.wallet.id == userData.primaryWalletId,
        )
    }
}