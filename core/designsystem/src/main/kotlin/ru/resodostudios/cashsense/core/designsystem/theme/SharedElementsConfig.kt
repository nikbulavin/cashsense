package ru.resodostudios.cashsense.core.designsystem.theme

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.compositionLocalOf

sealed interface SharedElementKey {
    data class Wallet(val walletId: String, val type: WalletSharedElementType)
    data class CategoryIcon(val transactionId: String) : SharedElementKey
    data class CategoryTitle(val transactionId: String, val title: String) : SharedElementKey
    data class TransactionAmount(val transactionId: String, val amount: String) : SharedElementKey
}

enum class WalletSharedElementType {
    Balance,
    Expenses,
    ExpensesTitle,
    Income,
    IncomeTitle,
    Title,
}

val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope> {
    throw IllegalStateException("No SharedTransitionScope provided")
}