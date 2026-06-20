package ru.resodostudios.cashsense.core.database.model

import androidx.room3.Embedded
import androidx.room3.Relation
import ru.resodostudios.cashsense.core.model.ExtendedWallet

data class PopulatedWallet(
    @Embedded
    val wallet: WalletEntity,
    @Relation(
        entity = TransactionEntity::class,
        parentColumns = ["id"],
        entityColumns = ["wallet_owner_id"],
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
