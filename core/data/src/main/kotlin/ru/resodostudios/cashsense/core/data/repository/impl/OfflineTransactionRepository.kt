package ru.resodostudios.cashsense.core.data.repository.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import ru.resodostudios.cashsense.core.data.model.asEntity
import ru.resodostudios.cashsense.core.data.repository.TransactionsRepository
import ru.resodostudios.cashsense.core.database.dao.TransactionDao
import ru.resodostudios.cashsense.core.database.model.asExternalModel
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.model.data.Transfer
import javax.inject.Inject
import kotlin.uuid.Uuid

internal class OfflineTransactionRepository @Inject constructor(
    private val dao: TransactionDao,
) : TransactionsRepository {

    override fun getTransaction(transactionId: String): Flow<Transaction> {
        return dao.getPopulatedTransaction(transactionId)
            .mapNotNull { it?.asExternalModel() }
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
        dao.upsertTransaction(transaction.asEntity())
    }

    override suspend fun deleteTransaction(id: String) = dao.deleteTransaction(id)

    override suspend fun upsertTransfer(transfer: Transfer) {
        dao.upsertTransfer(
            withdrawalTransaction = transfer.withdrawalTransaction.asEntity(),
            depositTransaction = transfer.depositTransaction.asEntity(),
        )
    }

    override suspend fun deleteTransfer(uuid: Uuid) = dao.deleteTransfer(uuid)
}