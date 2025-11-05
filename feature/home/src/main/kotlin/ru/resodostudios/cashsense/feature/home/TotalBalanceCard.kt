package ru.resodostudios.cashsense.feature.home

import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import ru.resodostudios.cashsense.core.designsystem.component.CsListItem
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.AccountBalance
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
internal fun TotalBalanceCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    headlineContent: @Composable () -> Unit,
) {
    OutlinedCard(
        shape = MaterialTheme.shapes.extraLarge,
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
        )
    }
}