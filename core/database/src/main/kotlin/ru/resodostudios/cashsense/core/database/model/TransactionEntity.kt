package ru.resodostudios.cashsense.core.database.model

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import ru.resodostudios.cashsense.core.common.getUsdCurrency
import ru.resodostudios.cashsense.core.model.Transaction
import java.math.BigDecimal
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = WalletEntity::class,
            parentColumns = ["id"],
            childColumns = ["wallet_owner_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [
        Index("wallet_owner_id"),
        Index("transfer_id"),
        Index("category_id"),
    ],
)
data class TransactionEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "wallet_owner_id")
    val walletOwnerId: String,
    val description: String?,
    val amount: BigDecimal,
    val timestamp: Instant,
    @ColumnInfo(defaultValue = "1")
    val completed: Boolean,
    @ColumnInfo(defaultValue = "0")
    val ignored: Boolean,
    @ColumnInfo(name = "transfer_id")
    val transferId: Uuid?,
    @ColumnInfo(name = "category_id")
    val categoryId: String?,
)

fun TransactionEntity.asExternalModel(): Transaction {
    return Transaction(
        id = id,
        walletOwnerId = walletOwnerId,
        description = description,
        amount = amount,
        timestamp = timestamp,
        completed = completed,
        ignored = ignored,
        transferId = transferId,
        currency = getUsdCurrency(),
        category = null,
    )
}