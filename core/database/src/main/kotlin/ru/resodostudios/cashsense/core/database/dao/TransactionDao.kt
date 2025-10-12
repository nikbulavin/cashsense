package ru.resodostudios.cashsense.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.resodostudios.cashsense.core.database.model.PopulatedTransaction
import ru.resodostudios.cashsense.core.database.model.TransactionCategoryCrossRefEntity
import ru.resodostudios.cashsense.core.database.model.TransactionEntity
import kotlin.uuid.Uuid

@Dao
interface TransactionDao {

    @Transaction
    @Query("SELECT * FROM transactions WHERE id = :transactionId")
    fun getTransactionWithCategoryEntity(transactionId: String): Flow<PopulatedTransaction>

    @Query("SELECT * FROM transactions_categories WHERE category_id = :categoryId")
    fun getTransactionCategoryCrossRefs(categoryId: String): Flow<List<TransactionCategoryCrossRefEntity>>

    @Query("SELECT count(*) FROM transactions")
    fun getTransactionsCount(): Flow<Int>

    @Query("SELECT * FROM transactions WHERE transfer_id = :transferId")
    fun getTransfer(transferId: Uuid): Flow<List<TransactionEntity>>

    @Upsert
    suspend fun upsertTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransaction(id: String)

    @Upsert
    suspend fun upsertTransactionCategoryCrossRef(crossRef: TransactionCategoryCrossRefEntity)

    @Query("DELETE FROM transactions_categories WHERE transaction_id = :transactionId")
    suspend fun deleteTransactionCategoryCrossRef(transactionId: String)

    @Query("DELETE FROM transactions WHERE transfer_id = :uuid")
    suspend fun deleteTransfer(uuid: Uuid)
}