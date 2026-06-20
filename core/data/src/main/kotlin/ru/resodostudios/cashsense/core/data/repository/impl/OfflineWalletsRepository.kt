package ru.resodostudios.cashsense.core.data.repository.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import ru.resodostudios.cashsense.core.data.model.asEntity
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.database.dao.WalletDao
import ru.resodostudios.cashsense.core.database.model.PopulatedWallet
import ru.resodostudios.cashsense.core.database.model.asExternalModel
import ru.resodostudios.cashsense.core.datastore.CsPreferencesDataSource
import ru.resodostudios.cashsense.core.model.ExtendedWallet
import ru.resodostudios.cashsense.core.model.Wallet
import java.util.Currency
import javax.inject.Inject

internal class OfflineWalletsRepository @Inject constructor(
    private val walletDao: WalletDao,
    private val csPreferencesDataSource: CsPreferencesDataSource,
) : WalletsRepository {

    override fun getExtendedWallet(walletId: String): Flow<ExtendedWallet> {
        return walletDao.getPopulatedWallet(walletId)
            .mapNotNull { it?.asExternalModel() }
    }

    override fun getExtendedWallets(): Flow<List<ExtendedWallet>> {
        return walletDao.getPopulatedWallets()
            .map { it.map(PopulatedWallet::asExternalModel) }
    }

    override fun getDistinctCurrencies(): Flow<List<Currency>> = walletDao.getDistinctCurrencies()

    override suspend fun upsertWallet(wallet: Wallet) = walletDao.upsertWallet(wallet.asEntity())

    override suspend fun deleteWallet(id: String) {
        walletDao.deleteWallet(id)
        val userData = csPreferencesDataSource.userData.first()
        if (id == userData.primaryWalletId) {
            csPreferencesDataSource.setPrimaryWalletId("")
        }
    }
}