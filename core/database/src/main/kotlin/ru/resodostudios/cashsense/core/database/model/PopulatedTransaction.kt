package ru.resodostudios.cashsense.core.database.model

import androidx.room3.Embedded
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
        parentColumns = ["category_id"],
        entityColumns = ["id"],
    )
    val category: CategoryEntity?,
)

fun PopulatedTransaction.asExternalModel(): Transaction {
    return transaction.asExternalModel().copy(
        currency = walletCurrency,
        category = category?.asExternalModel(),
    )
}
