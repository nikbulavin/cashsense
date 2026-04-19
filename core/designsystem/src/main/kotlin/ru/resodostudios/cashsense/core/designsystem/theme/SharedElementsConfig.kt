package ru.resodostudios.cashsense.core.designsystem.theme

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.compositionLocalOf

data class SharedElementKey(
    val id: String,
    val origin: String,
    val type: SharedElementType,
)

enum class SharedElementType {
    BalanceAmount,
    ExpensesAmount,
    ExpensesTitle,
    IncomeAmount,
    IncomeTitle,
    WalletTitle,
    TransactionAmount,
    CategoryTitle,
    CategoryIcon,
}

val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope> {
    throw IllegalStateException("No SharedTransitionScope provided")
}