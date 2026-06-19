package ru.resodostudios.cashsense.core.database.model

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import ru.resodostudios.cashsense.core.model.data.TransactionCategoryCrossRef

@Entity(
    tableName = "transactions_categories",
    primaryKeys = ["transaction_id", "category_id"],
    foreignKeys = [
        ForeignKey(
            entity = TransactionEntity::class,
            parentColumns = ["id"],
            childColumns = ["transaction_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["transaction_id"]),
        Index(value = ["category_id"]),
    ],
)
data class TransactionCategoryCrossRefEntity(
    @ColumnInfo(name = "transaction_id")
    val transactionId: String,
    @ColumnInfo(name = "category_id")
    val categoryId: String,
)

fun TransactionCategoryCrossRefEntity.asExternalModel(): TransactionCategoryCrossRef {
    return TransactionCategoryCrossRef(
        transactionId = transactionId,
        categoryId = categoryId,
    )
}