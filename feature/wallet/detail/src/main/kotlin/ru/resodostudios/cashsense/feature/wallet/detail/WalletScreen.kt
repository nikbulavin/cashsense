package ru.resodostudios.cashsense.feature.wallet.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.designsystem.component.CsAlertDialog
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
    modifier: Modifier = Modifier,
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
        modifier = modifier,
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
    modifier: Modifier = Modifier,
    updateTransactionId: (String) -> Unit = {},
    onUpdateTransactionIgnoring: (Boolean) -> Unit = {},
    onDeleteTransaction: () -> Unit = {},
) {
    when (walletState) {
        WalletUiState.Loading -> LoadingState(modifier.fillMaxSize())
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

            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

            Scaffold(
                modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    WalletTopBar(
                        userWallet = walletState.userWallet,
                        showNavigationIcon = showNavigationIcon,
                        scrollBehavior = scrollBehavior,
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
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun BoxScope.WalletToolbar(
    expanded: Boolean,
    onTransfer: (String) -> Unit,
    walletId: String,
    onEditWallet: (String) -> Unit,
    onDeleteWallet: (String) -> Unit,
    navigateToTransactionDialog: (String, String?, Boolean) -> Unit
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
                    CsIcons.Outlined.SendMoney,
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
                }
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
    scrollBehavior: TopAppBarScrollBehavior,
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
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = CsIcons.Outlined.ArrowBack,
                        contentDescription = null,
                    )
                }
            }
        },
        actions = { PrimaryToggleButton(userWallet, onPrimaryClick) },
        windowInsets = WindowInsets(0, 0, 0, 0),
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors().copy(
            scrolledContainerColor = MaterialTheme.colorScheme.surface,
        ),
    )
}

@Composable
private fun PrimaryToggleButton(
    userWallet: UserWallet,
    onPrimaryClick: (walletId: String, isPrimary: Boolean) -> Unit,
) {
    val (icon, contentDescription) = if (userWallet.isPrimary) {
        CsIcons.Filled.Star to stringResource(localesR.string.primary_icon_description)
    } else {
        CsIcons.Outlined.Star to stringResource(localesR.string.non_primary_icon_description)
    }
    CsIconToggleButton(
        checked = userWallet.isPrimary,
        onCheckedChange = { isChecked ->
            onPrimaryClick(userWallet.id, isChecked)
        },
        icon = icon,
        contentDescription = contentDescription,
        modifier = Modifier
            .width(52.dp)
            .padding(end = 4.dp),
    )
}