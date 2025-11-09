package ru.resodostudios.cashsense.core.database.model

import androidx.room.Embedded
import androidx.room.Relation
import ru.resodostudios.cashsense.core.model.data.ExtendedWallet
import ru.resodostudios.cashsense.core.model.data.TransactionWithCategory

data class PopulatedWallet(
    @Embedded
    val wallet: WalletEntity,
    @Relation(
        entity = TransactionEntity::class,
        parentColumn = "id",
        entityColumn = "wallet_owner_id",
    )
    val transactions: List<PopulatedTransaction>,
)

fun PopulatedWallet.asExternalModel() = ExtendedWallet(
    wallet = wallet.asExternalModel(),
    transactionsWithCategories = transactions.map {
        TransactionWithCategory(
            transaction = it.transaction.asExternalModel(),
            category = it.category?.asExternalModel(),
        )
    }
)