package ru.resodostudios.cashsense.feature.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateBounds
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Add
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.MoreVert
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.SendMoney
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.TrendingDown
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.TrendingUp
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.model.data.UserWallet
import ru.resodostudios.cashsense.core.ui.component.AnimatedAmount
import ru.resodostudios.cashsense.core.ui.util.formatAmount
import ru.resodostudios.cashsense.core.util.getUsdCurrency
import java.math.BigDecimal
import java.util.Currency
import ru.resodostudios.cashsense.core.locales.R as localesR

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WalletCard(
    userWallet: UserWallet,
    expenses: BigDecimal,
    income: BigDecimal,
    onWalletClick: (String) -> Unit,
    onNewTransactionClick: (String) -> Unit,
    onTransferClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
) {
    val border = if (selected) {
        BorderStroke(
            width = 2.dp,
            brush = SolidColor(MaterialTheme.colorScheme.outlineVariant),
        )
    } else {
        CardDefaults.outlinedCardBorder()
    }

    OutlinedCard(
        onClick = { onWalletClick(userWallet.id) },
        shape = RoundedCornerShape(24.dp),
        border = border,
        modifier = modifier,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(top = 12.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = userWallet.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleLarge,
            )
            AnimatedAmount(
                targetState = userWallet.currentBalance,
                label = "WalletBalance",
            ) {
                Text(
                    text = it.formatAmount(userWallet.currency),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            TagsSection(
                expenses = expenses,
                income = income,
                currency = userWallet.currency,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
        val addTransactionText = stringResource(localesR.string.add_transaction)
        val addTransferText = stringResource(localesR.string.transfer)
        ButtonGroup(
            modifier = Modifier.padding(start = 16.dp, end = 8.dp, bottom = 12.dp, top = 16.dp),
            overflowIndicator = { menuState ->
                IconButton(
                    onClick = {
                        if (menuState.isExpanded) {
                            menuState.dismiss()
                        } else {
                            menuState.show()
                        }
                    }
                ) {
                    Icon(
                        imageVector = CsIcons.Outlined.MoreVert,
                        contentDescription = stringResource(localesR.string.wallet_menu_icon_description),
                    )
                }
            }
        ) {
            customItem(
                buttonGroupContent = {
                    Button(
                        onClick = { onNewTransactionClick(userWallet.id) },
                        shapes = ButtonDefaults.shapes(),
                    ) {
                        Icon(
                            imageVector = CsIcons.Outlined.Add,
                            contentDescription = addTransactionText,
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(
                            text = addTransactionText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            ) { state ->
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(
                            imageVector = CsIcons.Outlined.Add,
                            contentDescription = null,
                        )
                    },
                    text = { Text(addTransactionText) },
                    onClick = {
                        onNewTransactionClick(userWallet.id)
                        state.dismiss()
                    }
                )
            }
            customItem(
                buttonGroupContent = {
                    OutlinedIconButton(
                        shapes = IconButtonDefaults.shapes(),
                        onClick = { onTransferClick(userWallet.id) },
                        border = IconButtonDefaults.outlinedIconButtonBorder(true).copy(
                            brush = SolidColor(MaterialTheme.colorScheme.outlineVariant),
                        ),
                    ) {
                        Icon(
                            imageVector = CsIcons.Outlined.SendMoney,
                            contentDescription = addTransferText,
                        )
                    }
                }
            ) { state ->
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(
                            imageVector = CsIcons.Outlined.Add,
                            contentDescription = null,
                        )
                    },
                    text = { Text(addTransferText) },
                    onClick = {
                        onTransferClick(userWallet.id)
                        state.dismiss()
                    }
                )
            }
        }
    }
}

@OptIn(
    ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
private fun TagsSection(
    expenses: BigDecimal,
    income: BigDecimal,
    currency: Currency,
    modifier: Modifier = Modifier,
) {
    LookaheadScope {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = modifier.animateContentSize(),
        ) {
            val animationSpec = MaterialTheme.motionScheme.fastSpatialSpec<Float>()
            AnimatedVisibility(
                visible = expenses.signum() > 0,
                enter = fadeIn() + scaleIn(animationSpec),
                exit = fadeOut() + scaleOut(animationSpec),
                modifier = Modifier.animateBounds(this@LookaheadScope),
            ) {
                CsAnimatedTag(
                    amount = expenses,
                    currency = currency,
                    color = MaterialTheme.colorScheme.errorContainer,
                    icon = CsIcons.Outlined.TrendingDown,
                )
            }
            AnimatedVisibility(
                visible = income.signum() > 0,
                enter = fadeIn() + scaleIn(animationSpec),
                exit = fadeOut() + scaleOut(animationSpec),
                modifier = Modifier.animateBounds(this@LookaheadScope),
            ) {
                CsAnimatedTag(
                    amount = income,
                    currency = currency,
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    icon = CsIcons.Outlined.TrendingUp,
                )
            }
        }
    }
}

@Composable
private fun CsAnimatedTag(
    amount: BigDecimal,
    currency: Currency,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.secondaryContainer,
    shape: Shape = RoundedCornerShape(16.dp),
    icon: ImageVector? = null,
) {
    Surface(
        color = color,
        shape = shape,
        modifier = modifier,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(
                start = 8.dp,
                top = 4.dp,
                end = 8.dp,
                bottom = 4.dp,
            )
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
            }
            AnimatedAmount(
                targetState = amount,
                label = "Tag",
            ) {
                Text(
                    text = it.formatAmount(currency),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
fun WalletCardPreview() {
    CsTheme {
        Surface {
            WalletCard(
                userWallet = UserWallet(
                    id = "",
                    title = "Debit",
                    initialBalance = BigDecimal(1499.99),
                    currency = getUsdCurrency(),
                    currentBalance = BigDecimal(2499.99),
                    isPrimary = true,
                ),
                expenses = BigDecimal(200),
                income = BigDecimal(800),
                onWalletClick = {},
                onNewTransactionClick = {},
                onTransferClick = { _ -> },
                modifier = Modifier.padding(16.dp).width(500.dp),
            )
        }
    }
}