package ru.resodostudios.cashsense.core.database.model

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import ru.resodostudios.cashsense.core.model.Wallet
import java.math.BigDecimal
import java.util.Currency

@Entity(
    tableName = "wallets",
)
data class WalletEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    @ColumnInfo(name = "initial_balance")
    val initialBalance: BigDecimal,
    val currency: Currency,
)

fun WalletEntity.asExternalModel(): Wallet {
    return Wallet(
        id = id,
        title = title,
        initialBalance = initialBalance,
        currency = currency,
    )
}
