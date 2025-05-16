package ru.resodostudios.cashsense.core.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import ru.resodostudios.cashsense.core.designsystem.component.CsTag
import ru.resodostudios.cashsense.core.designsystem.component.ListItemShape
import ru.resodostudios.cashsense.core.model.data.DateFormatType
import ru.resodostudios.cashsense.core.model.data.TransactionWithCategory
import ru.resodostudios.cashsense.core.ui.component.EmptyState
import ru.resodostudios.cashsense.core.ui.component.TransactionItem
import ru.resodostudios.cashsense.core.ui.util.formatDate
import java.time.format.FormatStyle
import ru.resodostudios.cashsense.core.locales.R as localesR

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun LazyListScope.transactions(
    transactionsCategories: List<TransactionWithCategory>,
    onTransactionClick: (String) -> Unit,
) {
    if (transactionsCategories.isNotEmpty()) {
        val transactionsByDay = transactionsCategories
            .groupBy {
                val timeZone = TimeZone.currentSystemDefault()
                it.transaction.timestamp
                    .toLocalDateTime(timeZone).date
                    .atTime(0, 0)
                    .toInstant(timeZone)
            }
            .toSortedMap(compareByDescending { it })

        transactionsByDay.forEach { transactionGroup ->
            stickyHeader {
                CsTag(
                    text = transactionGroup.key.formatDate(DateFormatType.DATE, FormatStyle.MEDIUM),
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                )
            }
            item { Spacer(Modifier.height(16.dp)) }
            itemsIndexed(
                items = transactionGroup.value,
                key = { _, transactionCategory -> transactionCategory.transaction.id },
                contentType = { _, _ -> "transaction" }
            ) { index, transactionCategory ->
                val shape = when {
                    index == 0 && transactionGroup.value.size == 1 -> ListItemShape.Single
                    index == 0 -> ListItemShape.First
                    index == transactionGroup.value.lastIndex -> ListItemShape.Last
                    else -> ListItemShape.Middle
                }
                TransactionItem(
                    transactionCategory = transactionCategory,
                    currency = transactionCategory.transaction.currency,
                    onClick = onTransactionClick,
                    shape = shape,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .animateItem(
                            placementSpec = MaterialTheme.motionScheme.defaultSpatialSpec(),
                        ),
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