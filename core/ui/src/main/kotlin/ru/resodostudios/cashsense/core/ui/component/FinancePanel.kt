package ru.resodostudios.cashsense.core.ui.component

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconButtonDefaults.mediumContainerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.lineModel
import kotlinx.datetime.Month
import kotlinx.datetime.number
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaMonth
import ru.resodostudios.cashsense.core.common.getUsdCurrency
import ru.resodostudios.cashsense.core.designsystem.component.button.ConnectedToggleButtonGroup
import ru.resodostudios.cashsense.core.designsystem.component.button.CsFilledTonalIconButton
import ru.resodostudios.cashsense.core.designsystem.component.button.CsOutlinedIconButton
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.ChevronLeft
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.ChevronRight
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Close
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.designsystem.theme.SharedElementKey
import ru.resodostudios.cashsense.core.designsystem.theme.SharedElementType
import ru.resodostudios.cashsense.core.designsystem.theme.sharedBoundsWithDefaults
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
import java.math.BigDecimal
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Currency
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
fun FinancePanel(
    walletId: String,
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
    SharedTransitionLayout {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val expensesSharedState = SharedElementKey(
                id = walletId,
                origin = walletId,
                type = SharedElementType.ExpensesAmount,
            )
            val expensesTitleSharedState = SharedElementKey(
                id = walletId,
                origin = walletId,
                type = SharedElementType.ExpensesTitle,
            )
            val incomeSharedState = SharedElementKey(
                id = walletId,
                origin = walletId,
                type = SharedElementType.IncomeAmount,
            )
            val incomeTitleSharedState = SharedElementKey(
                id = walletId,
                origin = walletId,
                type = SharedElementType.IncomeTitle,
            )
            val motionScheme = MaterialTheme.motionScheme
            AnimatedContent(
                targetState = transactionFilter.financeType,
                label = "FinancePanel",
                transitionSpec = {
                    fadeIn(motionScheme.defaultEffectsSpec()) togetherWith
                            fadeOut(motionScheme.defaultEffectsSpec())
                },
            ) { financeType ->
                var expensesAmountState by remember(financeType) { mutableStateOf(formattedExpenses) }
                var incomeAmountState by remember(financeType) { mutableStateOf(formattedIncome) }
                var graphDataState by remember(financeType) { mutableStateOf(graphData) }
                var transactionFilterState by remember(financeType) { mutableStateOf(transactionFilter) }

                if (financeType == transactionFilter.financeType) {
                    expensesAmountState = formattedExpenses
                    incomeAmountState = formattedIncome
                    graphDataState = graphData
                    transactionFilterState = transactionFilter
                }

                when (financeType) {
                    NOT_SET -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(horizontal = 16.dp),
                        ) {
                            FinanceCard(
                                formattedAmount = expensesAmountState,
                                title = stringResource(localesR.string.expenses),
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    onFinanceTypeUpdate(EXPENSES)
                                    onDateTypeUpdate(MONTH)
                                },
                                animatedVisibilityScope = this@AnimatedContent,
                                sharedTransitionScope = this@SharedTransitionLayout,
                                amountSharedContentState = expensesSharedState,
                                titleSharedContentState = expensesTitleSharedState,
                            )
                            FinanceCard(
                                formattedAmount = incomeAmountState,
                                title = stringResource(localesR.string.income_plural),
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    onFinanceTypeUpdate(INCOME)
                                    onDateTypeUpdate(MONTH)
                                },
                                animatedVisibilityScope = this@AnimatedContent,
                                sharedTransitionScope = this@SharedTransitionLayout,
                                amountSharedContentState = incomeSharedState,
                                titleSharedContentState = incomeTitleSharedState,
                            )
                        }
                    }

                    EXPENSES -> {
                        DetailedFinanceSection(
                            formattedAmount = expensesAmountState,
                            graphData = graphDataState,
                            transactionFilter = transactionFilterState,
                            currency = currency,
                            title = stringResource(localesR.string.expenses),
                            onBackClick = {
                                onFinanceTypeUpdate(NOT_SET)
                                onDateTypeUpdate(ALL)
                            },
                            onDateTypeUpdate = onDateTypeUpdate,
                            onSelectedDateUpdate = onSelectedDateUpdate,
                            onCategoryFilterUpdate = onCategoryFilterUpdate,
                            animatedVisibilityScope = this@AnimatedContent,
                            sharedTransitionScope = this@SharedTransitionLayout,
                            availableCategories = availableCategories,
                            amountSharedContentState = expensesSharedState,
                            titleSharedContentState = expensesTitleSharedState,
                        )
                    }

                    INCOME -> {
                        DetailedFinanceSection(
                            formattedAmount = incomeAmountState,
                            graphData = graphDataState,
                            transactionFilter = transactionFilterState,
                            currency = currency,
                            title = stringResource(localesR.string.income_plural),
                            onBackClick = {
                                onFinanceTypeUpdate(NOT_SET)
                                onDateTypeUpdate(ALL)
                            },
                            onDateTypeUpdate = onDateTypeUpdate,
                            onSelectedDateUpdate = onSelectedDateUpdate,
                            onCategoryFilterUpdate = onCategoryFilterUpdate,
                            animatedVisibilityScope = this@AnimatedContent,
                            sharedTransitionScope = this@SharedTransitionLayout,
                            availableCategories = availableCategories,
                            amountSharedContentState = incomeSharedState,
                            titleSharedContentState = incomeTitleSharedState,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FinanceCard(
    formattedAmount: String,
    title: String,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
    amountSharedContentState: Any,
    titleSharedContentState: Any,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    enabled: Boolean = true,
) {
    with(sharedTransitionScope) {
        val motionScheme = MaterialTheme.motionScheme
        OutlinedCard(
            modifier = modifier
                .sharedBoundsWithDefaults(
                    sharedContentState = rememberSharedContentState(
                        key = SharedElementKey(
                            id = title,
                            origin = title,
                            type = SharedElementType.Bounds,
                        ),
                    ),
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                    placeholderSize = SharedTransitionScope.PlaceholderSize.AnimatedSize,
                    resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(
                        ContentScale.FillWidth,
                        Center,
                    ),
                ),
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
                    modifier = Modifier
                        .sharedBounds(
                            boundsTransform = motionScheme.sharedElementTransitionSpec,
                            sharedContentState = rememberSharedContentState(amountSharedContentState),
                            animatedVisibilityScope = animatedVisibilityScope,
                        ),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .sharedBounds(
                            boundsTransform = motionScheme.sharedElementTransitionSpec,
                            sharedContentState = rememberSharedContentState(titleSharedContentState),
                            animatedVisibilityScope = animatedVisibilityScope,
                        ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun DetailedFinanceSection(
    formattedAmount: String,
    availableCategories: List<Category>,
    graphData: Map<Int, BigDecimal>,
    transactionFilter: TransactionFilter,
    currency: Currency,
    title: String,
    onBackClick: () -> Unit,
    onDateTypeUpdate: (DateType) -> Unit,
    onSelectedDateUpdate: (Int) -> Unit,
    onCategoryFilterUpdate: (Category, Boolean) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
    amountSharedContentState: Any,
    titleSharedContentState: Any,
    modifier: Modifier = Modifier,
) {
    with(sharedTransitionScope) {
        BackHandler(
            enabled = LocalNavAnimatedContentScope.current.transition.let {
                it.currentState == it.targetState
            },
            onBack = onBackClick,
        )
        Column(
            modifier = modifier
                .padding(horizontal = 16.dp)
                .sharedBoundsWithDefaults(
                    sharedContentState = rememberSharedContentState(
                        key = SharedElementKey(
                            id = title,
                            origin = title,
                            type = SharedElementType.Bounds,
                        ),
                    ),
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                    placeholderSize = SharedTransitionScope.PlaceholderSize.AnimatedSize,
                    resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(
                        ContentScale.FillWidth,
                        Center,
                    ),
                ),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
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
            DateRangeSelectionRow(
                onSelectedDateUpdate = onSelectedDateUpdate,
                transactionFilter = transactionFilter,
            )
            AnimatedAmount(
                formattedAmount = formattedAmount,
                label = "DetailedFinanceCard",
                modifier = Modifier
                    .padding(bottom = 6.dp)
                    .sharedBounds(
                        boundsTransform = MaterialTheme.motionScheme.sharedElementTransitionSpec,
                        sharedContentState = rememberSharedContentState(amountSharedContentState),
                        animatedVisibilityScope = animatedVisibilityScope,
                    ),
                style = MaterialTheme.typography.headlineLarge,
            )
            Text(
                text = title,
                modifier = Modifier
                    .sharedBounds(
                        boundsTransform = MaterialTheme.motionScheme.sharedElementTransitionSpec,
                        sharedContentState = rememberSharedContentState(titleSharedContentState),
                        animatedVisibilityScope = animatedVisibilityScope,
                    ),
                style = MaterialTheme.typography.labelLargeEmphasized,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (graphData.isNotEmpty() && transactionFilter.financeType != NOT_SET) {
                val modelProducer = remember { CartesianChartModelProducer() }
                LaunchedEffect(graphData) {
                    modelProducer.runTransaction {
                        if (graphData.isEmpty() || graphData.keys.size < 2) return@runTransaction
                        lineModel { series(graphData.keys, graphData.values) }
                    }
                }
                FinanceGraph(
                    transactionFilter = transactionFilter,
                    modelProducer = modelProducer,
                    currency = currency,
                )
            } else {
                FinanceGraphPlaceholder()
            }
            if (transactionFilter.financeType != NOT_SET) {
                CategorySelectionRow(
                    availableCategories = availableCategories,
                    selectedCategories = transactionFilter.selectedCategories,
                    onCategoryFilterUpdate = onCategoryFilterUpdate,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun FilterDateTypeSelectorRow(
    transactionFilter: TransactionFilter,
    onDateTypeUpdate: (DateType) -> Unit,
    modifier: Modifier = Modifier,
) {
    ConnectedToggleButtonGroup(
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun DateRangeSelectionRow(
    onSelectedDateUpdate: (Int) -> Unit,
    transactionFilter: TransactionFilter,
    modifier: Modifier = Modifier,
) {
    val locale = LocalLocale.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        val selectedDate = when (transactionFilter.dateType) {
            YEAR -> transactionFilter.selectedDate.year.toString()
            MONTH -> {
                val monthName = Month(transactionFilter.selectedDate.month.number)
                    .toJavaMonth()
                    .getDisplayName(
                        TextStyle.FULL_STANDALONE,
                        locale.platformLocale,
                    )
                    .replaceFirstChar { it.uppercaseChar() }

                if (transactionFilter.selectedDate.year != getCurrentYear()) {
                    "$monthName ${transactionFilter.selectedDate.year}"
                } else {
                    monthName
                }
            }

            WEEK -> {
                val date = transactionFilter.selectedDate.toJavaLocalDate()
                val firstDayOfWeek = WeekFields.of(locale.platformLocale).firstDayOfWeek
                val weekStart = date.with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
                val weekEnd = weekStart.plusDays(6)
                val formatter = DateTimeFormatter.ofPattern("d MMM", locale.platformLocale)
                runCatching {
                    "${formatter.format(weekStart)} — ${formatter.format(weekEnd)}"
                }.getOrDefault("")
            }

            ALL -> ""
        }
        Text(
            text = selectedDate,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.weight(1f),
        )
        ButtonGroup(
            overflowIndicator = {},
        ) {
            customItem(
                buttonGroupContent = {
                    val interactionSource = remember { MutableInteractionSource() }
                    Box(
                        modifier = Modifier.animateWidth(interactionSource),
                    ) {
                        CsFilledTonalIconButton(
                            onClick = { onSelectedDateUpdate(-1) },
                            icon = CsIcons.Outlined.ChevronLeft,
                            contentDescription = stringResource(localesR.string.previous_date),
                            containerSize = mediumContainerSize(IconButtonDefaults.IconButtonWidthOption.Narrow),
                            iconSize = IconButtonDefaults.mediumIconSize,
                            interactionSource = interactionSource,
                        )
                    }
                },
                menuContent = {},
            )
            customItem(
                buttonGroupContent = {
                    val interactionSource = remember { MutableInteractionSource() }
                    Box(
                        modifier = Modifier.animateWidth(interactionSource),
                    ) {
                        CsFilledTonalIconButton(
                            onClick = { onSelectedDateUpdate(1) },
                            icon = CsIcons.Outlined.ChevronRight,
                            contentDescription = stringResource(localesR.string.next_date),
                            containerSize = mediumContainerSize(IconButtonDefaults.IconButtonWidthOption.Narrow),
                            iconSize = IconButtonDefaults.mediumIconSize,
                            interactionSource = interactionSource,
                        )
                    }
                },
                menuContent = {},
            )
        }
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
                walletId = "",
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
                walletId = "",
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