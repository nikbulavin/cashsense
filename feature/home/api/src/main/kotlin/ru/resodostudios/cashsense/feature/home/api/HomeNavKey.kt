package ru.resodostudios.cashsense.feature.home.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class HomeNavKey(
    val walletId: String? = null,
) : NavKey