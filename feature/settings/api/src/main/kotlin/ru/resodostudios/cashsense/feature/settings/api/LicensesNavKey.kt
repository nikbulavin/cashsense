package ru.resodostudios.cashsense.feature.settings.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import ru.resodostudios.core.navigation.Navigator

@Serializable
data object LicensesNavKey : NavKey

fun Navigator.navigateToLicenses() = navigate(LicensesNavKey)