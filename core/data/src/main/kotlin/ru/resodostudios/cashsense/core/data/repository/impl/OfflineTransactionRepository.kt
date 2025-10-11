package ru.resodostudios.cashsense.core.data.repository.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.resodostudios.cashsense.core.data.model.asEntity
import ru.resodostudios.cashsense.core.data.repository.TransactionsRepository
import ru.resodostudios.cashsense.core.database.dao.TransactionDao
import ru.resodostudios.cashsense.core.database.model.TransactionCategoryCrossRefEntity
import ru.resodostudios.cashsense.core.database.model.asExternalModel
import ru.resodostudios.cashsense.core.model.data.TransactionCategoryCrossRef
import ru.resodostudios.cashsense.core.model.data.TransactionWithCategory
import ru.resodostudios.cashsense.core.model.data.Transfer
import javax.inject.Inject
import kotlin.uuid.Uuid

internal class OfflineTransactionRepository @Inject constructor(
    private val dao: TransactionDao,
) : TransactionsRepository {

    override fun getTransactionWithCategory(transactionId: String): Flow<TransactionWithCategory> {
        return dao.getTransactionWithCategoryEntity(transactionId)
            .map { it.asExternalModel() }
    }

    override fun getTransactionCategoryCrossRefs(categoryId: String): Flow<List<TransactionCategoryCrossRef>> {
        return dao.getTransactionCategoryCrossRefs(categoryId)
            .map { it.map { crossRef -> crossRef.asExternalModel() } }
    }

    override fun getTransactionsCount(): Flow<Int> = dao.getTransactionsCount()

    override suspend fun upsertTransaction(transactionWithCategory: TransactionWithCategory) {
        dao.upsertTransaction(transactionWithCategory.transaction.asEntity())
        dao.deleteTransactionCategoryCrossRef(transactionWithCategory.transaction.id)
        transactionWithCategory.category?.id?.let { categoryId ->
            val crossRef = TransactionCategoryCrossRefEntity(
                transactionId = transactionWithCategory.transaction.id,
                categoryId = categoryId,
            )
            dao.upsertTransactionCategoryCrossRef(crossRef)
        }
    }

    override suspend fun deleteTransaction(id: String) = dao.deleteTransaction(id)

    override suspend fun upsertTransactionCategoryCrossRef(crossRef: TransactionCategoryCrossRef) {
        dao.upsertTransactionCategoryCrossRef(crossRef.asEntity())
    }

    override suspend fun upsertTransfer(transfer: Transfer) {
        dao.upsertTransaction(transfer.withdrawalTransaction.transaction.asEntity())
        dao.upsertTransaction(transfer.depositTransaction.transaction.asEntity())
    }

    override suspend fun deleteTransfer(uuid: Uuid) = dao.deleteTransfer(uuid)
}