package ru.resodostudios.cashsense.feature.transaction.overview.impl

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AppBarRow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarDefaults.ScreenOffset
import androidx.compose.material3.FloatingToolbarDefaults.floatingToolbarVerticalNestedScroll
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import ru.resodostudios.cashsense.core.designsystem.component.AnimatedIcon
import ru.resodostudios.cashsense.core.designsystem.component.CsAlertDialog
import ru.resodostudios.cashsense.core.designsystem.component.CsListItem
import ru.resodostudios.cashsense.core.designsystem.component.button.CsIconButton
import ru.resodostudios.cashsense.core.designsystem.component.button.CsIconToggleButton
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.filled.Csv
import ru.resodostudios.cashsense.core.designsystem.icon.filled.Delete
import ru.resodostudios.cashsense.core.designsystem.icon.filled.Edit
import ru.resodostudios.cashsense.core.designsystem.icon.filled.Star
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Add
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.ArrowBack
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Delete
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.MoreVert
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.SendMoney
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.SentimentCalm
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.SentimentExcited
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.SentimentFrustrated
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.SentimentNeutral
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.SentimentSad
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Star
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Wallet
import ru.resodostudios.cashsense.core.designsystem.theme.LocalSharedTransitionScope
import ru.resodostudios.cashsense.core.designsystem.theme.SharedElementKey
import ru.resodostudios.cashsense.core.designsystem.theme.SharedElementType
import ru.resodostudios.cashsense.core.designsystem.theme.sharedBoundsAdaptive
import ru.resodostudios.cashsense.core.model.Category
import ru.resodostudios.cashsense.core.model.DateType
import ru.resodostudios.cashsense.core.model.FinanceType
import ru.resodostudios.cashsense.core.model.Transaction
import ru.resodostudios.cashsense.core.model.Wallet
import ru.resodostudios.cashsense.core.ui.component.AnimatedAmount
import ru.resodostudios.cashsense.core.ui.component.FinancePanel
import ru.resodostudios.cashsense.core.ui.component.LoadingState
import ru.resodostudios.cashsense.core.ui.transactions
import ru.resodostudios.cashsense.core.ui.util.TrackScreenViewEvent
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
internal fun TransactionOverviewScreen(
    shouldShowNavigationIcon: Boolean,
    onBackClick: () -> Unit,
    onTransactionClick: (String) -> Unit,
    onTransfer: (String) -> Unit,
    onEditWallet: (String) -> Unit,
    onImportClick: (String) -> Unit,
    navigateToTransactionEditor: (walletId: String, transactionId: String?, repeated: Boolean) -> Unit,
    viewModel: TransactionOverviewViewModel = hiltViewModel(),
) {
    val financePanelUiState by viewModel.financePanelUiState.collectAsStateWithLifecycle()
    val transactionOverviewState by viewModel.transactionOverviewUiState.collectAsStateWithLifecycle()

    TransactionOverviewScreen(
        shouldShowNavigationIcon = shouldShowNavigationIcon,
        onBackClick = onBackClick,
        financePanelUiState = financePanelUiState,
        onDateTypeUpdate = viewModel::updateDateType,
        onFinanceTypeUpdate = viewModel::updateFinanceType,
        onSelectedDateUpdate = viewModel::updateSelectedDate,
        onCategoryFilterUpdate = viewModel::updateSelectedCategories,
        transactionOverviewState = transactionOverviewState,
        onTransactionSelect = {
            viewModel.updateSelectedTransaction(it)
            it?.id?.let { transaction -> onTransactionClick(transaction) }
        },
        onTransfer = onTransfer,
        onWalletEdit = onEditWallet,
        onWalletDelete = {
            viewModel.deleteWallet(it)
            onBackClick()
        },
        onImportClick = onImportClick,
        onPrimaryClick = viewModel::setPrimaryWalletId,
        navigateToTransactionEditor = navigateToTransactionEditor,
    )
}

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
private fun TransactionOverviewScreen(
    financePanelUiState: FinancePanelUiState,
    transactionOverviewState: TransactionOverviewUiState,
    shouldShowNavigationIcon: Boolean,
    onBackClick: () -> Unit,
    onDateTypeUpdate: (DateType) -> Unit,
    onFinanceTypeUpdate: (FinanceType) -> Unit,
    onSelectedDateUpdate: (Int) -> Unit,
    onCategoryFilterUpdate: (Category, Boolean) -> Unit,
    onTransactionSelect: (Transaction?) -> Unit = {},
    onTransfer: (String) -> Unit,
    onWalletEdit: (String) -> Unit,
    onWalletDelete: (String) -> Unit,
    onImportClick: (String) -> Unit,
    onPrimaryClick: (walletId: String, isPrimary: Boolean) -> Unit,
    navigateToTransactionEditor: (walletId: String, transactionId: String?, repeated: Boolean) -> Unit,
) {
    if (transactionOverviewState is TransactionOverviewUiState.Loading ||
        financePanelUiState is FinancePanelUiState.Loading
    ) {
        LoadingState(Modifier.fillMaxSize())
    }

    when (transactionOverviewState) {
        TransactionOverviewUiState.Loading -> Unit
        is TransactionOverviewUiState.Success -> {
            val hazeState = rememberHazeState()
            val hazeStyle = HazeMaterials.thick(MaterialTheme.colorScheme.tertiaryContainer)
            val motionScheme = MaterialTheme.motionScheme
            val dateTextColor = MaterialTheme.colorScheme.onTertiaryContainer
            val wallet = (financePanelUiState as? FinancePanelUiState.Shown)?.wallet

            with(LocalSharedTransitionScope.current) {
                Box(
                    modifier = Modifier
                        .then(
                            if (wallet != null) {
                                Modifier.sharedBoundsAdaptive(
                                    sharedContentState = rememberSharedContentState(
                                        key = SharedElementKey(
                                            id = wallet.id,
                                            origin = wallet.id,
                                            type = SharedElementType.Bounds,
                                        ),
                                    ),
                                    placeholderSize = SharedTransitionScope.PlaceholderSize.AnimatedSize,
                                    clipShape = MaterialTheme.shapes.extraLarge,
                                )
                            } else {
                                Modifier
                            }
                        ),
                ) {
                    var isWalletToolbarExpanded by rememberSaveable { mutableStateOf(true) }
                    if (wallet != null) {
                        WalletToolbar(
                            wallet = wallet,
                            formattedCurrentBalance = financePanelUiState.formattedTotalBalance,
                            expanded = isWalletToolbarExpanded,
                            onTransfer = onTransfer,
                            onWalletEdit = onWalletEdit,
                            onWalletDelete = onWalletDelete,
                            onImportClick = onImportClick,
                            navigateToTransactionEditor = navigateToTransactionEditor,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .navigationBarsPadding()
                                .offset(y = -ScreenOffset)
                                .zIndex(1f),
                        )
                    }
                    Column(
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                    ) {
                        TopBar(
                            financePanelUiState = financePanelUiState,
                            shouldShowNavigationIcon = shouldShowNavigationIcon,
                            onBackClick = onBackClick,
                            onPrimaryClick = onPrimaryClick,
                        )
                        LazyColumn(
                            contentPadding = PaddingValues(bottom = 96.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .then(
                                    if (wallet != null) {
                                        Modifier.floatingToolbarVerticalNestedScroll(
                                            expanded = isWalletToolbarExpanded,
                                            onExpand = { isWalletToolbarExpanded = true },
                                            onCollapse = { isWalletToolbarExpanded = false },
                                        )
                                    } else {
                                        Modifier
                                    }
                                ),
                        ) {
                            header(
                                financePanelUiState = financePanelUiState,
                                onDateTypeUpdate = onDateTypeUpdate,
                                onFinanceTypeUpdate = onFinanceTypeUpdate,
                                onSelectedDateUpdate = onSelectedDateUpdate,
                                onCategoryFilterUpdate = onCategoryFilterUpdate,
                            )
                            transactions(
                                selectedTransaction = transactionOverviewState.selectedTransaction,
                                groupedTransactions = transactionOverviewState.groupedTransactions,
                                walletIdsAndTitles = transactionOverviewState.walletIdsAndTitles,
                                hazeState = hazeState,
                                hazeStyle = hazeStyle,
                                onClick = onTransactionSelect,
                                motionScheme = motionScheme,
                                dateTextColor = dateTextColor,
                            )
                        }
                    }
                }
            }
        }
    }
    TrackScreenViewEvent(screenName = "TransactionOverview")
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
private fun TopBar(
    financePanelUiState: FinancePanelUiState,
    shouldShowNavigationIcon: Boolean,
    onBackClick: () -> Unit,
    onPrimaryClick: (walletId: String, isPrimary: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (financePanelUiState) {
        FinancePanelUiState.Loading -> Unit
        FinancePanelUiState.NotShown -> Unit
        is FinancePanelUiState.Shown -> {
            with(LocalSharedTransitionScope.current) {
                TopAppBar(
                    title = {
                        Text(
                            text = if (financePanelUiState.wallet != null) {
                                financePanelUiState.wallet.title
                            } else {
                                stringResource(localesR.string.total_balance)
                            },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = if (financePanelUiState.wallet != null) {
                                Modifier.sharedBoundsAdaptive(
                                    sharedContentState = rememberSharedContentState(
                                        key = SharedElementKey(
                                            id = financePanelUiState.wallet.id,
                                            origin = financePanelUiState.wallet.id,
                                            type = SharedElementType.WalletTitle,
                                        ),
                                    ),
                                    resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(),
                                )
                            } else {
                                Modifier
                            },
                        )
                    },
                    subtitle = {
                        AnimatedAmount(
                            formattedAmount = financePanelUiState.formattedTotalBalance,
                            label = if (financePanelUiState.wallet != null) "WalletBalance" else "TotalBalance",
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(
                                    if (financePanelUiState.wallet != null) {
                                        Modifier.sharedBoundsAdaptive(
                                            sharedContentState = rememberSharedContentState(
                                                key = SharedElementKey(
                                                    id = financePanelUiState.wallet.id,
                                                    origin = financePanelUiState.wallet.id,
                                                    type = SharedElementType.BalanceAmount,
                                                ),
                                            ),
                                            resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(),
                                        )
                                    } else {
                                        Modifier
                                    }
                                ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    navigationIcon = {
                        if (shouldShowNavigationIcon) {
                            CsIconButton(
                                onClick = onBackClick,
                                icon = CsIcons.Outlined.ArrowBack,
                                contentDescription = stringResource(localesR.string.navigation_back_icon_description),
                                tooltipPosition = TooltipAnchorPosition.Right,
                            )
                        }
                    },
                    actions = {
                        if (financePanelUiState.wallet != null) {
                            PrimaryToggleButton(
                                isPrimary = financePanelUiState.isPrimary,
                                onPrimaryClick = {
                                    onPrimaryClick(
                                        financePanelUiState.wallet.id,
                                        !financePanelUiState.isPrimary
                                    )
                                },
                            )
                        } else {
                            FinancialHealthIcon(
                                financialHealth = financePanelUiState.financialHealth,
                                modifier = Modifier.padding(end = 8.dp),
                            )
                        }
                    },
                    modifier = modifier,
                )
            }
        }
    }
}

private fun LazyListScope.header(
    financePanelUiState: FinancePanelUiState,
    onDateTypeUpdate: (DateType) -> Unit,
    onFinanceTypeUpdate: (FinanceType) -> Unit,
    onSelectedDateUpdate: (Int) -> Unit,
    onCategoryFilterUpdate: (Category, Boolean) -> Unit,
) {
    when (financePanelUiState) {
        FinancePanelUiState.Loading -> Unit
        FinancePanelUiState.NotShown -> Unit
        is FinancePanelUiState.Shown -> {
            item {
                FinancePanel(
                    walletId = financePanelUiState.wallet?.id ?: "",
                    availableCategories = financePanelUiState.availableCategories,
                    currency = financePanelUiState.userCurrency,
                    formattedExpenses = financePanelUiState.formattedExpenses,
                    formattedIncome = financePanelUiState.formattedIncome,
                    graphData = financePanelUiState.graphData,
                    transactionFilter = financePanelUiState.transactionFilter,
                    onDateTypeUpdate = onDateTypeUpdate,
                    onFinanceTypeUpdate = onFinanceTypeUpdate,
                    onSelectedDateUpdate = onSelectedDateUpdate,
                    onCategoryFilterUpdate = onCategoryFilterUpdate,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun FinancialHealthIcon(
    financialHealth: FinancialHealth,
    modifier: Modifier = Modifier,
) {
    val badColor = MaterialTheme.colorScheme.errorContainer
    val neutralColor = MaterialTheme.colorScheme.surfaceVariant
    val goodColor = MaterialTheme.colorScheme.primaryContainer

    val (icon, targetColor) = when (financialHealth) {
        FinancialHealth.VERY_BAD -> CsIcons.Outlined.SentimentFrustrated to badColor
        FinancialHealth.BAD -> CsIcons.Outlined.SentimentSad to badColor
        FinancialHealth.NEUTRAL -> CsIcons.Outlined.SentimentNeutral to neutralColor
        FinancialHealth.GOOD -> CsIcons.Outlined.SentimentCalm to goodColor
        FinancialHealth.VERY_GOOD -> CsIcons.Outlined.SentimentExcited to goodColor
    }
    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        label = "FinancialHealthColor",
        animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
    )
    Surface(
        shape = CircleShape,
        color = animatedColor,
        modifier = modifier,
    ) {
        AnimatedIcon(
            icon = icon,
            contentDescription = stringResource(localesR.string.financial_health),
            modifier = Modifier.padding(4.dp),
        )
    }
}

@Composable
private fun WalletToolbar(
    wallet: Wallet,
    formattedCurrentBalance: String,
    expanded: Boolean,
    onTransfer: (String) -> Unit,
    onWalletEdit: (String) -> Unit,
    onWalletDelete: (String) -> Unit,
    onImportClick: (String) -> Unit,
    navigateToTransactionEditor: (String, String?, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var shouldShowDeletionDialog by rememberSaveable { mutableStateOf(false) }
    HorizontalFloatingToolbar(
        colors = FloatingToolbarDefaults.vibrantFloatingToolbarColors(),
        modifier = modifier,
        expanded = expanded,
        leadingContent = {
            IconButton(
                onClick = { onTransfer(wallet.id) },
            ) {
                Icon(
                    imageVector = CsIcons.Outlined.SendMoney,
                    contentDescription = stringResource(localesR.string.transfer),
                )
            }
        },
        trailingContent = {
            val importButtonLabel = stringResource(localesR.string.import_csv)
            val editButtonLabel = stringResource(localesR.string.edit)
            val deleteButtonLabel = stringResource(localesR.string.delete)
            AppBarRow(
                maxItemCount = 1,
                overflowIndicator = { menuState ->
                    IconButton(
                        onClick = {
                            if (menuState.isShowing) menuState.dismiss() else menuState.show()
                        },
                    ) {
                        Icon(
                            imageVector = CsIcons.Outlined.MoreVert,
                            contentDescription = stringResource(localesR.string.wallet_menu_icon_description),
                        )
                    }
                },
            ) {
                clickableItem(
                    onClick = { onImportClick(wallet.id) },
                    icon = {
                        Icon(
                            imageVector = CsIcons.Filled.Csv,
                            contentDescription = importButtonLabel,
                        )
                    },
                    label = importButtonLabel,
                )
                clickableItem(
                    onClick = { onWalletEdit(wallet.id) },
                    icon = {
                        Icon(
                            imageVector = CsIcons.Filled.Edit,
                            contentDescription = editButtonLabel,
                        )
                    },
                    label = editButtonLabel,
                )
                clickableItem(
                    onClick = { shouldShowDeletionDialog = true },
                    icon = {
                        Icon(
                            imageVector = CsIcons.Filled.Delete,
                            contentDescription = deleteButtonLabel,
                        )
                    },
                    label = deleteButtonLabel,
                )
            }
        },
        content = {
            FilledIconButton(
                modifier = Modifier.width(64.dp),
                onClick = { navigateToTransactionEditor(wallet.id, null, false) },
            ) {
                Icon(
                    imageVector = CsIcons.Outlined.Add,
                    contentDescription = stringResource(localesR.string.add_transaction),
                )
            }
        }
    )
    if (shouldShowDeletionDialog) {
        CsAlertDialog(
            titleRes = localesR.string.delete_wallet,
            icon = CsIcons.Outlined.Delete,
            confirmButtonTextRes = localesR.string.delete,
            dismissButtonTextRes = localesR.string.cancel,
            onConfirm = {
                onWalletDelete(wallet.id)
                shouldShowDeletionDialog = false
            },
            onDismiss = { shouldShowDeletionDialog = false },
            content = {
                Column {
                    Text(stringResource(localesR.string.permanently_delete_wallet))
                    CsListItem(
                        headlineContent = {
                            Text(
                                text = wallet.title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                        supportingContent = {
                            Text(
                                text = formattedCurrentBalance,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = CsIcons.Outlined.Wallet,
                                contentDescription = null,
                            )
                        },
                    )
                }
            },
        )
    }
}

@Composable
private fun PrimaryToggleButton(
    isPrimary: Boolean,
    onPrimaryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (icon, contentDescription) = if (isPrimary) {
        CsIcons.Filled.Star to stringResource(localesR.string.non_primary_icon_description)
    } else {
        CsIcons.Outlined.Star to stringResource(localesR.string.primary_icon_description)
    }
    CsIconToggleButton(
        checked = isPrimary,
        onCheckedChange = { onPrimaryClick() },
        icon = icon,
        contentDescription = contentDescription,
        modifier = modifier,
        tooltipPosition = TooltipAnchorPosition.Left,
    )
}
