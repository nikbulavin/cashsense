package ru.resodostudios.cashsense.core.database.model

import androidx.room.Embedded
import androidx.room.Relation
import ru.resodostudios.cashsense.core.model.data.ExtendedWallet

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

fun PopulatedWallet.asExternalModel(): ExtendedWallet {
    val transactions = transactions.map(PopulatedTransaction::asExternalModel)
    val currentBalance = transactions.sumOf { it.amount } + wallet.initialBalance
    return ExtendedWallet(
        wallet = wallet.asExternalModel(),
        transactions = transactions,
        currentBalance = currentBalance,
    )
}