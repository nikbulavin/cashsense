@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class)

package ru.resodostudios.cashsense.core.designsystem.theme

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.compositionLocalOf

sealed interface SharedElementKey {
    object Expenses : SharedElementKey
    object Income : SharedElementKey
}

val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope> {
    throw kotlin.IllegalStateException("No SharedTransitionScope provided")
}