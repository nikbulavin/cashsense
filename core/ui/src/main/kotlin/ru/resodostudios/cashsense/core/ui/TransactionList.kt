package ru.resodostudios.cashsense.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import ru.resodostudios.cashsense.core.designsystem.component.CsTag
import ru.resodostudios.cashsense.core.designsystem.component.ListItemPositionShapes
import ru.resodostudios.cashsense.core.model.data.DateFormatType
import ru.resodostudios.cashsense.core.model.data.TransactionWithCategory
import ru.resodostudios.cashsense.core.ui.component.EmptyState
import ru.resodostudios.cashsense.core.ui.component.TransactionItem
import ru.resodostudios.cashsense.core.ui.util.formatDate
import java.time.format.FormatStyle
import kotlin.time.Instant
import ru.resodostudios.cashsense.core.locales.R as localesR

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun LazyListScope.transactions(
    transactionsCategories: Map<Instant, List<TransactionWithCategory>>,
    onClick: (String?) -> Unit,
    selectedTransaction: TransactionWithCategory? = null,
    onIgnoreToggle: (Boolean) -> Unit = {},
    onRepeatClick: (String) -> Unit = {},
    onEditClick: (String) -> Unit = {},
    onDeleteClick: () -> Unit = {},
) {
    if (transactionsCategories.isNotEmpty()) {
        transactionsCategories.forEach { transactionGroup ->
            stickyHeader(
                contentType = "Date",
            ) {
                CsTag(
                    text = transactionGroup.key.formatDate(DateFormatType.DATE, FormatStyle.MEDIUM),
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                )
            }
            item { Spacer(Modifier.height(16.dp)) }
            itemsIndexed(
                items = transactionGroup.value,
                key = { _, transactionCategory -> transactionCategory.transaction.id },
                contentType = { _, _ -> "Transaction" }
            ) { index, transactionCategory ->
                val shape = when {
                    index == 0 && transactionGroup.value.size == 1 -> ListItemPositionShapes.Single
                    index == 0 -> ListItemPositionShapes.First
                    index == transactionGroup.value.lastIndex -> ListItemPositionShapes.Last
                    else -> ListItemPositionShapes.Middle
                }
                val selected = selectedTransaction?.transaction?.id == transactionCategory.transaction.id
                TransactionItem(
                    transaction = transactionCategory.transaction,
                    category = transactionCategory.category,
                    currency = transactionCategory.transaction.currency,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .clip(shape)
                        .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                        .clickable { onClick(if (selected) null else transactionCategory.transaction.id) }
                        .animateItem(
                            placementSpec = MaterialTheme.motionScheme.defaultSpatialSpec(),
                        ),
                    selected = selected,
                    onIgnoreToggle = onIgnoreToggle,
                    onRepeatClick = onRepeatClick,
                    onEditClick = onEditClick,
                    onDeleteClick = onDeleteClick,
                )
                if (index != transactionGroup.value.lastIndex) Spacer(Modifier.height(2.dp))
            }
        }
    } else {
        item {
            EmptyState(
                messageRes = localesR.string.transactions_empty,
                animationRes = R.raw.anim_transactions_empty,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
            )
        }
    }
}

fun List<TransactionWithCategory>.groupByDate(
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): Map<Instant, List<TransactionWithCategory>> {
    return this
        .asSequence()
        .groupBy {
            it.transaction.timestamp
                .toLocalDateTime(timeZone).date
                .atTime(0, 0)
                .toInstant(timeZone)
        }
        .toSortedMap(compareByDescending { it })
}