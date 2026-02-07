package ru.resodostudios.cashsense.core.designsystem.theme

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.compositionLocalOf

sealed interface SharedElementKey {
    data object Expenses : SharedElementKey
    data object Income : SharedElementKey
    data class WalletTitle(val walletId: String, val title: String) : SharedElementKey
    data class WalletBalance(val walletId: String, val balance: String) : SharedElementKey
    data class CategoryIcon(val transactionId: String) : SharedElementKey
    data class CategoryTitle(val transactionId: String, val title: String) : SharedElementKey
    data class TransactionAmount(val transactionId: String, val amount: String) : SharedElementKey
}

val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope> {
    throw IllegalStateException("No SharedTransitionScope provided")
}