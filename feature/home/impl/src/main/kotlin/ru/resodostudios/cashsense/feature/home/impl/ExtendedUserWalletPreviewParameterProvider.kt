package ru.resodostudios.cashsense.feature.home.impl

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import ru.resodostudios.cashsense.core.model.data.ExtendedUserWallet
import ru.resodostudios.cashsense.core.model.data.Wallet
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
                    wallet = Wallet(
                        id = "1",
                        title = "Credit",
                        initialBalance = BigDecimal(0),
                        currency = getUsdCurrency(),
                    ),
                    transactions = emptyList(),
                    currentBalance = BigDecimal(1000),
                    isPrimary = true,
                ),
                ExtendedUserWallet(
                    wallet = Wallet(
                        id = "2",
                        title = "Debit",
                        initialBalance = BigDecimal(0),
                        currency = getUsdCurrency(),
                    ),
                    transactions = emptyList(),
                    currentBalance = BigDecimal(500),
                    isPrimary = false,
                ),
            )
        )
}
