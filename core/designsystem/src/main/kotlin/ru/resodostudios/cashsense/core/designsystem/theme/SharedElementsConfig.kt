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
    Bounds,
    CategoryIcon,
    CategoryTitle,
    ExpensesAmount,
    ExpensesTitle,
    IncomeAmount,
    IncomeTitle,
    TransactionAmount,
    WalletTitle,
}

val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope> {
    throw IllegalStateException("No SharedTransitionScope provided")
}