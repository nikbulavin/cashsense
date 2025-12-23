@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class)

package ru.resodostudios.cashsense.core.designsystem.theme

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.compositionLocalOf

sealed interface SharedElementKey {
    data object Expenses : SharedElementKey
    data object Income : SharedElementKey
    data class WalletTitle(val walletId: String, val title: String) : SharedElementKey
    data class WalletBalance(val walletId: String, val balance: String) : SharedElementKey
}

val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope> {
    throw kotlin.IllegalStateException("No SharedTransitionScope provided")
}