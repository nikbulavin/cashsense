package ru.resodostudios.cashsense.core.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import ru.resodostudios.cashsense.core.designsystem.component.CsSelectableListItem
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Block
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Pending
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.SendMoney
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.designsystem.theme.LocalSharedTransitionScope
import ru.resodostudios.cashsense.core.designsystem.theme.SharedElementKey
import ru.resodostudios.cashsense.core.designsystem.theme.sharedElementTransitionSpec
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.ui.util.formatAmount
import ru.resodostudios.cashsense.core.util.getUsdCurrency
import java.util.Currency
import kotlin.time.Instant
import ru.resodostudios.cashsense.core.locales.R as localesR

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun TransactionItem(
    selected: Boolean,
    transaction: Transaction,
    currency: Currency,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shapes: ListItemShapes = ListItemDefaults.shapes(),
) {
    val (categoryIcon, categoryTitle) = if (transaction.transferId != null) {
        CsIcons.Outlined.SendMoney to stringResource(localesR.string.transfers)
    } else {
        val iconId = transaction.category?.iconId ?: StoredIcon.TRANSACTION.storedId
        val title = transaction.category?.title ?: stringResource(localesR.string.uncategorized)
        StoredIcon.asImageVector(iconId) to title
    }

    val effectsSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Float>()
    val floatSpatialSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
    val intSizeSpatialSpec = MaterialTheme.motionScheme.defaultSpatialSpec<IntSize>()

    with(LocalSharedTransitionScope.current) {
        CsSelectableListItem(
            shapes = shapes,
            onClick = onClick,
            selected = selected,
            modifier = modifier,
            content = {
                val formattedAmount = transaction.amount.formatAmount(currency, true)
                Text(
                    text = formattedAmount,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.sharedBounds(
                        sharedContentState = rememberSharedContentState(
                            SharedElementKey.TransactionAmount(
                                transactionId = transaction.id,
                                amount = formattedAmount,
                            )
                        ),
                        animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                        resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(),
                        boundsTransform = MaterialTheme.motionScheme.sharedElementTransitionSpec,
                    ),
                )
            },
            supportingContent = {
                Text(
                    text = categoryTitle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.sharedBounds(
                        sharedContentState = rememberSharedContentState(
                            SharedElementKey.CategoryTitle(
                                transactionId = transaction.id,
                                title = categoryTitle,
                            )
                        ),
                        animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                        resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(),
                        boundsTransform = MaterialTheme.motionScheme.sharedElementTransitionSpec,
                    ),
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
                        visible = !transaction.completed,
                        enter = fadeIn(effectsSpec) + scaleIn(floatSpatialSpec),
                        exit = fadeOut(effectsSpec) + scaleOut(floatSpatialSpec),
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
                }
            },
            leadingContent = {
                Icon(
                    imageVector = categoryIcon,
                    contentDescription = null,
                    modifier = Modifier.sharedBounds(
                        sharedContentState = rememberSharedContentState(
                            SharedElementKey.CategoryIcon(transaction.id)
                        ),
                        animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                        boundsTransform = MaterialTheme.motionScheme.sharedElementTransitionSpec,
                    ),
                )
            },
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@PreviewLightDark
@Composable
private fun TransactionItemPreview() {
    CsTheme {
        Surface {
            TransactionItem(
                transaction = Transaction(
                    id = "1",
                    walletOwnerId = "1",
                    description = null,
                    amount = (-25).toBigDecimal(),
                    timestamp = Instant.parse("2024-09-13T14:20:00Z"),
                    completed = false,
                    ignored = true,
                    transferId = null,
                    currency = getUsdCurrency(),
                    category = Category(
                        id = "1",
                        title = "Fastfood",
                        iconId = StoredIcon.FASTFOOD.storedId,
                    ),
                ),
                currency = getUsdCurrency(),
                selected = false,
                onClick = {},
            )
        }
    }
}