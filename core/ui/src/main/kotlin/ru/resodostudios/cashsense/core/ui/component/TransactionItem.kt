package ru.resodostudios.cashsense.core.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.component.CsListItemEmphasized
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Block
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Delete
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Pending
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.SendMoney
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.StatusType.PENDING
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.ui.util.formatAmount
import ru.resodostudios.cashsense.core.util.getUsdCurrency
import java.util.Currency
import kotlin.time.Instant
import ru.resodostudios.cashsense.core.locales.R as localesR

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun TransactionItem(
    transaction: Transaction,
    category: Category?,
    currency: Currency,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onDelete: () -> Unit = {},
) {
    val (icon, categoryTitle) = if (transaction.transferId != null) {
        CsIcons.Outlined.SendMoney to stringResource(localesR.string.transfers)
    } else {
        val iconId = category?.iconId ?: StoredIcon.TRANSACTION.storedId
        val title = category?.title ?: stringResource(localesR.string.uncategorized)
        StoredIcon.asImageVector(iconId) to title
    }

    Column(modifier = modifier) {
        CsListItemEmphasized(
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
                val animationSpec = MaterialTheme.motionScheme.fastSpatialSpec<Float>()
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    AnimatedVisibility(
                        visible = transaction.ignored,
                        enter = fadeIn() + scaleIn(animationSpec),
                        exit = fadeOut() + scaleOut(animationSpec),
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = MaterialShapes.PixelCircle.toShape(),
                        ) {
                            Icon(
                                imageVector = CsIcons.Outlined.Block,
                                contentDescription = stringResource(localesR.string.transaction_ignore),
                                modifier = Modifier
                                    .padding(4.dp)
                                    .size(20.dp),
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                            )
                        }
                    }
                    AnimatedVisibility(
                        visible = transaction.status == PENDING,
                        enter = fadeIn() + scaleIn(animationSpec),
                        exit = fadeOut() + scaleOut(animationSpec),
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            shape = MaterialShapes.Clover4Leaf.toShape(),
                        ) {
                            Icon(
                                imageVector = CsIcons.Outlined.Pending,
                                contentDescription = stringResource(localesR.string.pending),
                                modifier = Modifier
                                    .padding(4.dp)
                                    .size(20.dp),
                                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                            )
                        }
                    }
                }
            },
            leadingContent = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                )
            },
            colors = ListItemDefaults.colors().copy(
                containerColor = Color.Transparent,
            ),
        )
        AnimatedVisibility(selected) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.padding(16.dp),
            ) {
                CsListItemEmphasized(
                    headlineContent = { Text(stringResource(localesR.string.delete)) },
                    leadingContent = {
                        Icon(
                            imageVector = CsIcons.Outlined.Delete,
                            contentDescription = null,
                        )
                    },
                    onClick = onDelete,
                    colors = ListItemDefaults.colors().copy(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
fun TransactionItemPreview() {
    CsTheme {
        Surface {
            TransactionItem(
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
                currency = getUsdCurrency(),
            )
        }
    }
}