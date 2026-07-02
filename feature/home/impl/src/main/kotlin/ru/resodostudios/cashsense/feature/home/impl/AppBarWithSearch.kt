package ru.resodostudios.cashsense.feature.home.impl

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarScrollBehavior
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.getSelectedEndDate
import androidx.compose.material3.getSelectedStartDate
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.ExperimentalHazeApi
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
import ru.resodostudios.cashsense.core.designsystem.component.button.CsIconButton
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.filled.AccountBalance
import ru.resodostudios.cashsense.core.designsystem.icon.filled.ArrowDropDown
import ru.resodostudios.cashsense.core.designsystem.icon.filled.ArrowDropUp
import ru.resodostudios.cashsense.core.designsystem.icon.filled.Settings
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.ArrowBack
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Calendar
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Check
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Close
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Wallet
import ru.resodostudios.cashsense.core.ui.component.IllustratedMessage
import ru.resodostudios.cashsense.core.ui.component.LoadingState
import ru.resodostudios.cashsense.core.ui.transactions
import ru.resodostudios.cashsense.core.ui.util.formatDateRange
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
            onSearch = {},
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
                enabled = walletIdsAndTitles.isNotEmpty(),
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
        val hazeState = rememberHazeState()
        val hazeStyle = HazeMaterials.thick(MaterialTheme.colorScheme.tertiaryContainer)
        val motionScheme = MaterialTheme.motionScheme
        val dateTextColor = MaterialTheme.colorScheme.onTertiaryContainer
        val transactionContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        LazyColumn(
            contentPadding = PaddingValues(bottom = 16.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    item {
                        DateFilterChip(
                            selectedDateRange = searchFilterState.selectedDateRange,
                            onDateRangeUpdate = onSearchFilterDateRangeChange,
                        )
                    }
                    item {
                        WalletFilterChip(
                            walletIdsAndTitles = walletIdsAndTitles,
                            selectedWalletIds = searchFilterState.selectedWalletIds,
                            onSearchFilterWalletToggle = onSearchFilterWalletToggle,
                        )
                    }
                }
            }
            when (searchResultState) {
                SearchResultUiState.EmptyQuery, SearchResultUiState.LoadFailed -> Unit
                SearchResultUiState.Loading -> item { LoadingState(Modifier.fillParentMaxSize()) }
                is SearchResultUiState.Success -> {
                    if (searchResultState.groupedTransactions.isEmpty()) {
                        item {
                            IllustratedMessage(
                                messageRes = localesR.string.search_no_results,
                                animationRes = R.raw.anim_search_no_results,
                                modifier = Modifier
                                    .fillParentMaxSize()
                                    .padding(16.dp),
                            )
                        }
                    } else {
                        transactions(
                            groupedTransactions = searchResultState.groupedTransactions,
                            onClick = { transaction ->
                                transaction?.id?.let { onTransactionClick(it) }
                            },
                            hazeState = hazeState,
                            hazeStyle = hazeStyle,
                            motionScheme = motionScheme,
                            dateTextColor = dateTextColor,
                            transactionContainerColor = transactionContainerColor,
                            isSharedTransitionEnabled = false,
                            walletIdsAndTitles = walletIdsAndTitles,
                        )
                    }
                }
            }
        }
    }
}

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
        modifier = modifier.animateContentSize(),
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
            modifier = Modifier.animateContentSize(),
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
