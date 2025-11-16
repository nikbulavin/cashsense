package ru.resodostudios.cashsense.core.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import ru.resodostudios.cashsense.core.data.repository.UserDataRepository
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.model.data.ExtendedUserWallet
import javax.inject.Inject

class GetExtendedUserWalletsUseCase @Inject constructor(
    private val walletsRepository: WalletsRepository,
    private val userDataRepository: UserDataRepository,
) {

    operator fun invoke(): Flow<List<ExtendedUserWallet>> = combine(
        walletsRepository.getExtendedWallets(),
        userDataRepository.userData,
    ) { extendedWallets, userData ->
        extendedWallets
            .map { extendedWallet ->
                ExtendedUserWallet(
                    wallet = extendedWallet.wallet,
                    transactions = extendedWallet.transactions,
                    currentBalance = extendedWallet.currentBalance,
                    isPrimary = extendedWallet.wallet.id == userData.primaryWalletId,
                )
            }
            .sortedByDescending { it.wallet.id == userData.primaryWalletId }
    }
}