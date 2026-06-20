package ru.resodostudios.cashsense.core.data.repository

import kotlinx.coroutines.flow.Flow
import ru.resodostudios.cashsense.core.model.Transaction
import ru.resodostudios.cashsense.core.model.Transfer
import kotlin.uuid.Uuid

interface TransactionsRepository {

    fun getTransaction(transactionId: String): Flow<Transaction>

    fun getTransactionsCount(): Flow<Int>

    fun getTransfer(transferId: Uuid, senderWalletId: String): Flow<Transfer>

    suspend fun upsertTransaction(transaction: Transaction)

    suspend fun deleteTransaction(id: String)

    suspend fun upsertTransfer(transfer: Transfer)

    suspend fun deleteTransfer(uuid: Uuid)
}