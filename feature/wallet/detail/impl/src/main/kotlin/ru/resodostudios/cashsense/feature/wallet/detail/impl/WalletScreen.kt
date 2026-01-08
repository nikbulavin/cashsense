package ru.resodostudios.cashsense.feature.wallet.detail.impl

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import ru.resodostudios.cashsense.core.designsystem.component.CsAlertDialog
import ru.resodostudios.cashsense.core.designsystem.component.CsListItem
import ru.resodostudios.cashsense.core.designsystem.component.button.CsIconButton
import ru.resodostudios.cashsense.core.designsystem.component.button.CsIconToggleButton
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.filled.Star
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Add
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.ArrowBack
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Delete
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Edit
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.MoreVert
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.SendMoney
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Star
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Wallet
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.designsystem.theme.LocalSharedTransitionScope
import ru.resodostudios.cashsense.core.designsystem.theme.SharedElementKey
import ru.resodostudios.cashsense.core.designsystem.theme.sharedElementTransitionSpec
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.DateType
import ru.resodostudios.cashsense.core.model.data.FinanceType
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.model.data.TransactionFilter
import ru.resodostudios.cashsense.core.model.data.Wallet
import ru.resodostudios.cashsense.core.ui.TransactionPreviewParameterProvider
import ru.resodostudios.cashsense.core.ui.component.AnimatedAmount
import ru.resodostudios.cashsense.core.ui.component.FinancePanel
import ru.resodostudios.cashsense.core.ui.component.LoadingState
import ru.resodostudios.cashsense.core.ui.groupByDate
import ru.resodostudios.cashsense.core.ui.transactions
import ru.resodostudios.cashsense.core.ui.util.TrackScreenViewEvent
import ru.resodostudios.cashsense.core.ui.util.getCurrentZonedDateTime
import ru.resodostudios.cashsense.core.util.getUsdCurrency
import java.math.BigDecimal
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
internal fun WalletScreen(
    onBackClick: () -> Unit,
    onTransactionClick: (String) -> Unit,
    onTransfer: (String) -> Unit,
    onEditWallet: (String) -> Unit,
    shouldShowNavigationIcon: Boolean,
    shouldHighlightSelectedTransaction: Boolean,
    navigateToTransactionDialog: (walletId: String, transactionId: String?, repeated: Boolean) -> Unit,
    viewModel: WalletViewModel = hiltViewModel(),
) {
    val walletState by viewModel.walletUiState.collectAsStateWithLifecycle()

    WalletScreen(
        walletState = walletState,
        shouldShowNavigationIcon = shouldShowNavigationIcon,
        shouldHighlightSelectedTransaction = shouldHighlightSelectedTransaction,
        onPrimaryClick = viewModel::setPrimaryWalletId,
        onTransfer = onTransfer,
        onWalletEdit = onEditWallet,
        onWalletDelete = viewModel::deleteWallet,
        onBackClick = onBackClick,
        navigateToTransactionDialog = navigateToTransactionDialog,
        onTransactionSelect = {
            viewModel.updateSelectedTransaction(it)
            it?.id?.let { transaction -> onTransactionClick(transaction) }
        },
        onDateTypeUpdate = viewModel::updateDateType,
        onFinanceTypeUpdate = viewModel::updateFinanceType,
        onSelectedDateUpdate = viewModel::updateSelectedDate,
        onCategoryFilterUpdate = viewModel::updateSelectedCategories,
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalHazeMaterialsApi::class,
)
@Composable
private fun WalletScreen(
    walletState: WalletUiState,
    shouldShowNavigationIcon: Boolean,
    shouldHighlightSelectedTransaction: Boolean,
    onPrimaryClick: (walletId: String, isPrimary: Boolean) -> Unit,
    onTransfer: (String) -> Unit,
    onWalletEdit: (String) -> Unit,
    onWalletDelete: (String) -> Unit,
    onBackClick: () -> Unit,
    onDateTypeUpdate: (DateType) -> Unit,
    onFinanceTypeUpdate: (FinanceType) -> Unit,
    onSelectedDateUpdate: (Int) -> Unit,
    onCategoryFilterUpdate: (Category, Boolean) -> Unit,
    navigateToTransactionDialog: (walletId: String, transactionId: String?, repeated: Boolean) -> Unit,
    onTransactionSelect: (Transaction?) -> Unit = {},
) {
    when (walletState) {
        WalletUiState.Loading -> LoadingState(Modifier.fillMaxSize())
        is WalletUiState.Success -> {
            Scaffold(
                topBar = {
                    WalletTopBar(
                        wallet = walletState.wallet,
                        formattedCurrentBalance = walletState.formattedCurrentBalance,
                        isPrimary = walletState.isPrimary,
                        showNavigationIcon = shouldShowNavigationIcon,
                        onBackClick = onBackClick,
                        onPrimaryClick = onPrimaryClick,
                    )
                },
            ) { paddingValues ->
                var expanded by rememberSaveable { mutableStateOf(true) }
                val hazeState = rememberHazeState()
                val hazeStyle =
                    HazeMaterials.ultraThin(MaterialTheme.colorScheme.secondaryContainer)

                Box(modifier = Modifier.padding(paddingValues)) {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 96.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .floatingToolbarVerticalNestedScroll(
                                expanded = expanded,
                                onExpand = { expanded = true },
                                onCollapse = { expanded = false },
                            ),
                    ) {
                        item {
                            FinancePanel(
                                availableCategories = walletState.availableCategories,
                                currency = walletState.wallet.currency,
                                formattedExpenses = walletState.formattedExpenses,
                                formattedIncome = walletState.formattedIncome,
                                graphData = walletState.graphData,
                                transactionFilter = walletState.transactionFilter,
                                onDateTypeUpdate = onDateTypeUpdate,
                                onFinanceTypeUpdate = onFinanceTypeUpdate,
                                onSelectedDateUpdate = onSelectedDateUpdate,
                                onCategoryFilterUpdate = onCategoryFilterUpdate,
                                modifier = Modifier.padding(top = 6.dp),
                            )
                        }
                        transactions(
                            groupedTransactions = walletState.groupedTransactions,
                            hazeState = hazeState,
                            hazeStyle = hazeStyle,
                            onClick = onTransactionSelect,
                            selectedTransaction = walletState.selectedTransaction,
                            shouldHighlightSelectedTransaction = shouldHighlightSelectedTransaction,
                        )
                    }
                    WalletToolbar(
                        wallet = walletState.wallet,
                        formattedCurrentBalance = walletState.formattedCurrentBalance,
                        expanded = expanded,
                        onTransfer = onTransfer,
                        onWalletEdit = onWalletEdit,
                        onWalletDelete = onWalletDelete,
                        navigateToTransactionDialog = navigateToTransactionDialog,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = -ScreenOffset),
                    )
                }
            }
        }
    }
    TrackScreenViewEvent(screenName = "Wallet")
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun WalletToolbar(
    wallet: Wallet,
    formattedCurrentBalance: String,
    expanded: Boolean,
    onTransfer: (String) -> Unit,
    onWalletEdit: (String) -> Unit,
    onWalletDelete: (String) -> Unit,
    navigateToTransactionDialog: (String, String?, Boolean) -> Unit,
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
                    onClick = { onWalletEdit(wallet.id) },
                    icon = {
                        Icon(
                            imageVector = CsIcons.Outlined.Edit,
                            contentDescription = stringResource(localesR.string.edit),
                        )
                    },
                    label = editButtonLabel,
                )
                clickableItem(
                    onClick = { shouldShowDeletionDialog = true },
                    icon = {
                        Icon(
                            imageVector = CsIcons.Outlined.Delete,
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
                onClick = { navigateToTransactionDialog(wallet.id, null, false) },
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
                        leadingContent = { Icon(imageVector = CsIcons.Outlined.Wallet, contentDescription = null) },
                    )
                }
            },
        )
    }
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
private fun WalletTopBar(
    wallet: Wallet,
    formattedCurrentBalance: String,
    isPrimary: Boolean,
    showNavigationIcon: Boolean,
    onBackClick: () -> Unit,
    onPrimaryClick: (walletId: String, isPrimary: Boolean) -> Unit,
) {
    with(LocalSharedTransitionScope.current) {
        TopAppBar(
            title = {
                Text(
                    text = wallet.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.sharedBounds(
                        sharedContentState = rememberSharedContentState(
                            key = SharedElementKey.WalletTitle(
                                walletId = wallet.id,
                                title = wallet.title,
                            ),
                        ),
                        animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                        resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(),
                        boundsTransform = MaterialTheme.motionScheme.sharedElementTransitionSpec,
                    ),
                )
            },
            subtitle = {
                AnimatedAmount(
                    formattedAmount = formattedCurrentBalance,
                    label = "WalletBalance",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.sharedBounds(
                        sharedContentState = rememberSharedContentState(
                            key = SharedElementKey.WalletBalance(
                                walletId = wallet.id,
                                balance = formattedCurrentBalance,
                            ),
                        ),
                        animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                        resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(),
                        boundsTransform = MaterialTheme.motionScheme.sharedElementTransitionSpec,
                    ),
                )
            },
            navigationIcon = {
                if (showNavigationIcon) {
                    CsIconButton(
                        onClick = onBackClick,
                        icon = CsIcons.Outlined.ArrowBack,
                        contentDescription = stringResource(localesR.string.navigation_back_icon_description),
                        tooltipPosition = TooltipAnchorPosition.Right,
                    )
                }
            },
            actions = {
                PrimaryToggleButton(
                    isPrimary = isPrimary,
                    onPrimaryClick = { onPrimaryClick(wallet.id, !isPrimary) },
                )
            },
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
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

@PreviewLightDark
@Composable
private fun WalletScreenPopulatedPreview(
    @PreviewParameter(TransactionPreviewParameterProvider::class)
    transactions: List<Transaction>,
) {
    CsTheme {
        WalletScreen(
            walletState = WalletUiState.Success(
                transactionFilter = TransactionFilter(
                    selectedCategories = emptySet(),
                    financeType = FinanceType.NOT_SET,
                    dateType = DateType.ALL,
                    selectedDate = getCurrentZonedDateTime().date,
                ),
                wallet = Wallet(
                    id = "1",
                    title = "Credit",
                    initialBalance = BigDecimal(1000),
                    currency = getUsdCurrency(),
                ),
                selectedTransaction = null,
                groupedTransactions = transactions.groupByDate(),
                availableCategories = emptyList(),
                formattedExpenses = "$495.9",
                formattedIncome = "1,000",
                graphData = emptyMap(),
                isPrimary = true,
                formattedCurrentBalance = "$57,500",
            ),
            shouldShowNavigationIcon = true,
            shouldHighlightSelectedTransaction = false,
            onPrimaryClick = { _, _ -> },
            onTransfer = {},
            onWalletEdit = {},
            onWalletDelete = {},
            onBackClick = {},
            onDateTypeUpdate = {},
            onFinanceTypeUpdate = {},
            onSelectedDateUpdate = {},
            onCategoryFilterUpdate = { _, _ -> },
            navigateToTransactionDialog = { _, _, _ -> },
        )
    }
}

@PreviewLightDark
@Composable
private fun WalletScreenEmptyPreview() {
    CsTheme {
        WalletScreen(
            walletState = WalletUiState.Success(
                transactionFilter = TransactionFilter(
                    selectedCategories = emptySet(),
                    financeType = FinanceType.NOT_SET,
                    dateType = DateType.ALL,
                    selectedDate = getCurrentZonedDateTime().date,
                ),
                wallet = Wallet(
                    id = "1",
                    title = "Credit",
                    initialBalance = BigDecimal(1000),
                    currency = getUsdCurrency(),
                ),
                selectedTransaction = null,
                groupedTransactions = emptyMap(),
                availableCategories = emptyList(),
                formattedExpenses = "$495.9",
                formattedIncome = "1,000",
                graphData = emptyMap(),
                isPrimary = true,
                formattedCurrentBalance = "$57,500",
            ),
            shouldShowNavigationIcon = true,
            shouldHighlightSelectedTransaction = false,
            onPrimaryClick = { _, _ -> },
            onTransfer = {},
            onWalletEdit = {},
            onWalletDelete = {},
            onBackClick = {},
            onDateTypeUpdate = {},
            onFinanceTypeUpdate = {},
            onSelectedDateUpdate = {},
            onCategoryFilterUpdate = { _, _ -> },
            navigateToTransactionDialog = { _, _, _ -> },
        )
    }
}