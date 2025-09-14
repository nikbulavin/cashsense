package ru.resodostudios.cashsense.core.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.component.AnimatedIcon
import ru.resodostudios.cashsense.core.designsystem.component.CsListItemEmphasized
import ru.resodostudios.cashsense.core.designsystem.component.ListItemPositionShapes
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Block
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Delete
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Description
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Edit
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.KeyboardArrowDown
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.KeyboardArrowUp
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Pending
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Redo
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.SendMoney
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.DateFormatType
import ru.resodostudios.cashsense.core.model.data.StatusType.PENDING
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.ui.util.formatAmount
import ru.resodostudios.cashsense.core.ui.util.formatDate
import ru.resodostudios.cashsense.core.util.getUsdCurrency
import java.time.format.FormatStyle
import java.util.Currency
import kotlin.time.Instant
import ru.resodostudios.cashsense.core.locales.R as localesR

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun TransactionItem(
    transaction: Transaction,
    category: Category?,
    currency: Currency,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onRepeatClick: (String) -> Unit = {},
    onEditClick: (String) -> Unit = {},
    onDeleteClick: () -> Unit = {},
) {
    val (icon, categoryTitle) = if (transaction.transferId != null) {
        CsIcons.Outlined.SendMoney to stringResource(localesR.string.transfers)
    } else {
        val iconId = category?.iconId ?: StoredIcon.TRANSACTION.storedId
        val title = category?.title ?: stringResource(localesR.string.uncategorized)
        StoredIcon.asImageVector(iconId) to title
    }

    val effectsSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Float>()
    val floatSpatialSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
    val intSizeSpatialSpec = MaterialTheme.motionScheme.defaultSpatialSpec<IntSize>()


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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.animateContentSize(intSizeSpatialSpec),
                ) {
                    AnimatedVisibility(
                        visible = transaction.ignored,
                        enter = fadeIn(effectsSpec) + scaleIn(floatSpatialSpec),
                        exit = fadeOut(effectsSpec) + scaleOut(floatSpatialSpec),
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
                        enter = fadeIn() + scaleIn(floatSpatialSpec),
                        exit = fadeOut() + scaleOut(floatSpatialSpec),
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            shape = MaterialShapes.Clover4Leaf.toShape(),
                            modifier = Modifier.padding(start = 8.dp),
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
                    AnimatedVisibility(
                        visible = selected,
                        enter = fadeIn() + scaleIn(floatSpatialSpec),
                        exit = fadeOut() + scaleOut(floatSpatialSpec),
                    ) {
                        Text(
                            text = transaction.timestamp.formatDate(
                                DateFormatType.TIME,
                                FormatStyle.SHORT,
                            ),
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(start = 8.dp),
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
            colors = ListItemDefaults.colors().copy(
                containerColor = Color.Transparent,
            ),
        )
        AnimatedVisibility(
            visible = selected,
            enter = fadeIn(effectsSpec) + expandVertically(intSizeSpatialSpec),
            exit = fadeOut(effectsSpec) + shrinkVertically(intSizeSpatialSpec),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            ) {
                AnimatedVisibility(
                    visible = !transaction.description.isNullOrBlank(),
                    enter = fadeIn() + scaleIn(floatSpatialSpec),
                    exit = fadeOut() + scaleOut(floatSpatialSpec),
                ) {
                    var expanded by remember { mutableStateOf(false) }
                    DescriptionListItem(
                        expanded = expanded,
                        transaction = transaction,
                        modifier = Modifier
                            .padding(bottom = 6.dp)
                            .clip(ListItemPositionShapes.Single)
                            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                            .clickable { expanded = !expanded },
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                }
                val isTransfer = transaction.transferId != null
                if (!isTransfer) {
                    CsListItemEmphasized(
                        headlineContent = { Text(stringResource(localesR.string.repeat)) },
                        leadingContent = {
                            Icon(
                                imageVector = CsIcons.Outlined.Redo,
                                contentDescription = null,
                            )
                        },
                        onClick = { onRepeatClick(transaction.id) },
                        colors = ListItemDefaults.colors().copy(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                        ),
                        shape = ListItemPositionShapes.First,
                    )
                    CsListItemEmphasized(
                        headlineContent = { Text(stringResource(localesR.string.edit)) },
                        leadingContent = {
                            Icon(
                                imageVector = CsIcons.Outlined.Edit,
                                contentDescription = null,
                            )
                        },
                        onClick = { onEditClick(transaction.id) },
                        colors = ListItemDefaults.colors().copy(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                        ),
                        shape = ListItemPositionShapes.Middle,
                    )
                }
                CsListItemEmphasized(
                    headlineContent = { Text(stringResource(localesR.string.delete)) },
                    leadingContent = {
                        Icon(
                            imageVector = CsIcons.Outlined.Delete,
                            contentDescription = null,
                        )
                    },
                    onClick = onDeleteClick,
                    colors = ListItemDefaults.colors().copy(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    ),
                    shape = if (isTransfer) ListItemPositionShapes.Single else ListItemPositionShapes.Last,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun DescriptionListItem(
    expanded: Boolean,
    transaction: Transaction,
    modifier: Modifier = Modifier,
) {
    val effectsSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Float>()
    val intSizeSpatialSpec = MaterialTheme.motionScheme.defaultSpatialSpec<IntSize>()
    Column(
        modifier = modifier,
    ) {
        CsListItemEmphasized(
            headlineContent = { Text(stringResource(localesR.string.description)) },
            leadingContent = {
                Icon(
                    imageVector = CsIcons.Outlined.Description,
                    contentDescription = null,
                )
            },
            trailingContent = {
                AnimatedIcon(
                    icon = if (expanded) CsIcons.Outlined.KeyboardArrowUp else CsIcons.Outlined.KeyboardArrowDown,
                    contentDescription = stringResource(if (expanded) localesR.string.show_less else localesR.string.show_more),
                )
            },
            colors = ListItemDefaults.colors().copy(
                containerColor = Color.Transparent,
            ),
        )
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(effectsSpec) + expandVertically(intSizeSpatialSpec),
            exit = fadeOut(effectsSpec) + shrinkVertically(intSizeSpatialSpec),
        ) {
            Text(
                text = transaction.description.toString(),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
            )
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