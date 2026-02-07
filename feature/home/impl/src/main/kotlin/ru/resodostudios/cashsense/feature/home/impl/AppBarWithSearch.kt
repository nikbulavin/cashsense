package ru.resodostudios.cashsense.feature.home.impl

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.ExpandedFullScreenContainedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarScrollBehavior
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.designsystem.component.button.CsIconButton
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.AccountBalance
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.ArrowBack
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Close
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Settings
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.ui.groupByDate
import ru.resodostudios.cashsense.core.ui.transactions
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
@OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalHazeMaterialsApi::class,
)
internal fun CsAppBarWithSearch(
    scrollBehavior: SearchBarScrollBehavior,
    searchResults: List<Transaction>,
    onSearch: (String) -> Unit,
    onTransactionClick: (transactionId: String) -> Unit,
    onTotalBalanceClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    val textFieldState = rememberTextFieldState()
    val searchBarState = rememberSearchBarState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(textFieldState) {
        snapshotFlow { textFieldState.text }
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
            onSearch = { scope.launch { searchBarState.animateToCollapsed() } },
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
                        onClick = { scope.launch { searchBarState.animateToCollapsed() } },
                        icon = CsIcons.Outlined.ArrowBack,
                        contentDescription = stringResource(localesR.string.navigation_back_icon_description),
                        tooltipPosition = TooltipAnchorPosition.Right,
                    )
                }
            } else null,
            trailingIcon = if (isSearchBarExpanded && textFieldState.text.isNotEmpty()) {
                {
                    CsIconButton(
                        onClick = { textFieldState.clearText() },
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
                icon = CsIcons.Outlined.AccountBalance,
                contentDescription = stringResource(localesR.string.total_balance),
                tooltipPosition = TooltipAnchorPosition.Right,
            )
        },
        actions = {
            CsIconButton(
                onClick = onSettingsClick,
                icon = CsIcons.Outlined.Settings,
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
        val hazeStyle = HazeMaterials.ultraThin(MaterialTheme.colorScheme.secondaryContainer)
        LazyColumn {
            transactions(
                groupedTransactions = searchResults.groupByDate(),
                hazeState = hazeState,
                hazeStyle = hazeStyle,
                onClick = { transaction ->
                    if (transaction != null) {
                        onTransactionClick(transaction.id)
                    }
                },
            )
        }
    }
}
