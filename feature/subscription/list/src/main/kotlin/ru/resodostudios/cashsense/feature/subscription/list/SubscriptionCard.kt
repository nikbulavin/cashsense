package ru.resodostudios.cashsense.feature.subscription.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.component.CsTag
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Calendar
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.NotificationsActive
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.model.data.RepeatingIntervalType.DAILY
import ru.resodostudios.cashsense.core.model.data.RepeatingIntervalType.MONTHLY
import ru.resodostudios.cashsense.core.model.data.RepeatingIntervalType.WEEKLY
import ru.resodostudios.cashsense.core.model.data.RepeatingIntervalType.YEARLY
import ru.resodostudios.cashsense.core.model.data.Subscription
import ru.resodostudios.cashsense.core.model.data.getRepeatingIntervalType
import ru.resodostudios.cashsense.core.ui.util.formatAmount
import ru.resodostudios.cashsense.core.ui.util.formatDate
import ru.resodostudios.cashsense.core.util.getUsdCurrency
import java.math.BigDecimal
import kotlin.time.Clock
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
fun SubscriptionCard(
    subscription: Subscription,
    onClick: (Subscription) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        onClick = { onClick(subscription) },
        shape = RoundedCornerShape(24.dp),
        modifier = modifier,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .padding(top = 12.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = subscription.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = subscription.amount.formatAmount(subscription.currency),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(top = 12.dp),
            ) {
                CsTag(
                    text = subscription.paymentDate.formatDate(),
                    icon = CsIcons.Outlined.Calendar,
                )
                AnimatedVisibility(
                    visible = subscription.reminder != null,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut(),
                ) {
                    val repeatingIntervalType = subscription.reminder?.repeatingInterval?.let {
                        getRepeatingIntervalType(it)
                    }
                    val reminderTitle = when (repeatingIntervalType) {
                        DAILY -> stringResource(localesR.string.repeat_daily)
                        WEEKLY -> stringResource(localesR.string.repeat_weekly)
                        MONTHLY -> stringResource(localesR.string.repeat_monthly)
                        YEARLY -> stringResource(localesR.string.repeat_yearly)
                        else -> stringResource(localesR.string.reminder)
                    }
                    CsTag(
                        text = reminderTitle,
                        icon = CsIcons.Outlined.NotificationsActive,
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
fun SubscriptionCardPreview() {
    CsTheme {
        Surface {
            SubscriptionCard(
                subscription = Subscription(
                    id = "",
                    title = "Spotify Premium",
                    amount = BigDecimal(14.99),
                    currency = getUsdCurrency(),
                    paymentDate = Clock.System.now(),
                    reminder = null,
                ),
                onClick = {},
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}