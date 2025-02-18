package ru.resodostudios.cashsense.feature.subscription.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.component.CsListItem
import ru.resodostudios.cashsense.core.designsystem.component.CsModalBottomSheet
import ru.resodostudios.cashsense.core.designsystem.component.CsTag
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Autorenew
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Calendar
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Delete
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Edit
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.NotificationsActive
import ru.resodostudios.cashsense.core.model.data.RepeatingIntervalType.DAILY
import ru.resodostudios.cashsense.core.model.data.RepeatingIntervalType.MONTHLY
import ru.resodostudios.cashsense.core.model.data.RepeatingIntervalType.WEEKLY
import ru.resodostudios.cashsense.core.model.data.RepeatingIntervalType.YEARLY
import ru.resodostudios.cashsense.core.model.data.Subscription
import ru.resodostudios.cashsense.core.model.data.getRepeatingIntervalType
import ru.resodostudios.cashsense.core.ui.util.formatAmount
import ru.resodostudios.cashsense.core.ui.util.formatDate
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
internal fun SubscriptionBottomSheet(
    subscription: Subscription,
    onDismiss: () -> Unit,
    onEdit: (String) -> Unit,
    onDelete: () -> Unit,
) {
    CsModalBottomSheet(onDismiss) {
        Column {
            CsListItem(
                headlineContent = {
                    Text(
                        text = subscription.title,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                },
                leadingContent = {
                    Icon(
                        imageVector = CsIcons.Outlined.Autorenew,
                        contentDescription = null,
                    )
                },
                supportingContent = {
                    Text(
                        text = subscription.amount.formatAmount(subscription.currency),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                }
            )
            FlowRow(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CsTag(
                    text = subscription.paymentDate.formatDate(),
                    icon = CsIcons.Outlined.Calendar,
                )
                AnimatedVisibility(subscription.reminder != null) {
                    val repeatingIntervalType = getRepeatingIntervalType(subscription.reminder?.repeatingInterval)
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
            HorizontalDivider(Modifier.padding(16.dp))
            CsListItem(
                headlineContent = { Text(stringResource(localesR.string.edit)) },
                leadingContent = {
                    Icon(
                        imageVector = CsIcons.Outlined.Edit,
                        contentDescription = null,
                    )
                },
                onClick = {
                    onDismiss()
                    onEdit(subscription.id)
                },
            )
            CsListItem(
                headlineContent = { Text(stringResource(localesR.string.delete)) },
                leadingContent = {
                    Icon(
                        imageVector = CsIcons.Outlined.Delete,
                        contentDescription = null,
                    )
                },
                onClick = {
                    onDismiss()
                    onDelete()
                },
            )
        }
    }
}