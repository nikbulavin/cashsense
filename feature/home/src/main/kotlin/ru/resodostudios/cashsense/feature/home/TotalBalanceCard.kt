package ru.resodostudios.cashsense.feature.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import ru.resodostudios.cashsense.core.designsystem.component.CsListItem
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.AccountBalance
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.SentimentCalm
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.SentimentExcited
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.SentimentFrustrated
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.SentimentNeutral
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.SentimentSad
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.ui.component.AnimatedAmount
import ru.resodostudios.cashsense.core.util.getUsdCurrency
import java.math.BigDecimal
import ru.resodostudios.cashsense.core.locales.R as localesR

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun TotalBalanceCard(
    totalBalanceState: TotalBalanceUiState,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    shape: Shape = MaterialTheme.shapes.extraLarge,
) {
    when (totalBalanceState) {
        TotalBalanceUiState.NotShown -> Unit
        TotalBalanceUiState.Loading, is TotalBalanceUiState.Shown -> {
            Card(
                shape = shape,
                modifier = modifier,
                onClick = onClick,
                colors = CardDefaults.cardColors().copy(
                    containerColor = Color.Transparent,
                ),
            ) {
                CsListItem(
                    leadingContent = {
                        Icon(
                            imageVector = CsIcons.Outlined.AccountBalance,
                            contentDescription = null,
                        )
                    },
                    headlineContent = {
                        AnimatedContent(
                            targetState = totalBalanceState,
                            label = "TotalBalanceState",
                            modifier = Modifier.zIndex(1f),
                        ) { state ->
                            if (state is TotalBalanceUiState.Shown) {
                                AnimatedAmount(
                                    amount = state.amount,
                                    currency = state.userCurrency,
                                    label = "TotalBalanceAnimatedAmount",
                                    withApproximatelySign = state.shouldShowApproximately,
                                )
                            } else {
                                LinearWavyProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp, bottom = 6.dp),
                                )
                            }
                        }
                    },
                    overlineContent = {
                        Text(
                            text = stringResource(localesR.string.total_balance),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    trailingContent = (totalBalanceState as? TotalBalanceUiState.Shown)?.financialHealth?.let {
                        {
                            val (icon, color) = when (it) {
                                FinancialHealth.VERY_BAD -> CsIcons.Outlined.SentimentFrustrated to MaterialTheme.colorScheme.errorContainer
                                FinancialHealth.BAD -> CsIcons.Outlined.SentimentSad to MaterialTheme.colorScheme.errorContainer
                                FinancialHealth.NEUTRAL -> CsIcons.Outlined.SentimentNeutral to MaterialTheme.colorScheme.surfaceVariant
                                FinancialHealth.GOOD -> CsIcons.Outlined.SentimentCalm to MaterialTheme.colorScheme.primaryContainer
                                FinancialHealth.VERY_GOOD -> CsIcons.Outlined.SentimentExcited to MaterialTheme.colorScheme.primaryContainer
                            }
                            Surface(
                                shape = CircleShape,
                                color = color,
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .padding(4.dp),
                                )
                            }
                        }
                    },
                )
            }
        }
    }
}

@Preview
@Composable
private fun TotalBalanceCardPreview() {
    CsTheme {
        Surface {
            TotalBalanceCard(
                totalBalanceState = TotalBalanceUiState.Shown(
                    amount = BigDecimal(2200),
                    userCurrency = getUsdCurrency(),
                    financialHealth = FinancialHealth.VERY_GOOD,
                    shouldShowApproximately = true,
                ),
            )
        }
    }
}

@Preview
@Composable
private fun TotalBalanceCardLoadingPreview() {
    CsTheme {
        Surface {
            TotalBalanceCard(
                totalBalanceState = TotalBalanceUiState.Loading,
            )
        }
    }
}