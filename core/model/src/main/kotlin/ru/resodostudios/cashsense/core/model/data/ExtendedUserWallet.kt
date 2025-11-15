package ru.resodostudios.cashsense.core.model.data

data class ExtendedUserWallet(
    val userWallet: UserWallet,
    val transactions: List<Transaction>,
)