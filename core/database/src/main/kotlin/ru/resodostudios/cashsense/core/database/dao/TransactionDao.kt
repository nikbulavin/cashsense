package ru.resodostudios.cashsense.core.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow
import ru.resodostudios.cashsense.core.database.model.PopulatedTransaction
import ru.resodostudios.cashsense.core.database.model.TransactionEntity
import kotlin.uuid.Uuid

@Dao
interface TransactionDao {

    @Transaction
    @Query("SELECT * FROM transactions WHERE id = :id")
    fun getPopulatedTransaction(id: String): Flow<PopulatedTransaction?>

    @Query("SELECT count(*) FROM transactions")
    fun getTransactionsCount(): Flow<Int>

    @Upsert
    suspend fun upsertTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransaction(id: String)

    @Query("SELECT * FROM transactions WHERE transfer_id = :transferId")
    fun getTransfer(transferId: Uuid): Flow<List<TransactionEntity>>

    @Transaction
    suspend fun upsertTransfer(
        withdrawalTransaction: TransactionEntity,
        depositTransaction: TransactionEntity,
    ) {
        upsertTransaction(withdrawalTransaction)
        upsertTransaction(depositTransaction)
    }

    @Query("DELETE FROM transactions WHERE transfer_id = :uuid")
    suspend fun deleteTransfer(uuid: Uuid)
}