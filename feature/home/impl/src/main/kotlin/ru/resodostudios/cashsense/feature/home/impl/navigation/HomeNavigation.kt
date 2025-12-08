package ru.resodostudios.cashsense.feature.home.impl.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import kotlinx.serialization.Serializable

@Serializable
data class HomeRoute(
    val walletId: String? = null,
)

fun NavController.navigateToHome(
    walletId: String? = null,
    navOptions: NavOptions? = null,
) = navigate(
    route = HomeRoute(walletId),
    navOptions = navOptions,
)