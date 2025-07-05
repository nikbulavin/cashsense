package ru.resodostudios.cashsense.core.model.data

data class UserData(
    val darkThemeConfig: DarkThemeConfig,
    val useDynamicColor: Boolean,
    val primaryWalletId: String,
    val currency: String,
    val shouldShowTotalBalance: Boolean,
)