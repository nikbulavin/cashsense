package ru.resodostudios.cashsense.core.ui.component

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Month
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.ChevronLeft
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.ChevronRight
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Close
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.DateType
import ru.resodostudios.cashsense.core.model.data.DateType.ALL
import ru.resodostudios.cashsense.core.model.data.DateType.MONTH
import ru.resodostudios.cashsense.core.model.data.DateType.WEEK
import ru.resodostudios.cashsense.core.model.data.DateType.YEAR
import ru.resodostudios.cashsense.core.model.data.FinanceType
import ru.resodostudios.cashsense.core.model.data.FinanceType.EXPENSES
import ru.resodostudios.cashsense.core.model.data.FinanceType.INCOME
import ru.resodostudios.cashsense.core.model.data.FinanceType.NOT_SET
import ru.resodostudios.cashsense.core.model.data.TransactionFilter
import ru.resodostudios.cashsense.core.model.data.TransactionWithCategory
import ru.resodostudios.cashsense.core.ui.TransactionCategoryPreviewParameterProvider
import ru.resodostudios.cashsense.core.ui.util.formatAmount
import ru.resodostudios.cashsense.core.ui.util.getCurrentYear
import ru.resodostudios.cashsense.core.ui.util.getCurrentZonedDateTime
import ru.resodostudios.cashsense.core.util.getUsdCurrency
import java.math.BigDecimal
import java.math.MathContext
import java.time.format.TextStyle
import java.util.Currency
import java.util.Locale
import ru.resodostudios.cashsense.core.locales.R as localesR

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun FinancePanel(
    availableCategories: List<Category>,
    currency: Currency,
    expenses: BigDecimal,
    income: BigDecimal,
    graphData: Map<Int, BigDecimal>,
    transactionFilter: TransactionFilter,
    onDateTypeUpdate: (DateType) -> Unit,
    onFinanceTypeUpdate: (FinanceType) -> Unit,
    onSelectedDateUpdate: (Int) -> Unit,
    onCategorySelect: (Category) -> Unit,
    onCategoryDeselect: (Category) -> Unit,
    modifier: Modifier = Modifier,
    shouldShowApproximately: Boolean = false,
) {
    Column(
        modifier = modifier.animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SharedTransitionLayout {
            AnimatedContent(
                targetState = transactionFilter.financeType,
                label = "FinancePanel",
                transitionSpec = {
                    (fadeIn(animationSpec = tween(220, delayMillis = 90)) + scaleIn(
                        initialScale = 0.92f,
                        animationSpec = tween(220, delayMillis = 90),
                    )) togetherWith fadeOut(snap())
                },
            ) { financeType ->
                when (financeType) {
                    NOT_SET -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                        ) {
                            val expensesProgress by animateFloatAsState(
                                targetValue = getFinanceProgress(expenses, income + expenses),
                                label = "ExpensesProgress",
                                animationSpec = tween(durationMillis = 400),
                            )
                            val incomeProgress by animateFloatAsState(
                                targetValue = getFinanceProgress(income, income + expenses),
                                label = "IncomeProgress",
                                animationSpec = tween(durationMillis = 400),
                            )
                            FinanceCard(
                                amount = expenses,
                                currency = currency,
                                subtitleRes = localesR.string.expenses,
                                indicatorProgress = expensesProgress,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    onFinanceTypeUpdate(EXPENSES)
                                    onDateTypeUpdate(MONTH)
                                },
                                animatedVisibilityScope = this@AnimatedContent,
                                shouldShowApproximately = shouldShowApproximately,
                            )
                            FinanceCard(
                                amount = income,
                                currency = currency,
                                subtitleRes = localesR.string.income_plural,
                                indicatorProgress = incomeProgress,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    onFinanceTypeUpdate(INCOME)
                                    onDateTypeUpdate(MONTH)
                                },
                                animatedVisibilityScope = this@AnimatedContent,
                                shouldShowApproximately = shouldShowApproximately,
                            )
                        }
                    }

                    EXPENSES -> {
                        DetailedFinanceSection(
                            amount = expenses,
                            graphData = graphData,
                            transactionFilter = transactionFilter,
                            currency = currency,
                            subtitleRes = localesR.string.expenses,
                            onBackClick = {
                                onFinanceTypeUpdate(NOT_SET)
                                onDateTypeUpdate(ALL)
                            },
                            onDateTypeUpdate = onDateTypeUpdate,
                            onSelectedDateUpdate = onSelectedDateUpdate,
                            onCategorySelect = onCategorySelect,
                            onCategoryDeselect = onCategoryDeselect,
                            modifier = Modifier.fillMaxWidth(),
                            animatedVisibilityScope = this@AnimatedContent,
                            availableCategories = availableCategories,
                            shouldShowApproximately = shouldShowApproximately,
                        )
                    }

                    INCOME -> {
                        DetailedFinanceSection(
                            amount = income,
                            graphData = graphData,
                            transactionFilter = transactionFilter,
                            currency = currency,
                            subtitleRes = localesR.string.income_plural,
                            onBackClick = {
                                onFinanceTypeUpdate(NOT_SET)
                                onDateTypeUpdate(ALL)
                            },
                            onDateTypeUpdate = onDateTypeUpdate,
                            onSelectedDateUpdate = onSelectedDateUpdate,
                            onCategorySelect = onCategorySelect,
                            onCategoryDeselect = onCategoryDeselect,
                            modifier = Modifier.fillMaxWidth(),
                            animatedVisibilityScope = this@AnimatedContent,
                            availableCategories = availableCategories,
                            shouldShowApproximately = shouldShowApproximately,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.FinanceCard(
    amount: BigDecimal,
    currency: Currency,
    @StringRes subtitleRes: Int,
    indicatorProgress: Float,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    enabled: Boolean = true,
    shouldShowApproximately: Boolean = false,
) {
    OutlinedCard(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        onClick = onClick,
        enabled = enabled,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            AnimatedAmount(
                targetState = amount,
                label = "FinanceCardTitle",
                modifier = Modifier.sharedBounds(
                    sharedContentState = rememberSharedContentState("$amount/$subtitleRes"),
                    animatedVisibilityScope = animatedVisibilityScope,
                ),
            ) {
                Text(
                    text = it.formatAmount(
                        currency = currency,
                        withApproximately = shouldShowApproximately,
                    ),
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = stringResource(subtitleRes),
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.sharedBounds(
                    sharedContentState = rememberSharedContentState(subtitleRes),
                    animatedVisibilityScope = animatedVisibilityScope,
                ),
            )
            LinearProgressIndicator(
                progress = { if (enabled) indicatorProgress else 0f },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.DetailedFinanceSection(
    amount: BigDecimal,
    availableCategories: List<Category>,
    graphData: Map<Int, BigDecimal>,
    transactionFilter: TransactionFilter,
    currency: Currency,
    @StringRes subtitleRes: Int,
    onBackClick: () -> Unit,
    onDateTypeUpdate: (DateType) -> Unit,
    onSelectedDateUpdate: (Int) -> Unit,
    onCategorySelect: (Category) -> Unit,
    onCategoryDeselect: (Category) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    shouldShowApproximately: Boolean = false,
) {
    Column(modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp, start = 16.dp, end = 16.dp),
        ) {
            FilterDateTypeSelectorRow(
                transactionFilter = transactionFilter,
                onDateTypeUpdate = onDateTypeUpdate,
                modifier = Modifier
                    .defaultMinSize(minWidth = 400.dp)
                    .weight(1f, false),
            )
            FilledTonalIconButton(
                onClick = onBackClick,
                modifier = Modifier.padding(start = 12.dp),
            ) {
                Icon(
                    imageVector = CsIcons.Outlined.Close,
                    contentDescription = null,
                )
            }
        }
        AnimatedVisibility(transactionFilter.dateType != WEEK) {
            FilterBySelectedDateTypeRow(
                onSelectedDateUpdate = onSelectedDateUpdate,
                transactionFilter = transactionFilter,
                modifier = Modifier.padding(bottom = 6.dp, start = 16.dp, end = 16.dp),
            )
        }
        AnimatedAmount(
            targetState = amount,
            label = "DetailedFinanceCard",
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp)
                .sharedBounds(
                    sharedContentState = rememberSharedContentState("$amount/$subtitleRes"),
                    animatedVisibilityScope = animatedVisibilityScope,
                ),
        ) {
            Text(
                text = amount.formatAmount(
                    currency = currency,
                    withApproximately = shouldShowApproximately,
                ),
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            text = stringResource(subtitleRes),
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp)
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(subtitleRes),
                    animatedVisibilityScope = animatedVisibilityScope,
                ),
            style = MaterialTheme.typography.labelLarge,
        )
        AnimatedVisibility(graphData.isNotEmpty() && transactionFilter.financeType != NOT_SET) {
            FinanceGraph(
                transactionFilter = transactionFilter,
                graphData = graphData,
                currency = currency,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
            )
        }
        AnimatedVisibility(transactionFilter.financeType != NOT_SET) {
            CategorySelectionRow(
                availableCategories = availableCategories,
                selectedCategories = transactionFilter.selectedCategories,
                onCategorySelect = onCategorySelect,
                onCategoryDeselect = onCategoryDeselect,
                modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp),
            )
        }
    }
}

@Composable
private fun FilterDateTypeSelectorRow(
    transactionFilter: TransactionFilter,
    onDateTypeUpdate: (DateType) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dateTypes = listOf(
        stringResource(localesR.string.week),
        stringResource(localesR.string.month),
        stringResource(localesR.string.year),
    )
    SingleChoiceSegmentedButtonRow(modifier) {
        dateTypes.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = dateTypes.size,
                ),
                onClick = { onDateTypeUpdate(DateType.entries[index]) },
                selected = transactionFilter.dateType == DateType.entries[index],
            ) {
                Text(
                    text = label,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun FilterBySelectedDateTypeRow(
    onSelectedDateUpdate: (Int) -> Unit,
    transactionFilter: TransactionFilter,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth(),
    ) {
        IconButton(
            onClick = { onSelectedDateUpdate(-1) },
        ) {
            Icon(
                imageVector = CsIcons.Outlined.ChevronLeft,
                contentDescription = null,
            )
        }

        val selectedDate = when (transactionFilter.dateType) {
            YEAR -> transactionFilter.selectedDate.year.toString()
            MONTH -> {
                val monthName = Month(transactionFilter.selectedDate.monthNumber)
                    .getDisplayName(
                        TextStyle.FULL_STANDALONE,
                        Locale.getDefault()
                    )
                    .replaceFirstChar { it.uppercaseChar() }

                if (transactionFilter.selectedDate.year != getCurrentYear()) {
                    "$monthName ${transactionFilter.selectedDate.year}"
                } else {
                    monthName
                }
            }

            ALL, WEEK -> ""
        }

        Text(
            text = selectedDate,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        IconButton(
            onClick = { onSelectedDateUpdate(1) },
        ) {
            Icon(
                imageVector = CsIcons.Outlined.ChevronRight,
                contentDescription = null,
            )
        }
    }
}

fun getFinanceProgress(
    value: BigDecimal,
    sumOfIncomeAndExpenses: BigDecimal,
): Float {
    if (sumOfIncomeAndExpenses.signum() == 0) return 0f
    return value.divide(sumOfIncomeAndExpenses, MathContext.DECIMAL32).toFloat()
}

@Preview
@Composable
fun FinancePanelDefaultPreview(
    @PreviewParameter(TransactionCategoryPreviewParameterProvider::class)
    transactionsCategories: List<TransactionWithCategory>,
) {
    CsTheme {
        Surface {
            val categories = transactionsCategories.mapNotNullTo(HashSet()) { it.category }
            FinancePanel(
                transactionFilter = TransactionFilter(
                    selectedCategories = categories.take(3).toSet(),
                    financeType = NOT_SET,
                    dateType = ALL,
                    selectedDate = getCurrentZonedDateTime().date,
                ),
                availableCategories = categories.toList(),
                currency = getUsdCurrency(),
                expenses = BigDecimal(200),
                income = BigDecimal(800),
                graphData = emptyMap(),
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                onDateTypeUpdate = {},
                onFinanceTypeUpdate = {},
                onSelectedDateUpdate = {},
                onCategorySelect = {},
                onCategoryDeselect = {},
            )
        }
    }
}

@Preview
@Composable
fun FinancePanelOpenedPreview(
    @PreviewParameter(TransactionCategoryPreviewParameterProvider::class)
    transactionsCategories: List<TransactionWithCategory>,
) {
    CsTheme {
        Surface {
            val categories = transactionsCategories.mapNotNull { it.category }
            FinancePanel(
                transactionFilter = TransactionFilter(
                    selectedCategories = categories.take(3).toSet(),
                    financeType = EXPENSES,
                    dateType = MONTH,
                    selectedDate = getCurrentZonedDateTime().date,
                ),
                availableCategories = categories.toList(),
                currency = getUsdCurrency(),
                expenses = BigDecimal(200),
                income = BigDecimal(800),
                graphData = mapOf(
                    1 to BigDecimal(100),
                    2 to BigDecimal(200),
                    3 to BigDecimal(300),
                    4 to BigDecimal(400),
                    5 to BigDecimal(500),
                ),
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                onDateTypeUpdate = {},
                onFinanceTypeUpdate = {},
                onSelectedDateUpdate = {},
                onCategorySelect = {},
                onCategoryDeselect = {},
            )
        }
    }
}