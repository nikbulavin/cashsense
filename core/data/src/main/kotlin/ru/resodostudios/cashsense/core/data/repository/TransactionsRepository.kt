package ru.resodostudios.cashsense.core.data.repository

import kotlinx.coroutines.flow.Flow
import ru.resodostudios.cashsense.core.model.data.TransactionCategoryCrossRef
import ru.resodostudios.cashsense.core.model.data.TransactionWithCategory
import kotlin.uuid.Uuid

interface TransactionsRepository {

    fun getTransactionWithCategory(transactionId: String): Flow<TransactionWithCategory>

    fun getTransactionCategoryCrossRefs(categoryId: String): Flow<List<TransactionCategoryCrossRef>>

    fun getTransactionsCount(): Flow<Int>

    suspend fun upsertTransaction(transactionWithCategory: TransactionWithCategory)

    suspend fun deleteTransaction(id: String)

    suspend fun upsertTransactionCategoryCrossRef(crossRef: TransactionCategoryCrossRef)

    suspend fun deleteTransfer(uuid: Uuid)
}