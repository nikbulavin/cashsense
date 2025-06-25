package ru.resodostudios.cashsense.core.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.component.CsListItem
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.SendMoney
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.StatusType.PENDING
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.model.data.TransactionWithCategory
import ru.resodostudios.cashsense.core.ui.util.formatAmount
import ru.resodostudios.cashsense.core.util.getUsdCurrency
import java.util.Currency
import kotlin.time.Instant
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
internal fun TransactionItem(
    transactionCategory: TransactionWithCategory,
    currency: Currency,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val transaction = transactionCategory.transaction
    val category = transactionCategory.category
    val (icon, categoryTitle) = if (transaction.transferId != null) {
        CsIcons.Outlined.SendMoney to stringResource(localesR.string.transfers)
    } else {
        val iconId = category?.iconId ?: StoredIcon.TRANSACTION.storedId
        val title = category?.title ?: stringResource(localesR.string.uncategorized)
        StoredIcon.asImageVector(iconId) to title
    }

    CsListItem(
        headlineContent = {
            Text(
                text = transaction.amount.formatAmount(currency, true),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        supportingContent = {
            Text(
                text = categoryTitle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        trailingContent = {
            AnimatedVisibility(
                visible = transaction.status == PENDING,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Text(
                        text = stringResource(localesR.string.pending),
                        modifier = Modifier.padding(
                            start = 6.dp,
                            top = 3.dp,
                            end = 6.dp,
                            bottom = 3.dp,
                        ),
                    )
                }
            }
        },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
            )
        },
        modifier = modifier.alpha(if (transaction.ignored) 0.38f else 1f),
        onClick = { onClick(transaction.id) },
    )
}

@Preview
@Composable
fun TransactionItemPreview() {
    CsTheme {
        Surface {
            TransactionItem(
                transactionCategory = TransactionWithCategory(
                    transaction = Transaction(
                        id = "1",
                        walletOwnerId = "1",
                        description = null,
                        amount = (-25).toBigDecimal(),
                        timestamp = Instant.parse("2024-09-13T14:20:00Z"),
                        status = PENDING,
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
                currency = getUsdCurrency(),
                onClick = {},
            )
        }
    }
}

@Preview
@Composable
fun TransactionItemIgnoredPreview() {
    CsTheme {
        Surface {
            TransactionItem(
                transactionCategory = TransactionWithCategory(
                    transaction = Transaction(
                        id = "1",
                        walletOwnerId = "1",
                        description = null,
                        amount = (-25).toBigDecimal(),
                        timestamp = Instant.parse("2024-09-13T14:20:00Z"),
                        status = PENDING,
                        ignored = true,
                        transferId = null,
                        currency = getUsdCurrency(),
                    ),
                    category = Category(
                        id = "1",
                        title = "Fastfood",
                        iconId = StoredIcon.FASTFOOD.storedId,
                    ),
                ),
                currency = getUsdCurrency(),
                onClick = {},
            )
        }
    }
}