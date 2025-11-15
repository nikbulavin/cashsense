package ru.resodostudios.cashsense.core.database.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.util.getUsdCurrency

data class PopulatedTransaction(
    @Embedded
    val transaction: TransactionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = TransactionCategoryCrossRefEntity::class,
            parentColumn = "transaction_id",
            entityColumn = "category_id",
        ),
    )
    val category: CategoryEntity?,
)

fun PopulatedTransaction.asExternalModel(): Transaction {
    return Transaction(
        id = transaction.id,
        walletOwnerId = transaction.walletOwnerId,
        description = transaction.description,
        amount = transaction.amount,
        timestamp = transaction.timestamp,
        completed = transaction.completed,
        ignored = transaction.ignored,
        transferId = transaction.transferId,
        currency = getUsdCurrency(),
        category = category?.asExternalModel(),
    )
}