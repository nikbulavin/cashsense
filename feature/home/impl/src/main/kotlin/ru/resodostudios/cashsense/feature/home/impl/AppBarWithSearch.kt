package ru.resodostudios.cashsense.feature.home.impl

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerDefaults
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.ExpandedFullScreenContainedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarScrollBehavior
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.getSelectedEndDate
import androidx.compose.material3.getSelectedStartDate
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import ru.resodostudios.cashsense.core.designsystem.component.AnimatedIcon
import ru.resodostudios.cashsense.core.designsystem.component.CsSelectableListItem
import ru.resodostudios.cashsense.core.designsystem.component.CsTag
import ru.resodostudios.cashsense.core.designsystem.component.button.CsIconButton
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.filled.AccountBalance
import ru.resodostudios.cashsense.core.designsystem.icon.filled.ArrowDropDown
import ru.resodostudios.cashsense.core.designsystem.icon.filled.ArrowDropUp
import ru.resodostudios.cashsense.core.designsystem.icon.filled.Settings
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.ArrowBack
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Block
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Calendar
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Check
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Close
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Pending
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.SendMoney
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Wallet
import ru.resodostudios.cashsense.core.model.data.DateFormatType
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.ui.component.IllustratedMessage
import ru.resodostudios.cashsense.core.ui.component.LoadingState
import ru.resodostudios.cashsense.core.ui.component.StoredIcon
import ru.resodostudios.cashsense.core.ui.groupByDate
import ru.resodostudios.cashsense.core.ui.util.formatAmount
import ru.resodostudios.cashsense.core.ui.util.formatDate
import ru.resodostudios.cashsense.core.ui.util.formatDateRange
import java.time.format.FormatStyle
import java.util.Currency
import kotlin.time.Duration.Companion.milliseconds
import ru.resodostudios.cashsense.core.locales.R as localesR

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalHazeMaterialsApi::class,
    ExperimentalHazeApi::class,
    FlowPreview::class,
)
@Composable
internal fun CsAppBarWithSearch(
    scrollBehavior: SearchBarScrollBehavior,
    searchResultState: SearchResultUiState,
    searchFilterState: SearchFilterState,
    walletIdsAndTitles: Map<String, String>,
    onSearch: (String) -> Unit,
    onSearchFilterWalletToggle: (String) -> Unit,
    onSearchFilterDateRangeChange: (LocalDate?, LocalDate?) -> Unit,
    onTransactionClick: (transactionId: String) -> Unit,
    onTotalBalanceClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    val textFieldState = rememberTextFieldState()
    val searchBarState = rememberSearchBarState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(textFieldState) {
        snapshotFlow { textFieldState.text }
            .debounce(300.milliseconds)
            .collectLatest { onSearch(it.toString()) }
    }

    val appBarWithSearchColors = SearchBarDefaults.appBarWithSearchColors(
        searchBarColors = SearchBarDefaults.containedColors(state = searchBarState),
    )
    val isSearchBarExpanded = searchBarState.targetValue == SearchBarValue.Expanded
    val inputField = @Composable {
        SearchBarDefaults.InputField(
            textFieldState = textFieldState,
            searchBarState = searchBarState,
            colors = appBarWithSearchColors.searchBarColors.inputFieldColors,
            onSearch = {
                scope.launch { searchBarState.animateToCollapsed() }
                textFieldState.clearText()
            },
            placeholder = {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clearAndSetSemantics {},
                    text = stringResource(localesR.string.search),
                    textAlign = if (isSearchBarExpanded) TextAlign.Left else TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            leadingIcon = if (isSearchBarExpanded) {
                {
                    CsIconButton(
                        onClick = {
                            scope.launch { searchBarState.animateToCollapsed() }
                            textFieldState.clearText()
                        },
                        icon = CsIcons.Outlined.ArrowBack,
                        contentDescription = stringResource(localesR.string.navigation_back_icon_description),
                        tooltipPosition = TooltipAnchorPosition.Right,
                    )
                }
            } else null,
            trailingIcon = if (isSearchBarExpanded && textFieldState.text.isNotEmpty()) {
                {
                    CsIconButton(
                        onClick = textFieldState::clearText,
                        icon = CsIcons.Outlined.Close,
                        contentDescription = stringResource(localesR.string.delete),
                        tooltipPosition = TooltipAnchorPosition.Left,
                    )
                }
            } else null,
        )
    }
    AppBarWithSearch(
        scrollBehavior = scrollBehavior,
        state = searchBarState,
        colors = appBarWithSearchColors,
        inputField = inputField,
        navigationIcon = {
            CsIconButton(
                onClick = onTotalBalanceClick,
                icon = CsIcons.Filled.AccountBalance,
                contentDescription = stringResource(localesR.string.total_balance),
                tooltipPosition = TooltipAnchorPosition.Right,
            )
        },
        actions = {
            CsIconButton(
                onClick = onSettingsClick,
                icon = CsIcons.Filled.Settings,
                contentDescription = stringResource(localesR.string.settings_title),
                tooltipPosition = TooltipAnchorPosition.Left,
            )
        },
    )
    ExpandedFullScreenContainedSearchBar(
        state = searchBarState,
        inputField = inputField,
        colors = appBarWithSearchColors.searchBarColors,
    ) {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DateFilterChip(
                selectedDateRange = searchFilterState.selectedDateRange,
                onDateRangeUpdate = onSearchFilterDateRangeChange,
            )
            WalletFilterChip(
                walletIdsAndTitles = walletIdsAndTitles,
                selectedWalletIds = searchFilterState.selectedWalletIds,
                onSearchFilterWalletToggle = onSearchFilterWalletToggle,
            )
        }
        when (searchResultState) {
            SearchResultUiState.EmptyQuery, SearchResultUiState.LoadFailed -> Unit
            SearchResultUiState.Loading -> LoadingState(Modifier.fillMaxSize())
            is SearchResultUiState.Success -> {
                if (searchResultState.transactions.isEmpty()) {
                    IllustratedMessage(
                        messageRes = localesR.string.search_no_results,
                        animationRes = R.raw.anim_search_no_results,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                    )
                } else {
                    val hazeState = rememberHazeState()
                    val hazeStyle = HazeMaterials.ultraThin(
                        MaterialTheme.colorScheme.secondaryContainer,
                    )
                    LazyColumn {
                        searchResultState.transactions.groupByDate().forEach { transactionGroup ->
                            stickyHeader(
                                contentType = "Date",
                            ) {
                                CsTag(
                                    text = transactionGroup.key.formatDate(
                                        DateFormatType.DATE,
                                        FormatStyle.MEDIUM
                                    ),
                                    color = Color.Transparent,
                                    modifier = Modifier
                                        .padding(start = 16.dp, top = 16.dp)
                                        .clip(CircleShape)
                                        .hazeEffect(hazeState, hazeStyle) {
                                            blurEnabled = true
                                            blurRadius = 10.dp
                                            noiseFactor = 0f
                                            inputScale = HazeInputScale.Auto
                                        },
                                )
                            }
                            item { Spacer(Modifier.height(16.dp)) }
                            itemsIndexed(
                                items = transactionGroup.value,
                                key = { _, transaction -> transaction.id },
                                contentType = { _, _ -> "Transaction" },
                            ) { index, transaction ->
                                val motionScheme = MaterialTheme.motionScheme
                                SearchResultItem(
                                    walletIdsAndTitles = walletIdsAndTitles,
                                    transaction = transaction,
                                    currency = transaction.currency,
                                    modifier = Modifier
                                        .hazeSource(hazeState)
                                        .padding(horizontal = 16.dp)
                                        .animateItem(
                                            fadeInSpec = motionScheme.defaultEffectsSpec(),
                                            fadeOutSpec = motionScheme.defaultEffectsSpec(),
                                            placementSpec = motionScheme.defaultSpatialSpec(),
                                        ),
                                    onClick = { onTransactionClick(transaction.id) },
                                    shapes = if (transactionGroup.value.size == 1) {
                                        ListItemDefaults.shapes(shape = RoundedCornerShape(16.dp))
                                    } else {
                                        ListItemDefaults.segmentedShapes(
                                            index,
                                            transactionGroup.value.size,
                                        )
                                    },
                                )
                                if (index != transactionGroup.value.lastIndex) {
                                    Spacer(Modifier.height(ListItemDefaults.SegmentedGap))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SearchResultItem(
    walletIdsAndTitles: Map<String, String>,
    transaction: Transaction,
    currency: Currency,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shapes: ListItemShapes = ListItemDefaults.shapes(),
) {
    val categoryIcon = if (transaction.transferId != null) {
        CsIcons.Outlined.SendMoney
    } else {
        StoredIcon.asImageVector(transaction.category?.iconId ?: StoredIcon.TRANSACTION.storedId)
    }

    val effectsSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Float>()
    val floatSpatialSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
    val intSizeSpatialSpec = MaterialTheme.motionScheme.defaultSpatialSpec<IntSize>()

    CsSelectableListItem(
        shapes = shapes,
        onClick = onClick,
        selected = false,
        modifier = modifier,
        content = {
            Text(
                text = transaction.amount.formatAmount(currency, true),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        supportingContent = {
            Text(
                text = walletIdsAndTitles[transaction.walletOwnerId]
                    ?: stringResource(localesR.string.none),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        },
        trailingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.animateContentSize(intSizeSpatialSpec),
            ) {
                AnimatedVisibility(
                    visible = transaction.ignored,
                    enter = fadeIn(effectsSpec) + scaleIn(floatSpatialSpec),
                    exit = fadeOut(effectsSpec) + scaleOut(floatSpatialSpec),
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = MaterialShapes.PixelCircle.toShape(),
                    ) {
                        Icon(
                            imageVector = CsIcons.Outlined.Block,
                            contentDescription = stringResource(localesR.string.transaction_ignore),
                            modifier = Modifier
                                .padding(4.dp)
                                .size(20.dp),
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                        )
                    }
                }
                AnimatedVisibility(
                    visible = !transaction.completed,
                    enter = fadeIn(effectsSpec) + scaleIn(floatSpatialSpec),
                    exit = fadeOut(effectsSpec) + scaleOut(floatSpatialSpec),
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = MaterialShapes.Clover4Leaf.toShape(),
                        modifier = Modifier.padding(start = 8.dp),
                    ) {
                        Icon(
                            imageVector = CsIcons.Outlined.Pending,
                            contentDescription = stringResource(localesR.string.pending),
                            modifier = Modifier
                                .padding(4.dp)
                                .size(20.dp),
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        )
                    }
                }
            }
        },
        leadingContent = {
            Icon(
                imageVector = categoryIcon,
                contentDescription = null,
            )
        },
        colors = ListItemDefaults.segmentedColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateFilterChip(
    selectedDateRange: Pair<LocalDate, LocalDate>?,
    onDateRangeUpdate: (LocalDate?, LocalDate?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selected = selectedDateRange != null
    var showDatePicker by remember { mutableStateOf(false) }

    val label = if (selectedDateRange != null) {
        val (start, end) = selectedDateRange
        formatDateRange(start, end)
    } else {
        stringResource(localesR.string.date)
    }

    FilterChip(
        modifier = modifier,
        selected = selected,
        onClick = { showDatePicker = true },
        label = {
            Text(
                text = label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        leadingIcon = {
            AnimatedIcon(
                icon = if (selected) CsIcons.Outlined.Check else CsIcons.Outlined.Calendar,
                iconSize = FilterChipDefaults.IconSize,
            )
        },
        trailingIcon = {
            if (selected) {
                Icon(
                    imageVector = CsIcons.Outlined.Close,
                    contentDescription = null,
                    modifier = Modifier
                        .size(FilterChipDefaults.IconSize)
                        .clickable { onDateRangeUpdate(null, null) },
                )
            } else {
                AnimatedIcon(
                    icon = if (showDatePicker) CsIcons.Filled.ArrowDropUp else CsIcons.Filled.ArrowDropDown,
                    modifier = Modifier.size(FilterChipDefaults.IconSize),
                )
            }
        },
        shapes = FilterChipDefaults.shapes(),
    )

    if (showDatePicker) {
        val dateRangePickerState = rememberDateRangePickerState(
            initialSelectedStartDate = selectedDateRange?.first?.toJavaLocalDate(),
            initialSelectedEndDate = selectedDateRange?.second?.toJavaLocalDate(),
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDateRangeUpdate(
                            dateRangePickerState.getSelectedStartDate()?.toKotlinLocalDate(),
                            dateRangePickerState.getSelectedEndDate()?.toKotlinLocalDate(),
                        )
                        showDatePicker = false
                    },
                    enabled = dateRangePickerState.selectedStartDateMillis != null && dateRangePickerState.selectedEndDateMillis != null,
                ) {
                    Text(stringResource(localesR.string.ok))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false },
                ) {
                    Text(stringResource(localesR.string.cancel))
                }
            },
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                title = {
                    DateRangePickerDefaults.DateRangePickerTitle(
                        displayMode = dateRangePickerState.displayMode,
                        modifier = Modifier.padding(start = 24.dp, end = 12.dp, top = 16.dp),
                    )
                },
                headline = {
                    DateRangePickerDefaults.DateRangePickerHeadline(
                        selectedStartDateMillis = dateRangePickerState.selectedStartDateMillis,
                        selectedEndDateMillis = dateRangePickerState.selectedEndDateMillis,
                        displayMode = dateRangePickerState.displayMode,
                        dateFormatter = remember { DatePickerDefaults.dateFormatter() },
                        modifier = Modifier.padding(start = 24.dp, end = 12.dp, bottom = 12.dp),
                    )
                },
            )
        }
    }
}

@Composable
private fun WalletFilterChip(
    walletIdsAndTitles: Map<String, String>,
    selectedWalletIds: List<String>,
    onSearchFilterWalletToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selected = selectedWalletIds.isNotEmpty()
    val hapticFeedback = LocalHapticFeedback.current

    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier,
    ) {
        FilterChip(
            selected = selected,
            onClick = { expanded = true },
            label = {
                Text(
                    text = buildString {
                        append(stringResource(localesR.string.wallet_widget_title))
                        if (selected) append(" (${selectedWalletIds.size})")
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            leadingIcon = {
                AnimatedIcon(
                    icon = if (selected) CsIcons.Outlined.Check else CsIcons.Outlined.Wallet,
                    iconSize = FilterChipDefaults.IconSize,
                )
            },
            trailingIcon = {
                AnimatedIcon(
                    icon = if (expanded) CsIcons.Filled.ArrowDropUp else CsIcons.Filled.ArrowDropDown,
                    iconSize = FilterChipDefaults.IconSize,
                )
            },
            shapes = FilterChipDefaults.shapes(),
        )
        DropdownMenuPopup(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuGroup(
                shapes = MenuDefaults.groupShape(0, 1),
                containerColor = MenuDefaults.groupVibrantContainerColor,
            ) {
                walletIdsAndTitles.entries.forEachIndexed { index, (id, title) ->
                    DropdownMenuItem(
                        checked = id in selectedWalletIds,
                        onCheckedChange = { checked ->
                            hapticFeedback.performHapticFeedback(
                                if (checked) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff,
                            )
                            onSearchFilterWalletToggle(id)
                        },
                        text = { Text(title) },
                        shapes = MenuDefaults.itemShape(index, walletIdsAndTitles.size),
                        checkedLeadingIcon = {
                            Icon(
                                imageVector = CsIcons.Outlined.Check,
                                modifier = Modifier.size(MenuDefaults.LeadingIconSize),
                                contentDescription = null,
                            )
                        },
                        colors = MenuDefaults.selectableItemVibrantColors(),
                    )
                }
            }
        }
    }
}
