package ru.resodostudios.cashsense.feature.home.impl

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.ExpandedFullScreenContainedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarScrollBehavior
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.designsystem.component.CsSelectableListItem
import ru.resodostudios.cashsense.core.designsystem.component.CsTag
import ru.resodostudios.cashsense.core.designsystem.component.button.CsIconButton
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.AccountBalance
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.ArrowBack
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Block
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Close
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Pending
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.SendMoney
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Settings
import ru.resodostudios.cashsense.core.model.data.DateFormatType
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.ui.component.StoredIcon
import ru.resodostudios.cashsense.core.ui.groupByDate
import ru.resodostudios.cashsense.core.ui.util.formatAmount
import ru.resodostudios.cashsense.core.ui.util.formatDate
import java.time.format.FormatStyle
import java.util.Currency
import ru.resodostudios.cashsense.core.locales.R as localesR

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalHazeMaterialsApi::class,
    ExperimentalHazeApi::class,
)
@Composable
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
            searchResults.groupByDate().forEach { transactionGroup ->
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
                    contentType = { _, _ -> "Transaction" }
                ) { index, transaction ->
                    val motionScheme = MaterialTheme.motionScheme
                    SearchResultItem(
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
                            ListItemDefaults.segmentedShapes(index, transactionGroup.value.size)
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun SearchResultItem(
    transaction: Transaction,
    currency: Currency,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shapes: ListItemShapes = ListItemDefaults.shapes(),
) {
    val (categoryIcon, categoryTitle) = if (transaction.transferId != null) {
        CsIcons.Outlined.SendMoney to stringResource(localesR.string.transfers)
    } else {
        val iconId = transaction.category?.iconId ?: StoredIcon.TRANSACTION.storedId
        val title = transaction.category?.title ?: stringResource(localesR.string.uncategorized)
        StoredIcon.asImageVector(iconId) to title
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
                text = buildString {
                    append(categoryTitle)
                    transaction.description?.let {
                        append(" • $it")
                    }
                },
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
    )
}
