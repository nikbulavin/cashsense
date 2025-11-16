package ru.resodostudios.cashsense.core.data.model

import ru.resodostudios.cashsense.core.database.model.TransactionCategoryCrossRefEntity
import ru.resodostudios.cashsense.core.model.data.TransactionCategoryCrossRef

fun TransactionCategoryCrossRef.asEntity(): TransactionCategoryCrossRefEntity {
    return TransactionCategoryCrossRefEntity(
        transactionId = transactionId,
        categoryId = categoryId,
    )
}