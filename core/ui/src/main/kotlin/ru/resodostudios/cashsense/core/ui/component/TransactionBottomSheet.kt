package ru.resodostudios.cashsense.core.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.resodostudios.cashsense.core.designsystem.component.CsListItem
import ru.resodostudios.cashsense.core.designsystem.component.CsModalBottomSheet
import ru.resodostudios.cashsense.core.designsystem.component.CsSwitch
import ru.resodostudios.cashsense.core.designsystem.component.CsTag
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Block
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.CheckCircle
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Delete
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Edit
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Pending
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Redo
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Schedule
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.SendMoney
import ru.resodostudios.cashsense.core.model.data.DateFormatType
import ru.resodostudios.cashsense.core.model.data.StatusType.COMPLETED
import ru.resodostudios.cashsense.core.model.data.TransactionWithCategory
import ru.resodostudios.cashsense.core.ui.util.formatAmount
import ru.resodostudios.cashsense.core.ui.util.formatDate
import java.math.BigDecimal
import java.time.format.FormatStyle
import java.util.Currency
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
fun TransactionBottomSheet(
    transactionCategory: TransactionWithCategory,
    currency: Currency,
    onDismiss: () -> Unit,
    onIgnoreClick: (Boolean) -> Unit,
    onRepeatClick: (String) -> Unit,
    onEdit: (String) -> Unit,
    onDelete: () -> Unit,
) {
    val transaction = transactionCategory.transaction
    CsModalBottomSheet(onDismiss) {
        val (leadingIcon, supportingText) = if (transaction.transferId != null) {
            CsIcons.Outlined.SendMoney to stringResource(localesR.string.transfers)
        } else {
            val icon = StoredIcon.asImageVector(
                transactionCategory.category?.iconId
                    ?: StoredIcon.TRANSACTION.storedId
            )
            val categoryTitle = transactionCategory.category?.title
                ?: stringResource(localesR.string.uncategorized)
            icon to categoryTitle
        }
        Column {
            CsListItem(
                headlineContent = {
                    Text(
                        text = transaction.amount
                            .run { if (this < BigDecimal.ZERO) negate() else abs() }
                            .formatAmount(currency, true),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                supportingContent = {
                    Text(
                        text = supportingText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                leadingContent = {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                    )
                }
            )
            FlowRow(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CsTag(
                    text = transaction.timestamp.formatDate(
                        DateFormatType.DATE_TIME,
                        FormatStyle.SHORT
                    ),
                    icon = CsIcons.Outlined.Schedule,
                )
                val transactionStatusTag = if (transaction.status == COMPLETED) {
                    Triple(
                        stringResource(localesR.string.completed),
                        CsIcons.Outlined.CheckCircle,
                        MaterialTheme.colorScheme.secondaryContainer,
                    )
                } else {
                    Triple(
                        stringResource(localesR.string.pending),
                        CsIcons.Outlined.Pending,
                        MaterialTheme.colorScheme.tertiaryContainer,
                    )
                }
                CsTag(
                    text = transactionStatusTag.first,
                    icon = transactionStatusTag.second,
                    color = transactionStatusTag.third,
                )
            }
            if (transaction.description != null && transaction.description!!.isNotBlank()) {
                Text(
                    text = stringResource(localesR.string.description),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(
                        top = 16.dp,
                        bottom = 8.dp,
                        start = 16.dp,
                        end = 16.dp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                ExpandableText(
                    text = transaction.description.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                )
            }
            HorizontalDivider(Modifier.padding(16.dp))
            if (transaction.transferId == null) {
                CsListItem(
                    headlineContent = { Text(stringResource(localesR.string.transaction_ignore)) },
                    leadingContent = {
                        Icon(
                            imageVector = CsIcons.Outlined.Block,
                            contentDescription = null,
                        )
                    },
                    trailingContent = {
                        CsSwitch(
                            checked = transaction.ignored,
                            onCheckedChange = onIgnoreClick,
                        )
                    },
                )
                CsListItem(
                    headlineContent = { Text(stringResource(localesR.string.repeat)) },
                    leadingContent = {
                        Icon(
                            imageVector = CsIcons.Outlined.Redo,
                            contentDescription = null,
                        )
                    },
                    onClick = {
                        onDismiss()
                        onRepeatClick(transaction.id)
                    },
                )
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
                        onEdit(transaction.id)
                    },
                )
            }
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

@Composable
private fun ExpandableText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    collapsedMaxLine: Int = 3,
    showMoreText: String = stringResource(localesR.string.show_more),
    showMoreStyle: SpanStyle = SpanStyle(
        fontWeight = FontWeight.W500,
        fontStyle = FontStyle.Italic,
        fontSize = 14.sp,
        textDecoration = TextDecoration.Underline,
    ),
    showLessText: String = stringResource(localesR.string.show_less),
    showLessStyle: SpanStyle = showMoreStyle,
) {
    var expanded by remember { mutableStateOf(false) }
    var clickable by remember { mutableStateOf(false) }
    var lastCharIndex by remember { mutableIntStateOf(0) }

    Box(
        modifier = modifier
            .clickable(
                enabled = clickable,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { expanded = !expanded },
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
            text = buildAnnotatedString {
                if (clickable) {
                    if (expanded) {
                        append("$text ")
                        withStyle(style = showLessStyle) { append(showLessText) }
                    } else {
                        val adjustText = text.substring(startIndex = 0, endIndex = lastCharIndex)
                            .dropLast(showMoreText.length + 4)
                            .dropLastWhile { Character.isWhitespace(it) || it == '.' }
                        append("$adjustText... ")
                        withStyle(style = showMoreStyle) { append(showMoreText) }
                    }
                } else {
                    append(text)
                }
            },
            maxLines = if (expanded) Int.MAX_VALUE else collapsedMaxLine,
            onTextLayout = { textLayoutResult ->
                if (!expanded && textLayoutResult.hasVisualOverflow) {
                    clickable = true
                    lastCharIndex = textLayoutResult.getLineEnd(collapsedMaxLine - 1)
                }
            },
            style = style,
        )
    }
}