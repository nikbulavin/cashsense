package ru.resodostudios.cashsense.feature.subscription.list.impl

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.component.CsAlertDialog
import ru.resodostudios.cashsense.core.designsystem.component.CsListItem
import ru.resodostudios.cashsense.core.designsystem.component.CsTag
import ru.resodostudios.cashsense.core.designsystem.component.button.CsIconButton
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Autorenew
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Calendar
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Delete
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Edit
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.MoreVert
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
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.extraLarge,
    onEditClick: (String) -> Unit = {},
    onDeleteClick: (String) -> Unit = {},
) {
    var shouldShowDeletionDialog by rememberSaveable { mutableStateOf(false) }
    OutlinedCard(
        shape = shape,
        modifier = modifier,
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(
                    top = 12.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                ),
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
            DropdownMenu(
                onEditClick = { onEditClick(subscription.id) },
                onDeleteClick = { shouldShowDeletionDialog = true },
                modifier = Modifier.padding(top = 6.dp, end = 10.dp),
            )
        }
    }
    if (shouldShowDeletionDialog) {
        CsAlertDialog(
            titleRes = localesR.string.delete_subscription,
            icon = CsIcons.Outlined.Delete,
            confirmButtonTextRes = localesR.string.delete,
            dismissButtonTextRes = localesR.string.cancel,
            onConfirm = {
                onDeleteClick(subscription.id)
                shouldShowDeletionDialog = false
            },
            onDismiss = { shouldShowDeletionDialog = false },
            content = {
                Column {
                    Text(stringResource(localesR.string.permanently_delete_subscription))
                    CsListItem(
                        headlineContent = {
                            Text(
                                text = subscription.title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                        supportingContent = {
                            Text(
                                text = subscription.amount.formatAmount(subscription.currency),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = CsIcons.Outlined.Autorenew,
                                contentDescription = null,
                            )
                        },
                    )
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun DropdownMenu(
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = modifier.wrapContentSize(Alignment.TopStart),
    ) {
        CsIconButton(
            onClick = { expanded = true },
            icon = CsIcons.Outlined.MoreVert,
            contentDescription = stringResource(localesR.string.toggle_menu),
            modifier = Modifier.size(IconButtonDefaults.smallContainerSize(IconButtonDefaults.IconButtonWidthOption.Narrow)),
        )
        DropdownMenu(
            expanded = expanded,
            shape = MenuDefaults.standaloneGroupShape,
            onDismissRequest = { expanded = false },
            containerColor = MenuDefaults.groupVibrantContainerColor,
        ) {
            DropdownMenuItem(
                shapes = MenuDefaults.itemShape(0, 2),
                text = { Text(text = stringResource(localesR.string.edit)) },
                onClick = {
                    onEditClick()
                    expanded = false
                },
                leadingIcon = {
                    Icon(
                        imageVector = CsIcons.Outlined.Edit,
                        contentDescription = null,
                    )
                },
                selected = false,
                colors = MenuDefaults.selectableItemVibrantColors(),
            )
            DropdownMenuItem(
                shapes = MenuDefaults.itemShape(1, 2),
                text = { Text(text = stringResource(localesR.string.delete)) },
                onClick = {
                    onDeleteClick()
                    expanded = false
                },
                leadingIcon = {
                    Icon(
                        imageVector = CsIcons.Outlined.Delete,
                        contentDescription = null,
                    )
                },
                selected = false,
                colors = MenuDefaults.selectableItemVibrantColors(),
            )
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
                    title = "Apple Music",
                    amount = BigDecimal(10.99),
                    currency = getUsdCurrency(),
                    paymentDate = Clock.System.now(),
                    reminder = null,
                ),
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}