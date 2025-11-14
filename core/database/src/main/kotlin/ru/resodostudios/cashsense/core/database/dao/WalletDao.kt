package ru.resodostudios.cashsense.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.resodostudios.cashsense.core.database.model.PopulatedWallet
import ru.resodostudios.cashsense.core.database.model.WalletEntity
import java.util.Currency

@Dao
interface WalletDao {

    @Transaction
    @Query("SELECT * FROM wallets WHERE id = :walletId")
    fun getWalletWithTransactionsAndCategoriesEntity(walletId: String): Flow<PopulatedWallet>

    @Transaction
    @Query("SELECT * FROM wallets")
    fun getWalletWithTransactionsAndCategoriesEntities(): Flow<List<PopulatedWallet>>

    @Query("SELECT DISTINCT currency FROM wallets")
    fun getDistinctCurrencies(): Flow<List<Currency>>

    @Upsert
    suspend fun upsertWallet(wallet: WalletEntity)

    @Query("DELETE FROM wallets WHERE id = :id")
    suspend fun deleteWallet(id: String)
}