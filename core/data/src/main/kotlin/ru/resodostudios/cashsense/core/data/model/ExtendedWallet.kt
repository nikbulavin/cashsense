package ru.resodostudios.cashsense.core.data.model

import ru.resodostudios.cashsense.core.database.model.PopulatedTransaction
import ru.resodostudios.cashsense.core.database.model.PopulatedWallet
import ru.resodostudios.cashsense.core.model.data.ExtendedWallet

fun ExtendedWallet.asEntity(): PopulatedWallet {
    return PopulatedWallet(
        wallet = wallet.asEntity(),
        transactions = transactions.map {
            PopulatedTransaction(
                transaction = it.asEntity(),
                category = it.category?.asEntity(),
            )
        }
    )
}