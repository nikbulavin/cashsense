package ru.resodostudios.cashsense.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
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