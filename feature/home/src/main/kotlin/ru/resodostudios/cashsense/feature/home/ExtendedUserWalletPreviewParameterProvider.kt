package ru.resodostudios.cashsense.feature.home

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import ru.resodostudios.cashsense.core.model.data.ExtendedUserWallet
import ru.resodostudios.cashsense.core.model.data.UserWallet
import ru.resodostudios.cashsense.core.util.getUsdCurrency
import java.math.BigDecimal

/**
 * This [PreviewParameterProvider](https://developer.android.com/reference/kotlin/androidx/compose/ui/tooling/preview/PreviewParameterProvider)
 * provides list of [ExtendedUserWallet] for Composable previews.
 */
class ExtendedUserWalletPreviewParameterProvider : PreviewParameterProvider<List<ExtendedUserWallet>> {

    override val values: Sequence<List<ExtendedUserWallet>>
        get() = sequenceOf(
            listOf(
                ExtendedUserWallet(
                    userWallet = UserWallet(
                        id = "1",
                        title = "Credit",
                        initialBalance = BigDecimal(0),
                        currentBalance = BigDecimal(1000),
                        currency = getUsdCurrency(),
                        isPrimary = true,
                    ),
                    transactions = emptyList(),
                ),
                ExtendedUserWallet(
                    userWallet = UserWallet(
                        id = "2",
                        title = "Debit",
                        initialBalance = BigDecimal(0),
                        currentBalance = BigDecimal(500),
                        currency = getUsdCurrency(),
                        isPrimary = false,
                    ),
                    transactions = emptyList(),
                ),
            )
        )
}
