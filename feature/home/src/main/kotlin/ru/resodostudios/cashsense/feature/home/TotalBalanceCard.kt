package ru.resodostudios.cashsense.feature.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.component.CsListItem
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.AccountBalance
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.SentimentCalm
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.SentimentExcited
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.SentimentFrustrated
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.SentimentNeutral
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.SentimentSad
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
internal fun TotalBalanceCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    shape: Shape = MaterialTheme.shapes.extraLarge,
    headlineContent: @Composable () -> Unit,
    financialHealth: FinancialHealth? = null,
) {
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
            headlineContent = headlineContent,
            overlineContent = {
                Text(
                    text = stringResource(localesR.string.total_balance),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            trailingContent = financialHealth?.let {
                {
                    val (icon, color) = when (it) {
                        FinancialHealth.VERY_BAD -> CsIcons.Outlined.SentimentFrustrated to MaterialTheme.colorScheme.error
                        FinancialHealth.BAD -> CsIcons.Outlined.SentimentSad to MaterialTheme.colorScheme.error
                        FinancialHealth.NEUTRAL -> CsIcons.Outlined.SentimentNeutral to MaterialTheme.colorScheme.tertiary
                        FinancialHealth.GOOD -> CsIcons.Outlined.SentimentCalm to MaterialTheme.colorScheme.primary
                        FinancialHealth.VERY_GOOD -> CsIcons.Outlined.SentimentExcited to MaterialTheme.colorScheme.primary
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                    )
                }
            },
        )
    }
}

@Preview
@Composable
private fun TotalBalanceCardPreview() {
    CsTheme {
        Surface {
            Column {
                TotalBalanceCard(
                    headlineContent = {
                        Text(text = "$2,100")
                    },
                    modifier = Modifier.padding(16.dp),
                    financialHealth = FinancialHealth.VERY_GOOD,
                )
                TotalBalanceCard(
                    headlineContent = {
                        Text(text = "$2,100")
                    },
                    modifier = Modifier.padding(16.dp),
                    financialHealth = FinancialHealth.GOOD,
                )
                TotalBalanceCard(
                    headlineContent = {
                        Text(text = "$2,100")
                    },
                    modifier = Modifier.padding(16.dp),
                    financialHealth = FinancialHealth.NEUTRAL,
                )
                TotalBalanceCard(
                    headlineContent = {
                        Text(text = "$2,100")
                    },
                    modifier = Modifier.padding(16.dp),
                    financialHealth = FinancialHealth.BAD,
                )
                TotalBalanceCard(
                    headlineContent = {
                        Text(text = "$2,100")
                    },
                    modifier = Modifier.padding(16.dp),
                    financialHealth = FinancialHealth.VERY_BAD,
                )
            }
        }
    }
}