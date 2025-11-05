package ru.resodostudios.cashsense.feature.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.component.CsListItem
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.AccountBalance
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
internal fun TotalBalanceCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    topPadding: Dp = 0.dp,
    headlineContent: @Composable () -> Unit,
) {
    val borderBrush = Brush.verticalGradient(
        colors = listOf(Color.Transparent, MaterialTheme.colorScheme.outlineVariant),
        startY = 200f,
    )
    OutlinedCard(
        shape = MaterialTheme.shapes.extraLarge.copy(
            topEnd = CornerSize(0.dp),
            topStart = CornerSize(0.dp),
        ),
        border = BorderStroke(1.dp, borderBrush),
        modifier = modifier,
        onClick = onClick,
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.25f),
        ),
    ) {
        Spacer(Modifier.height(topPadding))
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