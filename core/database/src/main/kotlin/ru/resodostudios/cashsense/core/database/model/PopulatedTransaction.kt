package ru.resodostudios.cashsense.core.database.model

import androidx.room3.Embedded
import androidx.room3.Junction
import androidx.room3.Relation
import ru.resodostudios.cashsense.core.model.data.Transaction
import java.util.Currency

data class PopulatedTransaction(
    @Embedded
    val transaction: TransactionEntity,
    @Relation(
        parentColumns = ["wallet_owner_id"],
        entityColumns = ["id"],
        entity = WalletEntity::class,
        projection = ["currency"],
    )
    val walletCurrency: Currency,
    @Relation(
        parentColumns = ["id"],
        entityColumns = ["id"],
        associateBy = Junction(
            value = TransactionCategoryCrossRefEntity::class,
            parentColumns = ["transaction_id"],
            entityColumns = ["category_id"],
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
        currency = walletCurrency,
        category = category?.asExternalModel(),
    )
}