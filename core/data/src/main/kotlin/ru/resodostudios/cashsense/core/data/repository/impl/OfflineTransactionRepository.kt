package ru.resodostudios.cashsense.core.data.repository.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.resodostudios.cashsense.core.data.model.asEntity
import ru.resodostudios.cashsense.core.data.repository.TransactionsRepository
import ru.resodostudios.cashsense.core.database.dao.TransactionDao
import ru.resodostudios.cashsense.core.database.model.asExternalModel
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.model.data.TransactionCategoryCrossRef
import ru.resodostudios.cashsense.core.model.data.Transfer
import javax.inject.Inject
import kotlin.uuid.Uuid

internal class OfflineTransactionRepository @Inject constructor(
    private val dao: TransactionDao,
) : TransactionsRepository {

    override fun getTransaction(transactionId: String): Flow<Transaction> {
        return dao.getTransactionWithCategoryEntity(transactionId)
            .map { it.asExternalModel() }
    }

    override fun getTransactionCategoryCrossRefs(categoryId: String): Flow<List<TransactionCategoryCrossRef>> {
        return dao.getTransactionCategoryCrossRefs(categoryId)
            .map { it.map { crossRef -> crossRef.asExternalModel() } }
    }

    override fun getTransactionsCount(): Flow<Int> = dao.getTransactionsCount()

    override fun getTransfer(transferId: Uuid, senderWalletId: String): Flow<Transfer> {
        return dao.getTransfer(transferId)
            .map { it.map { transaction -> transaction.asExternalModel() } }
            .map { transferTransactions ->
                val withdrawalTransaction = transferTransactions.find { it.walletOwnerId == senderWalletId }!!
                val depositTransaction = transferTransactions.find { it.walletOwnerId != senderWalletId }!!
                Transfer(
                    withdrawalTransaction = withdrawalTransaction,
                    depositTransaction = depositTransaction,
                )
            }
    }

    override suspend fun upsertTransaction(transaction: Transaction) {
        dao.upsertTransactionWithCategory(
            transaction = transaction.asEntity(),
            category = transaction.category?.asEntity(),
        )
    }

    override suspend fun deleteTransaction(id: String) = dao.deleteTransaction(id)

    override suspend fun upsertTransactionCategoryCrossRef(crossRef: TransactionCategoryCrossRef) {
        dao.upsertTransactionCategoryCrossRef(crossRef.asEntity())
    }

    override suspend fun upsertTransfer(transfer: Transfer) {
        dao.upsertTransfer(
            withdrawalTransaction = transfer.withdrawalTransaction.asEntity(),
            depositTransaction = transfer.depositTransaction.asEntity(),
        )
    }

    override suspend fun deleteTransfer(uuid: Uuid) = dao.deleteTransfer(uuid)
}