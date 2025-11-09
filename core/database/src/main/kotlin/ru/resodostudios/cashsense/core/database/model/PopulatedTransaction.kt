package ru.resodostudios.cashsense.core.database.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import ru.resodostudios.cashsense.core.model.data.TransactionWithCategory

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
        )
    )
    val category: CategoryEntity?,
)

fun PopulatedTransaction.asExternalModel(): TransactionWithCategory {
    return TransactionWithCategory(
        transaction = transaction.asExternalModel(),
        category = category?.asExternalModel(),
    )
}