package ru.resodostudios.cashsense.core.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.StatusType
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.model.data.TransactionWithCategory
import ru.resodostudios.cashsense.core.ui.component.StoredIcon
import ru.resodostudios.cashsense.core.util.getUsdCurrency
import kotlin.time.Instant

/**
 * This [PreviewParameterProvider](https://developer.android.com/reference/kotlin/androidx/compose/ui/tooling/preview/PreviewParameterProvider)
 * provides list of [TransactionWithCategory] for Composable previews.
 */
class TransactionCategoryPreviewParameterProvider : PreviewParameterProvider<List<TransactionWithCategory>> {

    override val values: Sequence<List<TransactionWithCategory>>
        get() = sequenceOf(
            listOf(
                TransactionWithCategory(
                    transaction = Transaction(
                        id = "1",
                        walletOwnerId = "1",
                        description = null,
                        amount = (-25).toBigDecimal(),
                        timestamp = Instant.parse("2024-09-13T14:20:00Z"),
                        status = StatusType.PENDING,
                        ignored = false,
                        transferId = null,
                        currency = getUsdCurrency(),
                    ),
                    category = Category(
                        id = "1",
                        title = "Fastfood",
                        iconId = StoredIcon.FASTFOOD.storedId,
                    ),
                ),
                TransactionWithCategory(
                    transaction = Transaction(
                        id = "2",
                        walletOwnerId = "1",
                        description = null,
                        amount = 1000.toBigDecimal(),
                        timestamp = Instant.parse("2024-08-13T14:20:00Z"),
                        status = StatusType.PENDING,
                        ignored = false,
                        transferId = null,
                        currency = getUsdCurrency(),
                    ),
                    category = Category(
                        id = "2",
                        title = "Salary",
                        iconId = StoredIcon.PAYMENTS.storedId,
                    ),
                ),
                TransactionWithCategory(
                    transaction = Transaction(
                        id = "1",
                        walletOwnerId = "1",
                        description = null,
                        amount = (-50).toBigDecimal(),
                        timestamp = Instant.DISTANT_PAST,
                        status = StatusType.PENDING,
                        ignored = false,
                        transferId = null,
                        currency = getUsdCurrency(),
                    ),
                    category = Category(
                        id = "10",
                        title = "Gas",
                        iconId = 10,
                    ),
                ),
                TransactionWithCategory(
                    transaction = Transaction(
                        id = "1",
                        walletOwnerId = "1",
                        description = null,
                        amount = (-50).toBigDecimal(),
                        timestamp = Instant.DISTANT_PAST,
                        status = StatusType.PENDING,
                        ignored = false,
                        transferId = null,
                        currency = getUsdCurrency(),
                    ),
                    category = Category(
                        id = "10",
                        title = "Gas",
                        iconId = 10,
                    ),
                ),
                TransactionWithCategory(
                    transaction = Transaction(
                        id = "1",
                        walletOwnerId = "1",
                        description = null,
                        amount = (-50).toBigDecimal(),
                        timestamp = Instant.DISTANT_PAST,
                        status = StatusType.PENDING,
                        ignored = false,
                        transferId = null,
                        currency = getUsdCurrency(),
                    ),
                    category = Category(
                        id = "1",
                        title = "Fastfood",
                        iconId = StoredIcon.FASTFOOD.storedId,
                    ),
                ),
                TransactionWithCategory(
                    transaction = Transaction(
                        id = "1",
                        walletOwnerId = "1",
                        description = null,
                        amount = (-50).toBigDecimal(),
                        timestamp = Instant.DISTANT_PAST,
                        status = StatusType.PENDING,
                        ignored = false,
                        transferId = null,
                        currency = getUsdCurrency(),
                    ),
                    category = Category(
                        id = "8",
                        title = "Internet",
                        iconId = 8,
                    ),
                ),
                TransactionWithCategory(
                    transaction = Transaction(
                        id = "1",
                        walletOwnerId = "1",
                        description = null,
                        amount = (-150).toBigDecimal(),
                        timestamp = Instant.DISTANT_PAST,
                        status = StatusType.PENDING,
                        ignored = false,
                        transferId = null,
                        currency = getUsdCurrency(),
                    ),
                    category = Category(
                        id = "8",
                        title = "Internet",
                        iconId = 8,
                    ),
                ),
                TransactionWithCategory(
                    transaction = Transaction(
                        id = "1",
                        walletOwnerId = "1",
                        description = null,
                        amount = 100.toBigDecimal(),
                        timestamp = Instant.DISTANT_PAST,
                        status = StatusType.PENDING,
                        ignored = false,
                        transferId = null,
                        currency = getUsdCurrency(),
                    ),
                    category = Category(
                        id = "1",
                        title = "Fastfood",
                        iconId = StoredIcon.FASTFOOD.storedId,
                    ),
                ),
                TransactionWithCategory(
                    transaction = Transaction(
                        id = "1",
                        walletOwnerId = "1",
                        description = null,
                        amount = (-175).toBigDecimal(),
                        timestamp = Instant.DISTANT_PAST,
                        status = StatusType.PENDING,
                        ignored = false,
                        transferId = null,
                        currency = getUsdCurrency(),
                    ),
                    category = Category(
                        id = "11",
                        title = "Electronics",
                        iconId = 11,
                    ),
                ),
                TransactionWithCategory(
                    transaction = Transaction(
                        id = "1",
                        walletOwnerId = "1",
                        description = null,
                        amount = (-150).toBigDecimal(),
                        timestamp = Instant.DISTANT_PAST,
                        status = StatusType.PENDING,
                        ignored = false,
                        transferId = null,
                        currency = getUsdCurrency(),
                    ),
                    category = Category(
                        id = "11",
                        title = "Electronics",
                        iconId = 11,
                    ),
                ),
            ),
        )
}