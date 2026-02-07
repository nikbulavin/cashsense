package ru.resodostudios.cashsense.feature.home.impl

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateBounds
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import ru.resodostudios.cashsense.core.designsystem.component.CsTag
import ru.resodostudios.cashsense.core.designsystem.component.button.CsIconButton
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.filled.Star
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Add
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.MoreVert
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.SendMoney
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.TrendingDown
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.TrendingUp
import ru.resodostudios.cashsense.core.designsystem.theme.LocalSharedTransitionScope
import ru.resodostudios.cashsense.core.designsystem.theme.SharedElementKey
import ru.resodostudios.cashsense.core.designsystem.theme.WalletSharedElementType
import ru.resodostudios.cashsense.core.designsystem.theme.dropShadow
import ru.resodostudios.cashsense.core.designsystem.theme.sharedElementTransitionSpec
import ru.resodostudios.cashsense.core.model.data.ExtendedUserWallet
import ru.resodostudios.cashsense.core.model.data.Wallet
import ru.resodostudios.cashsense.core.ui.CsThemePreview
import ru.resodostudios.cashsense.core.ui.component.AnimatedAmount
import ru.resodostudios.cashsense.core.ui.util.formatAmount
import ru.resodostudios.cashsense.core.util.getUsdCurrency
import ru.resodostudios.cashsense.feature.home.impl.model.UiWallet
import java.math.BigDecimal
import ru.resodostudios.cashsense.core.locales.R as localesR

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun WalletCard(
    uiWallet: UiWallet,
    onWalletClick: (String) -> Unit,
    onNewTransactionClick: (String) -> Unit,
    onTransferClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    shape: Shape = MaterialTheme.shapes.extraLarge,
) {
    with(LocalSharedTransitionScope.current) {
        val wallet = uiWallet.extendedUserWallet.wallet
        OutlinedCard(
            onClick = { onWalletClick(wallet.id) },
            shape = shape,
            modifier = modifier.then(if (selected) Modifier.dropShadow(shape) else Modifier),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .padding(top = 12.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth(),
            ) {
                Text(
                    text = wallet.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.sharedBounds(
                        sharedContentState = rememberSharedContentState(
                            key = SharedElementKey.Wallet(
                                walletId = wallet.id,
                                type = WalletSharedElementType.Title,
                            ),
                        ),
                        animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                        resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(),
                        boundsTransform = MaterialTheme.motionScheme.sharedElementTransitionSpec,
                    ),
                )
                val balance = uiWallet.extendedUserWallet.currentBalance.formatAmount(wallet.currency)
                AnimatedAmount(
                    formattedAmount = balance,
                    label = "WalletBalance",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState(
                                key = SharedElementKey.Wallet(
                                    walletId = wallet.id,
                                    type = WalletSharedElementType.Balance,
                                ),
                            ),
                            animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                            resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(),
                            boundsTransform = MaterialTheme.motionScheme.sharedElementTransitionSpec,
                        ),
                )
                TagsSection(
                    formattedExpenses = uiWallet.expenses.formatAmount(wallet.currency),
                    formattedIncome = uiWallet.income.formatAmount(wallet.currency),
                    shouldShowExpensesTag = uiWallet.expenses.signum() > 0,
                    shouldShowIncomeTag = uiWallet.income.signum() > 0,
                    modifier = Modifier.padding(top = 8.dp),
                    isPrimary = uiWallet.extendedUserWallet.isPrimary,
                )
            }
            val addTransactionText = stringResource(localesR.string.add_transaction)
            val addTransferText = stringResource(localesR.string.transfer)
            ButtonGroup(
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 12.dp,
                    top = 16.dp,
                ),
                overflowIndicator = { menuState ->
                    CsIconButton(
                        onClick = { if (menuState.isShowing) menuState.dismiss() else menuState.show() },
                        icon = CsIcons.Outlined.MoreVert,
                        contentDescription = stringResource(localesR.string.wallet_menu_icon_description),
                    )
                },
            ) {
                customItem(
                    buttonGroupContent = {
                        Button(
                            onClick = { onNewTransactionClick(wallet.id) },
                            shapes = ButtonDefaults.shapes(),
                        ) {
                            Icon(
                                imageVector = CsIcons.Outlined.Add,
                                contentDescription = addTransactionText,
                                modifier = Modifier.size(ButtonDefaults.IconSize),
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
                            onNewTransactionClick(wallet.id)
                            state.dismiss()
                        },
                    )
                }
                customItem(
                    buttonGroupContent = {
                        OutlinedButton(
                            onClick = { onTransferClick(wallet.id) },
                            shapes = ButtonDefaults.shapes(),
                        ) {
                            Icon(
                                imageVector = CsIcons.Outlined.SendMoney,
                                contentDescription = addTransactionText,
                                modifier = Modifier.size(ButtonDefaults.IconSize),
                            )
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                            Text(
                                text = addTransferText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    },
                ) { state ->
                    DropdownMenuItem(
                        leadingIcon = {
                            Icon(
                                imageVector = CsIcons.Outlined.SendMoney,
                                contentDescription = null,
                            )
                        },
                        text = { Text(addTransferText) },
                        onClick = {
                            onTransferClick(wallet.id)
                            state.dismiss()
                        },
                    )
                }
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
    formattedExpenses: String,
    formattedIncome: String,
    shouldShowExpensesTag: Boolean,
    shouldShowIncomeTag: Boolean,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = false,
) {
    LookaheadScope {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = modifier.animateContentSize(),
        ) {
            val spatialSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
            val effectsSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Float>()
            AnimatedVisibility(
                visible = isPrimary,
                enter = fadeIn(effectsSpec) + scaleIn(spatialSpec),
                exit = fadeOut(effectsSpec) + scaleOut(spatialSpec),
                modifier = Modifier.animateBounds(this@LookaheadScope),
            ) {
                CsTag(
                    text = stringResource(localesR.string.primary),
                    icon = CsIcons.Filled.Star,
                )
            }
            AnimatedVisibility(
                visible = shouldShowExpensesTag,
                enter = fadeIn(effectsSpec) + scaleIn(spatialSpec),
                exit = fadeOut(effectsSpec) + scaleOut(spatialSpec),
                modifier = Modifier.animateBounds(this@LookaheadScope),
            ) {
                CsAnimatedTag(
                    formattedAmount = formattedExpenses,
                    color = MaterialTheme.colorScheme.errorContainer,
                    icon = CsIcons.Outlined.TrendingDown,
                )
            }
            AnimatedVisibility(
                visible = shouldShowIncomeTag,
                enter = fadeIn(effectsSpec) + scaleIn(spatialSpec),
                exit = fadeOut(effectsSpec) + scaleOut(spatialSpec),
                modifier = Modifier.animateBounds(this@LookaheadScope),
            ) {
                CsAnimatedTag(
                    formattedAmount = formattedIncome,
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    icon = CsIcons.Outlined.TrendingUp,
                )
            }
        }
    }
}

@Composable
private fun CsAnimatedTag(
    formattedAmount: String,
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
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
            }
            AnimatedAmount(
                formattedAmount = formattedAmount,
                label = "Tag",
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@PreviewLightDark
@Preview("Large font", fontScale = 2f)
@Composable
private fun WalletCardPreview() {
    CsThemePreview {
        Surface {
            WalletCard(
                uiWallet = UiWallet(
                    expenses = 200.toBigDecimal(),
                    income = 800.toBigDecimal(),
                    extendedUserWallet = ExtendedUserWallet(
                        wallet = Wallet(
                            id = "",
                            title = "Debit",
                            initialBalance = BigDecimal(1499.99),
                            currency = getUsdCurrency(),
                        ),
                        currentBalance = BigDecimal(1499.99),
                        isPrimary = true,
                        transactions = emptyList(),
                    )
                ),
                onWalletClick = {},
                onNewTransactionClick = {},
                onTransferClick = { _ -> },
                modifier = Modifier
                    .padding(16.dp)
                    .width(500.dp),
            )
        }
    }
}

@PreviewLightDark
@Preview("Large font", fontScale = 2f)
@Composable
private fun WalletCardSelectedPreview() {
    CsThemePreview {
        Surface {
            WalletCard(
                uiWallet = UiWallet(
                    expenses = 200.toBigDecimal(),
                    income = 800.toBigDecimal(),
                    extendedUserWallet = ExtendedUserWallet(
                        wallet = Wallet(
                            id = "",
                            title = "Debit",
                            initialBalance = BigDecimal(1499.99),
                            currency = getUsdCurrency(),
                        ),
                        currentBalance = BigDecimal(1499.99),
                        isPrimary = true,
                        transactions = emptyList(),
                    )
                ),
                onWalletClick = {},
                onNewTransactionClick = {},
                onTransferClick = { _ -> },
                modifier = Modifier
                    .padding(16.dp)
                    .width(500.dp),
                selected = true,
            )
        }
    }
}