package ru.resodostudios.cashsense.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import ru.resodostudios.cashsense.core.designsystem.component.CsTag
import ru.resodostudios.cashsense.core.designsystem.component.ListItemPositionShapes
import ru.resodostudios.cashsense.core.model.data.DateFormatType
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.ui.component.EmptyState
import ru.resodostudios.cashsense.core.ui.component.TransactionItem
import ru.resodostudios.cashsense.core.ui.util.formatDate
import java.time.format.FormatStyle
import kotlin.time.Instant
import ru.resodostudios.cashsense.core.locales.R as localesR

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalHazeApi::class)
fun LazyListScope.transactions(
    groupedTransactions: Map<Instant, List<Transaction>>,
    hazeState: HazeState,
    hazeStyle: HazeStyle,
    onClick: (Transaction?) -> Unit,
    selectedTransaction: Transaction? = null,
    onRepeatClick: (String) -> Unit = {},
    onEditClick: (String) -> Unit = {},
    onDeleteClick: () -> Unit = {},
) {
    if (groupedTransactions.isNotEmpty()) {
        groupedTransactions.forEach { transactionGroup ->
            stickyHeader(
                contentType = "Date",
            ) {
                CsTag(
                    text = transactionGroup.key.formatDate(DateFormatType.DATE, FormatStyle.MEDIUM),
                    color = Color.Transparent,
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp)
                        .clip(CircleShape)
                        .hazeEffect(hazeState, hazeStyle) {
                            blurEnabled = true
                            blurRadius = 10.dp
                            noiseFactor = 0.1f
                            inputScale = HazeInputScale.Auto
                        },
                )
            }
            item { Spacer(Modifier.height(16.dp)) }
            itemsIndexed(
                items = transactionGroup.value,
                key = { _, transaction -> transaction.id },
                contentType = { _, _ -> "Transaction" }
            ) { index, transaction ->
                val shape = when (index) {
                    0 if transactionGroup.value.size == 1 -> ListItemPositionShapes.Single
                    0 -> ListItemPositionShapes.First
                    transactionGroup.value.lastIndex -> ListItemPositionShapes.Last
                    else -> ListItemPositionShapes.Middle
                }
                val selected = selectedTransaction == transaction
                val motionScheme = MaterialTheme.motionScheme
                TransactionItem(
                    transaction = transaction,
                    currency = transaction.currency,
                    modifier = Modifier
                        .hazeSource(hazeState)
                        .padding(horizontal = 16.dp)
                        .clip(shape)
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        .clickable { onClick(if (selected) null else transaction) }
                        .animateItem(
                            fadeInSpec = motionScheme.defaultEffectsSpec(),
                            fadeOutSpec = motionScheme.defaultEffectsSpec(),
                            placementSpec = motionScheme.defaultSpatialSpec(),
                        ),
                    selected = selected,
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

fun List<Transaction>.groupByDate(
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): Map<Instant, List<Transaction>> {
    return this
        .groupBy {
            it.timestamp
                .toLocalDateTime(timeZone).date
                .atTime(0, 0)
                .toInstant(timeZone)
        }
        .toSortedMap(compareByDescending { it })
}