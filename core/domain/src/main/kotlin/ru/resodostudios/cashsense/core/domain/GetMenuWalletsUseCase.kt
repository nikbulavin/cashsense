package ru.resodostudios.cashsense.core.domain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.resodostudios.cashsense.core.common.CsDispatchers.Default
import ru.resodostudios.cashsense.core.common.Dispatcher
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.model.MenuWallet
import javax.inject.Inject

class GetMenuWalletsUseCase @Inject constructor(
    private val walletsRepository: WalletsRepository,
    @Dispatcher(Default) private val defaultDispatcher: CoroutineDispatcher,
) {

    operator fun invoke(): Flow<List<MenuWallet>> =
        walletsRepository.getExtendedWallets()
            .map { extendedWallets ->
                extendedWallets.map {
                    MenuWallet(
                        id = it.wallet.id,
                        title = it.wallet.title,
                        currentBalance = it.currentBalance,
                        currency = it.wallet.currency,
                    )
                }
            }
            .flowOn(defaultDispatcher)
}
