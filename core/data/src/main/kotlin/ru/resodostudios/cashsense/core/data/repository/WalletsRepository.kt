package ru.resodostudios.cashsense.core.data.repository

import kotlinx.coroutines.flow.Flow
import ru.resodostudios.cashsense.core.model.data.ExtendedWallet
import ru.resodostudios.cashsense.core.model.data.Wallet
import java.util.Currency

interface WalletsRepository {

    fun getExtendedWallet(walletId: String): Flow<ExtendedWallet>

    fun getExtendedWallets(): Flow<List<ExtendedWallet>>

    fun getDistinctCurrencies(): Flow<List<Currency>>

    suspend fun upsertWallet(wallet: Wallet)

    suspend fun deleteWallet(id: String)
}