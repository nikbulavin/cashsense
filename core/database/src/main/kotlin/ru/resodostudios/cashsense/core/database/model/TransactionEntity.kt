package ru.resodostudios.cashsense.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.util.getUsdCurrency
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
    ],
)
data class TransactionEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "wallet_owner_id", index = true)
    val walletOwnerId: String,
    val description: String?,
    val amount: BigDecimal,
    val timestamp: Instant,
    @ColumnInfo(defaultValue = "1")
    val completed: Boolean,
    @ColumnInfo(defaultValue = "0")
    val ignored: Boolean,
    @ColumnInfo(name = "transfer_id", index = true)
    val transferId: Uuid?,
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