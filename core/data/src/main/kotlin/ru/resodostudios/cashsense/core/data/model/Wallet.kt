package ru.resodostudios.cashsense.core.data.model

import ru.resodostudios.cashsense.core.database.model.WalletEntity
import ru.resodostudios.cashsense.core.model.data.Wallet

fun Wallet.asEntity(): WalletEntity {
    return WalletEntity(
        id = id,
        title = title,
        initialBalance = initialBalance,
        currency = currency,
    )
}