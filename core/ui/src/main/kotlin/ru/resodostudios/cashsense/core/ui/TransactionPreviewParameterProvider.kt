package ru.resodostudios.cashsense.core.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.ui.component.StoredIcon
import ru.resodostudios.cashsense.core.util.getUsdCurrency
import kotlin.time.Instant

/**
 * This [PreviewParameterProvider](https://developer.android.com/reference/kotlin/androidx/compose/ui/tooling/preview/PreviewParameterProvider)
 * provides list of [Transaction] for Composable previews.
 */
class TransactionPreviewParameterProvider : PreviewParameterProvider<List<Transaction>> {

    override val values: Sequence<List<Transaction>>
        get() = sequenceOf(
            listOf(
                Transaction(
                    id = "1",
                    walletOwnerId = "1",
                    description = null,
                    amount = (-25).toBigDecimal(),
                    timestamp = Instant.parse("2025-09-13T14:20:00Z"),
                    completed = true,
                    ignored = true,
                    transferId = null,
                    currency = getUsdCurrency(),
                    category = Category(
                        id = "1",
                        title = "Fastfood",
                        iconId = StoredIcon.FASTFOOD.storedId,
                    ),
                ),
                Transaction(
                    id = "2",
                    walletOwnerId = "1",
                    description = null,
                    amount = 1000.toBigDecimal(),
                    timestamp = Instant.parse("2025-08-13T14:20:00Z"),
                    completed = true,
                    ignored = false,
                    transferId = null,
                    currency = getUsdCurrency(),
                    category = Category(
                        id = "2",
                        title = "Salary",
                        iconId = StoredIcon.PAYMENTS.storedId,
                    ),
                ),
                Transaction(
                    id = "3",
                    walletOwnerId = "1",
                    description = null,
                    amount = (-50).toBigDecimal(),
                    timestamp = Instant.DISTANT_PAST,
                    completed = false,
                    ignored = false,
                    transferId = null,
                    currency = getUsdCurrency(),
                    category = Category(
                        id = "10",
                        title = "Gas",
                        iconId = 10,
                    ),
                ),
                Transaction(
                    id = "4",
                    walletOwnerId = "1",
                    description = null,
                    amount = (-50).toBigDecimal(),
                    timestamp = Instant.parse("2024-08-13T14:20:00Z"),
                    completed = true,
                    ignored = false,
                    transferId = null,
                    currency = getUsdCurrency(),
                    category = Category(
                        id = "10",
                        title = "Gas",
                        iconId = 10,
                    ),
                ),
                Transaction(
                    id = "5",
                    walletOwnerId = "1",
                    description = null,
                    amount = (-50).toBigDecimal(),
                    timestamp = Instant.parse("2024-08-13T14:20:00Z"),
                    completed = true,
                    ignored = false,
                    transferId = null,
                    currency = getUsdCurrency(),
                    category = Category(
                        id = "1",
                        title = "Fastfood",
                        iconId = StoredIcon.FASTFOOD.storedId,
                    ),
                ),
                Transaction(
                    id = "6",
                    walletOwnerId = "1",
                    description = null,
                    amount = (-50).toBigDecimal(),
                    timestamp = Instant.parse("2024-08-13T14:20:00Z"),
                    completed = false,
                    ignored = false,
                    transferId = null,
                    currency = getUsdCurrency(),
                    category = Category(
                        id = "8",
                        title = "Internet",
                        iconId = 8,
                    ),
                ),
                Transaction(
                    id = "7",
                    walletOwnerId = "1",
                    description = null,
                    amount = (-150).toBigDecimal(),
                    timestamp = Instant.parse("2024-08-13T14:20:00Z"),
                    completed = true,
                    ignored = false,
                    transferId = null,
                    currency = getUsdCurrency(),
                    category = Category(
                        id = "8",
                        title = "Internet",
                        iconId = 8,
                    ),
                ),
                Transaction(
                    id = "8",
                    walletOwnerId = "1",
                    description = null,
                    amount = 100.toBigDecimal(),
                    timestamp = Instant.parse("2024-08-13T14:20:00Z"),
                    completed = false,
                    ignored = false,
                    transferId = null,
                    currency = getUsdCurrency(),
                    category = Category(
                        id = "1",
                        title = "Fastfood",
                        iconId = StoredIcon.FASTFOOD.storedId,
                    ),
                ),
                Transaction(
                    id = "9",
                    walletOwnerId = "1",
                    description = null,
                    amount = (-175).toBigDecimal(),
                    timestamp = Instant.parse("2024-08-13T14:20:00Z"),
                    completed = false,
                    ignored = false,
                    transferId = null,
                    currency = getUsdCurrency(),
                    category = Category(
                        id = "11",
                        title = "Electronics",
                        iconId = 11,
                    ),
                ),
                Transaction(
                    id = "10",
                    walletOwnerId = "1",
                    description = null,
                    amount = (-150).toBigDecimal(),
                    timestamp = Instant.parse("2024-08-13T14:20:00Z"),
                    completed = false,
                    ignored = false,
                    transferId = null,
                    currency = getUsdCurrency(),
                    category = Category(
                        id = "11",
                        title = "Electronics",
                        iconId = 11,
                    ),
                ),
            ),
        )
}