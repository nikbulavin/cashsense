package ru.resodostudios.cashsense.feature.wallet.detail

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconButtonDefaults.smallContainerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTooltipState
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.designsystem.component.CsAlertDialog
import ru.resodostudios.cashsense.core.designsystem.component.CsIconButton
import ru.resodostudios.cashsense.core.designsystem.component.CsIconToggleButton
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.filled.Star
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Add
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.ArrowBack
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Delete
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Edit
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.MoreVert
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.SendMoney
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Star
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.DateType
import ru.resodostudios.cashsense.core.model.data.FinanceType
import ru.resodostudios.cashsense.core.model.data.UserWallet
import ru.resodostudios.cashsense.core.ui.component.AnimatedAmount
import ru.resodostudios.cashsense.core.ui.component.FinancePanel
import ru.resodostudios.cashsense.core.ui.component.LoadingState
import ru.resodostudios.cashsense.core.ui.component.TransactionBottomSheet
import ru.resodostudios.cashsense.core.ui.transactions
import ru.resodostudios.cashsense.core.ui.util.TrackScreenViewEvent
import ru.resodostudios.cashsense.core.ui.util.formatAmount
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
fun WalletScreen(
    onBackClick: () -> Unit,
    onTransfer: (String) -> Unit,
    onEditWallet: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    showNavigationIcon: Boolean,
    navigateToTransactionDialog: (walletId: String, transactionId: String?, repeated: Boolean) -> Unit,
    viewModel: WalletViewModel = hiltViewModel(),
) {
    val walletState by viewModel.walletUiState.collectAsStateWithLifecycle()

    WalletScreen(
        walletState = walletState,
        showNavigationIcon = showNavigationIcon,
        onPrimaryClick = viewModel::setPrimaryWalletId,
        onTransfer = onTransfer,
        onEditWallet = onEditWallet,
        onDeleteWallet = onDeleteClick,
        onBackClick = onBackClick,
        navigateToTransactionDialog = navigateToTransactionDialog,
        updateTransactionId = viewModel::updateTransactionId,
        onUpdateTransactionIgnoring = viewModel::updateTransactionIgnoring,
        onDeleteTransaction = viewModel::deleteTransaction,
        onDateTypeUpdate = viewModel::updateDateType,
        onFinanceTypeUpdate = viewModel::updateFinanceType,
        onSelectedDateUpdate = viewModel::updateSelectedDate,
        onCategorySelect = viewModel::addToSelectedCategories,
        onCategoryDeselect = viewModel::removeFromSelectedCategories
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
private fun WalletScreen(
    walletState: WalletUiState,
    showNavigationIcon: Boolean,
    onPrimaryClick: (walletId: String, isPrimary: Boolean) -> Unit,
    onTransfer: (String) -> Unit,
    onEditWallet: (String) -> Unit,
    onDeleteWallet: (String) -> Unit,
    onBackClick: () -> Unit,
    onDateTypeUpdate: (DateType) -> Unit,
    onFinanceTypeUpdate: (FinanceType) -> Unit,
    onSelectedDateUpdate: (Int) -> Unit,
    onCategorySelect: (Category) -> Unit,
    onCategoryDeselect: (Category) -> Unit,
    navigateToTransactionDialog: (walletId: String, transactionId: String?, repeated: Boolean) -> Unit,
    updateTransactionId: (String) -> Unit = {},
    onUpdateTransactionIgnoring: (Boolean) -> Unit = {},
    onDeleteTransaction: () -> Unit = {},
) {
    when (walletState) {
        WalletUiState.Loading -> LoadingState(Modifier.fillMaxSize())
        is WalletUiState.Success -> {
            var showTransactionBottomSheet by rememberSaveable { mutableStateOf(false) }
            var showTransactionDeletionDialog by rememberSaveable { mutableStateOf(false) }

            if (showTransactionBottomSheet && walletState.selectedTransactionCategory != null) {
                TransactionBottomSheet(
                    transactionCategory = walletState.selectedTransactionCategory,
                    currency = walletState.userWallet.currency,
                    onDismiss = { showTransactionBottomSheet = false },
                    onIgnoreClick = onUpdateTransactionIgnoring,
                    onRepeatClick = { transactionId ->
                        navigateToTransactionDialog(walletState.userWallet.id, transactionId, true)
                    },
                    onEdit = { transactionId ->
                        navigateToTransactionDialog(walletState.userWallet.id, transactionId, false)
                    },
                    onDelete = { showTransactionDeletionDialog = true },
                )
            }
            if (showTransactionDeletionDialog) {
                CsAlertDialog(
                    titleRes = localesR.string.permanently_delete,
                    confirmButtonTextRes = localesR.string.delete,
                    dismissButtonTextRes = localesR.string.cancel,
                    icon = CsIcons.Outlined.Delete,
                    onConfirm = {
                        onDeleteTransaction()
                        showTransactionDeletionDialog = false
                    },
                    onDismiss = { showTransactionDeletionDialog = false },
                )
            }

            Scaffold(
                topBar = {
                    WalletTopBar(
                        userWallet = walletState.userWallet,
                        showNavigationIcon = showNavigationIcon,
                        onBackClick = onBackClick,
                        onPrimaryClick = onPrimaryClick,
                    )
                },
            ) { paddingValues ->
                var expanded by rememberSaveable { mutableStateOf(true) }

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
                                currency = walletState.userWallet.currency,
                                expenses = walletState.expenses,
                                income = walletState.income,
                                graphData = walletState.graphData,
                                transactionFilter = walletState.transactionFilter,
                                onDateTypeUpdate = onDateTypeUpdate,
                                onFinanceTypeUpdate = onFinanceTypeUpdate,
                                onSelectedDateUpdate = onSelectedDateUpdate,
                                onCategorySelect = onCategorySelect,
                                onCategoryDeselect = onCategoryDeselect,
                                modifier = Modifier.padding(top = 6.dp),
                            )
                        }
                        transactions(
                            transactionsCategories = walletState.transactionsCategories,
                            onTransactionClick = {
                                updateTransactionId(it)
                                showTransactionBottomSheet = true
                            },
                        )
                    }
                    WalletToolbar(
                        expanded = expanded,
                        onTransfer = onTransfer,
                        walletId = walletState.userWallet.id,
                        onEditWallet = onEditWallet,
                        onDeleteWallet = onDeleteWallet,
                        navigateToTransactionDialog = navigateToTransactionDialog,
                    )
                }
            }
        }
    }
    TrackScreenViewEvent(screenName = "Wallet")
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun BoxScope.WalletToolbar(
    expanded: Boolean,
    onTransfer: (String) -> Unit,
    walletId: String,
    onEditWallet: (String) -> Unit,
    onDeleteWallet: (String) -> Unit,
    navigateToTransactionDialog: (String, String?, Boolean) -> Unit,
) {
    val vibrantColors = FloatingToolbarDefaults.vibrantFloatingToolbarColors()
    HorizontalFloatingToolbar(
        colors = vibrantColors,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .offset(y = -ScreenOffset),
        expanded = expanded,
        leadingContent = {
            IconButton(
                onClick = { onTransfer(walletId) },
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
                            if (menuState.isExpanded) {
                                menuState.dismiss()
                            } else {
                                menuState.show()
                            }
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
                    onClick = { onEditWallet(walletId) },
                    icon = {
                        Icon(
                            imageVector = CsIcons.Outlined.Edit,
                            contentDescription = stringResource(localesR.string.edit),
                        )
                    },
                    label = editButtonLabel,
                )
                clickableItem(
                    onClick = { onDeleteWallet(walletId) },
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
                onClick = { navigateToTransactionDialog(walletId, null, false) },
            ) {
                Icon(
                    imageVector = CsIcons.Outlined.Add,
                    contentDescription = stringResource(localesR.string.add_transaction),
                )
            }
        }
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
private fun WalletTopBar(
    userWallet: UserWallet,
    showNavigationIcon: Boolean,
    onBackClick: () -> Unit,
    onPrimaryClick: (walletId: String, isPrimary: Boolean) -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = userWallet.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        subtitle = {
            AnimatedAmount(
                targetState = userWallet.currentBalance,
                label = "WalletBalance",
            ) {
                Text(
                    text = it.formatAmount(userWallet.currency),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        navigationIcon = {
            if (showNavigationIcon) {
                CsIconButton(
                    onClick = onBackClick,
                    icon = CsIcons.Outlined.ArrowBack,
                    contentDescription = stringResource(localesR.string.navigation_back_icon_description),
                )
            }
        },
        actions = { PrimaryToggleButton(userWallet, onPrimaryClick) },
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun PrimaryToggleButton(
    userWallet: UserWallet,
    onPrimaryClick: (walletId: String, isPrimary: Boolean) -> Unit,
) {
    val (icon, @StringRes contentDescriptionRes) = if (userWallet.isPrimary) {
        CsIcons.Filled.Star to localesR.string.non_primary_icon_description
    } else {
        CsIcons.Outlined.Star to localesR.string.primary_icon_description
    }

    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
            positioning = TooltipAnchorPosition.Below,
        ),
        tooltip = { PlainTooltip { Text(stringResource(contentDescriptionRes)) } },
        state = rememberTooltipState(),
    ) {
        CsIconToggleButton(
            checked = userWallet.isPrimary,
            onCheckedChange = { isChecked ->
                onPrimaryClick(userWallet.id, isChecked)
            },
            icon = icon,
            contentDescription = stringResource(contentDescriptionRes),
            modifier = Modifier
                .size(smallContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide))
                .padding(end = 4.dp),
        )
    }
}