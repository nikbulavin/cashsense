package ru.resodostudios.cashsense.feature.home.impl.model

import ru.resodostudios.cashsense.core.model.ExtendedUserWallet
import java.math.BigDecimal

data class UiWallet(
    val extendedUserWallet: ExtendedUserWallet,
    val expenses: BigDecimal,
    val income: BigDecimal,
)