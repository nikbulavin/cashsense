package ru.resodostudios.cashsense.core.ui.component

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.snap
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import kotlinx.datetime.Month
import kotlinx.datetime.number
import kotlinx.datetime.toJavaMonth
import ru.resodostudios.cashsense.core.designsystem.component.button.CsConnectedButtonGroup
import ru.resodostudios.cashsense.core.designsystem.component.button.CsIconButton
import ru.resodostudios.cashsense.core.designsystem.component.button.CsOutlinedIconButton
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.ChevronLeft
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.ChevronRight
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Close
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.designsystem.theme.LocalSharedTransitionScope
import ru.resodostudios.cashsense.core.designsystem.theme.SharedElementKey
import ru.resodostudios.cashsense.core.designsystem.theme.sharedElementTransitionSpec
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
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.model.data.TransactionFilter
import ru.resodostudios.cashsense.core.ui.TransactionPreviewParameterProvider
import ru.resodostudios.cashsense.core.ui.util.getCurrentYear
import ru.resodostudios.cashsense.core.ui.util.getCurrentZonedDateTime
import ru.resodostudios.cashsense.core.util.getUsdCurrency
import java.math.BigDecimal
import java.time.format.TextStyle
import java.util.Currency
import java.util.Locale
import ru.resodostudios.cashsense.core.locales.R as localesR

@OptIn(
    ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
fun FinancePanel(
    availableCategories: List<Category>,
    currency: Currency,
    formattedExpenses: String,
    formattedIncome: String,
    graphData: Map<Int, BigDecimal>,
    transactionFilter: TransactionFilter,
    onDateTypeUpdate: (DateType) -> Unit,
    onFinanceTypeUpdate: (FinanceType) -> Unit,
    onSelectedDateUpdate: (Int) -> Unit,
    onCategoryFilterUpdate: (Category, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val animSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
        AnimatedContent(
            targetState = transactionFilter.financeType,
            label = "FinancePanel",
            transitionSpec = {
                fadeIn() + scaleIn(animSpec, 0.92f) togetherWith fadeOut(snap())
            },
        ) { financeType ->
            when (financeType) {
                NOT_SET -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    ) {
                        FinanceCard(
                            formattedAmount = formattedExpenses,
                            subtitleRes = localesR.string.expenses,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onFinanceTypeUpdate(EXPENSES)
                                onDateTypeUpdate(MONTH)
                            },
                            animatedVisibilityScope = this@AnimatedContent,
                            sharedContentState = SharedElementKey.Expenses,
                        )
                        FinanceCard(
                            formattedAmount = formattedIncome,
                            subtitleRes = localesR.string.income_plural,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onFinanceTypeUpdate(INCOME)
                                onDateTypeUpdate(MONTH)
                            },
                            animatedVisibilityScope = this@AnimatedContent,
                            sharedContentState = SharedElementKey.Income,
                        )
                    }
                }

                EXPENSES -> {
                    DetailedFinanceSection(
                        formattedAmount = formattedExpenses,
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
                        onCategoryFilterUpdate = onCategoryFilterUpdate,
                        modifier = Modifier.fillMaxWidth(),
                        animatedVisibilityScope = this@AnimatedContent,
                        availableCategories = availableCategories,
                        sharedContentState = SharedElementKey.Expenses,
                    )
                }

                INCOME -> {
                    DetailedFinanceSection(
                        formattedAmount = formattedIncome,
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
                        onCategoryFilterUpdate = onCategoryFilterUpdate,
                        modifier = Modifier.fillMaxWidth(),
                        animatedVisibilityScope = this@AnimatedContent,
                        availableCategories = availableCategories,
                        sharedContentState = SharedElementKey.Income,
                    )
                }
            }
        }
    }
}

@OptIn(
    ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
private fun FinanceCard(
    formattedAmount: String,
    @StringRes subtitleRes: Int,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedContentState: SharedElementKey,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    enabled: Boolean = true,
) {
    with(LocalSharedTransitionScope.current) {
        OutlinedCard(
            modifier = modifier,
            shape = RoundedCornerShape(20.dp),
            onClick = onClick,
            enabled = enabled,
        ) {
            Column(
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                AnimatedAmount(
                    formattedAmount = formattedAmount,
                    label = "FinanceCardTitle",
                    modifier = Modifier.sharedBounds(
                        boundsTransform = MaterialTheme.motionScheme.sharedElementTransitionSpec,
                        sharedContentState = rememberSharedContentState(sharedContentState),
                        animatedVisibilityScope = animatedVisibilityScope,
                        resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(),
                    ),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = stringResource(subtitleRes),
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.sharedBounds(
                        boundsTransform = MaterialTheme.motionScheme.sharedElementTransitionSpec,
                        sharedContentState = rememberSharedContentState(subtitleRes),
                        animatedVisibilityScope = animatedVisibilityScope,
                        resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(),
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@OptIn(
    ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3Api::class,
)
@Composable
private fun DetailedFinanceSection(
    formattedAmount: String,
    availableCategories: List<Category>,
    graphData: Map<Int, BigDecimal>,
    transactionFilter: TransactionFilter,
    currency: Currency,
    @StringRes subtitleRes: Int,
    onBackClick: () -> Unit,
    onDateTypeUpdate: (DateType) -> Unit,
    onSelectedDateUpdate: (Int) -> Unit,
    onCategoryFilterUpdate: (Category, Boolean) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedContentState: SharedElementKey,
    modifier: Modifier = Modifier,
) {
    with(LocalSharedTransitionScope.current) {
        BackHandler { onBackClick() }
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
                        .widthIn(max = 500.dp)
                        .weight(1f, false),
                )
                CsOutlinedIconButton(
                    onClick = onBackClick,
                    icon = CsIcons.Outlined.Close,
                    contentDescription = stringResource(localesR.string.close),
                    modifier = Modifier.padding(start = 12.dp),
                )
            }
            AnimatedVisibility(
                visible = transactionFilter.dateType != WEEK,
                enter = fadeIn() + expandVertically(MaterialTheme.motionScheme.fastSpatialSpec()),
            ) {
                FilterBySelectedDateTypeRow(
                    onSelectedDateUpdate = onSelectedDateUpdate,
                    transactionFilter = transactionFilter,
                    modifier = Modifier.padding(bottom = 6.dp, start = 16.dp, end = 16.dp),
                )
            }
            AnimatedAmount(
                formattedAmount = formattedAmount,
                label = "DetailedFinanceCard",
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp)
                    .sharedBounds(
                        boundsTransform = MaterialTheme.motionScheme.sharedElementTransitionSpec,
                        sharedContentState = rememberSharedContentState(sharedContentState),
                        animatedVisibilityScope = animatedVisibilityScope,
                        resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(),
                    ),
                style = MaterialTheme.typography.headlineLarge,
            )
            Text(
                text = stringResource(subtitleRes),
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp)
                    .sharedBounds(
                        boundsTransform = MaterialTheme.motionScheme.sharedElementTransitionSpec,
                        sharedContentState = rememberSharedContentState(subtitleRes),
                        animatedVisibilityScope = animatedVisibilityScope,
                        resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(),
                    ),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (graphData.isNotEmpty() && transactionFilter.financeType != NOT_SET) {
                val modelProducer = remember { CartesianChartModelProducer() }
                LaunchedEffect(graphData) {
                    modelProducer.runTransaction {
                        if (graphData.isEmpty() || graphData.keys.size < 2) return@runTransaction
                        lineSeries { series(graphData.keys, graphData.values) }
                    }
                }
                FinanceGraph(
                    transactionFilter = transactionFilter,
                    modelProducer = modelProducer,
                    currency = currency,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                )
            } else {
                FinanceGraphPlaceholder()
            }
            if (transactionFilter.financeType != NOT_SET) {
                CategorySelectionRow(
                    availableCategories = availableCategories,
                    selectedCategories = transactionFilter.selectedCategories,
                    onCategoryFilterUpdate = onCategoryFilterUpdate,
                    modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun FilterDateTypeSelectorRow(
    transactionFilter: TransactionFilter,
    onDateTypeUpdate: (DateType) -> Unit,
    modifier: Modifier = Modifier,
) {
    CsConnectedButtonGroup(
        selectedIndex = transactionFilter.dateType.ordinal,
        options = listOf(
            stringResource(localesR.string.week),
            stringResource(localesR.string.month),
            stringResource(localesR.string.year),
        ),
        onClick = { onDateTypeUpdate(DateType.entries[it]) },
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
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
        CsIconButton(
            onClick = { onSelectedDateUpdate(-1) },
            icon = CsIcons.Outlined.ChevronLeft,
            contentDescription = stringResource(localesR.string.previous_date),
        )

        val selectedDate = when (transactionFilter.dateType) {
            YEAR -> transactionFilter.selectedDate.year.toString()
            MONTH -> {
                val monthName = Month(transactionFilter.selectedDate.month.number)
                    .toJavaMonth()
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

        CsIconButton(
            onClick = { onSelectedDateUpdate(1) },
            icon = CsIcons.Outlined.ChevronRight,
            contentDescription = stringResource(localesR.string.next_date),
        )
    }
}

@Preview
@Composable
private fun FinancePanelDefaultPreview(
    @PreviewParameter(TransactionPreviewParameterProvider::class)
    transactions: List<Transaction>,
) {
    CsTheme {
        Surface {
            val categories = transactions.mapNotNullTo(HashSet()) { it.category }
            FinancePanel(
                transactionFilter = TransactionFilter(
                    selectedCategories = categories.take(3).toSet(),
                    financeType = NOT_SET,
                    dateType = ALL,
                    selectedDate = getCurrentZonedDateTime().date,
                ),
                availableCategories = categories.toList(),
                currency = getUsdCurrency(),
                formattedExpenses = "$200",
                formattedIncome = "$800",
                graphData = emptyMap(),
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                onDateTypeUpdate = {},
                onFinanceTypeUpdate = {},
                onSelectedDateUpdate = {},
                onCategoryFilterUpdate = { _, _ -> },
            )
        }
    }
}

@Preview
@Composable
private fun FinancePanelOpenedPreview(
    @PreviewParameter(TransactionPreviewParameterProvider::class)
    transactions: List<Transaction>,
) {
    CsTheme {
        Surface {
            val categories = transactions.mapNotNull { it.category }
            FinancePanel(
                transactionFilter = TransactionFilter(
                    selectedCategories = categories.take(3).toSet(),
                    financeType = EXPENSES,
                    dateType = MONTH,
                    selectedDate = getCurrentZonedDateTime().date,
                ),
                availableCategories = categories.toList(),
                currency = getUsdCurrency(),
                formattedExpenses = "$200",
                formattedIncome = "$800",
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
                onCategoryFilterUpdate = { _, _ -> },
            )
        }
    }
}