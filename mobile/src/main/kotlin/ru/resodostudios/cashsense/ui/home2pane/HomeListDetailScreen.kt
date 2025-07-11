package ru.resodostudios.cashsense.ui.home2pane

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.VerticalDragHandle
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.PaneExpansionAnchor
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldPredictiveBackHandler
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import ru.resodostudios.cashsense.R
import ru.resodostudios.cashsense.core.ui.component.EmptyState
import ru.resodostudios.cashsense.core.util.Constants.DEEP_LINK_SCHEME_AND_HOST
import ru.resodostudios.cashsense.core.util.Constants.HOME_PATH
import ru.resodostudios.cashsense.core.util.Constants.WALLET_ID_KEY
import ru.resodostudios.cashsense.feature.home.HomeScreen
import ru.resodostudios.cashsense.feature.home.navigation.HomeRoute
import ru.resodostudios.cashsense.feature.transaction.overview.TransactionOverviewScreen
import ru.resodostudios.cashsense.feature.transaction.overview.navigation.TransactionOverviewRoute
import ru.resodostudios.cashsense.feature.wallet.detail.WalletScreen
import ru.resodostudios.cashsense.feature.wallet.detail.WalletViewModel
import ru.resodostudios.cashsense.feature.wallet.detail.navigation.WalletRoute
import kotlin.math.max
import ru.resodostudios.cashsense.core.locales.R as localesR

private const val DEEP_LINK_BASE_PATH = "$DEEP_LINK_SCHEME_AND_HOST/$HOME_PATH/{$WALLET_ID_KEY}"

@Serializable
internal object WalletPlaceholderRoute

@Serializable
internal object HomeListDetailRoute

fun NavGraphBuilder.homeListDetailScreen(
    onEditWallet: (String) -> Unit,
    onTransfer: (String) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    navigateToTransactionDialog: (walletId: String, transactionId: String?, repeated: Boolean) -> Unit,
    nestedDestinations: NavGraphBuilder.() -> Unit,
) {
    navigation<HomeListDetailRoute>(startDestination = HomeRoute()) {
        composable<HomeRoute>(
            deepLinks = listOf(
                navDeepLink<HomeRoute>(basePath = DEEP_LINK_BASE_PATH),
            ),
        ) {
            HomeListDetailScreen(
                onEditWallet = onEditWallet,
                onTransfer = onTransfer,
                onShowSnackbar = onShowSnackbar,
                navigateToTransactionDialog = navigateToTransactionDialog,
            )
        }
        nestedDestinations()
    }
}

@Composable
internal fun HomeListDetailScreen(
    onEditWallet: (String) -> Unit,
    onTransfer: (String) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    navigateToTransactionDialog: (walletId: String, transactionId: String?, repeated: Boolean) -> Unit,
    viewModel: Home2PaneViewModel = hiltViewModel(),
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
) {
    val selectedWalletId by viewModel.selectedWalletId.collectAsStateWithLifecycle()
    val shouldDisplayUndoWallet by viewModel.shouldDisplayUndoWalletState.collectAsStateWithLifecycle()

    HomeListDetailScreen(
        selectedWalletId = selectedWalletId,
        navigateToTransactionDialog = navigateToTransactionDialog,
        onWalletSelect = viewModel::onWalletSelect,
        onTransfer = onTransfer,
        onEditWallet = onEditWallet,
        onDeleteWallet = viewModel::deleteWallet,
        onShowSnackbar = onShowSnackbar,
        windowAdaptiveInfo = windowAdaptiveInfo,
        shouldDisplayUndoWallet = shouldDisplayUndoWallet,
        undoWalletRemoval = viewModel::undoWalletRemoval,
        clearUndoState = viewModel::clearUndoState,
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun HomeListDetailScreen(
    selectedWalletId: String?,
    navigateToTransactionDialog: (walletId: String, transactionId: String?, repeated: Boolean) -> Unit,
    onWalletSelect: (String?) -> Unit,
    onTransfer: (String) -> Unit,
    onEditWallet: (String) -> Unit,
    onDeleteWallet: (String) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    windowAdaptiveInfo: WindowAdaptiveInfo,
    shouldDisplayUndoWallet: Boolean = false,
    undoWalletRemoval: () -> Unit = {},
    clearUndoState: () -> Unit = {},
) {
    val scaffoldNavigator = rememberListDetailPaneScaffoldNavigator(
        scaffoldDirective = calculatePaneScaffoldDirective(windowAdaptiveInfo),
        initialDestinationHistory = listOfNotNull(
            ThreePaneScaffoldDestinationItem(ListDetailPaneScaffoldRole.List),
            ThreePaneScaffoldDestinationItem<Any>(ListDetailPaneScaffoldRole.Detail).takeIf {
                selectedWalletId != null
            },
        ),
    )
    val paneExpansionState = rememberPaneExpansionState(
        anchors = listOf(
            PaneExpansionAnchor.Proportion(0f),
            PaneExpansionAnchor.Proportion(0.35f),
            PaneExpansionAnchor.Proportion(0.5f),
            PaneExpansionAnchor.Proportion(0.65f),
            PaneExpansionAnchor.Proportion(1f),
        ),
    )
    var walletRoute by remember {
        val route = selectedWalletId?.let { WalletRoute(it) } ?: WalletPlaceholderRoute
        mutableStateOf(route)
    }
    val coroutineScope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }

    ThreePaneScaffoldPredictiveBackHandler(
        scaffoldNavigator,
        BackNavigationBehavior.PopUntilScaffoldValueChange,
    )
    BackHandler(
        paneExpansionState.currentAnchor == PaneExpansionAnchor.Proportion(0f) &&
                scaffoldNavigator.isListPaneVisible() &&
                scaffoldNavigator.isDetailPaneVisible(),
    ) {
        coroutineScope.launch {
            paneExpansionState.animateTo(PaneExpansionAnchor.Proportion(1f))
        }
    }

    fun onWalletClickShowDetailPane(walletId: String?) {
        onWalletSelect(walletId)
        if (walletId != null) {
            walletRoute = WalletRoute(walletId)
            coroutineScope.launch {
                scaffoldNavigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
            }
            if (!scaffoldNavigator.isDetailPaneVisible()) clearUndoState()
            if (paneExpansionState.currentAnchor == PaneExpansionAnchor.Proportion(1f)) {
                coroutineScope.launch {
                    paneExpansionState.animateTo(PaneExpansionAnchor.Proportion(0f))
                }
            }
        } else {
            walletRoute = WalletPlaceholderRoute
        }
    }

    fun onTotalBalanceClickShowDetailPane() {
        walletRoute = TransactionOverviewRoute
        coroutineScope.launch {
            scaffoldNavigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
        }
        if (!scaffoldNavigator.isDetailPaneVisible()) clearUndoState()
    }

    val minPaneWidth = 300.dp

    NavigableListDetailPaneScaffold(
        navigator = scaffoldNavigator,
        listPane = {
            AnimatedPane {
                Box(
                    modifier = Modifier
                        .clipToBounds()
                        .layout { measurable, constraints ->
                            val width = max(minPaneWidth.roundToPx(), constraints.maxWidth)
                            val placeable = measurable.measure(
                                constraints.copy(
                                    minWidth = minPaneWidth.roundToPx(),
                                    maxWidth = width,
                                ),
                            )
                            layout(constraints.maxWidth, placeable.height) {
                                placeable.placeRelative(
                                    x = 0,
                                    y = 0,
                                )
                            }
                        },
                ) {
                    HomeScreen(
                        onWalletClick = ::onWalletClickShowDetailPane,
                        onTransfer = onTransfer,
                        onEditWallet = onEditWallet,
                        onDeleteWallet = {
                            onWalletSelect(null)
                            walletRoute = WalletPlaceholderRoute
                            onDeleteWallet(it)
                        },
                        onTransactionCreate = {
                            navigateToTransactionDialog(it, null, false)
                        },
                        highlightSelectedWallet = scaffoldNavigator.isDetailPaneVisible(),
                        onShowSnackbar = onShowSnackbar,
                        shouldDisplayUndoWallet = shouldDisplayUndoWallet,
                        undoWalletRemoval = undoWalletRemoval,
                        clearUndoState = clearUndoState,
                        onTotalBalanceClick = ::onTotalBalanceClickShowDetailPane,
                    )
                }
            }
        },
        detailPane = {
            AnimatedPane {
                Box(
                    modifier = Modifier
                        .clipToBounds()
                        .layout { measurable, constraints ->
                            val width = max(minPaneWidth.roundToPx(), constraints.maxWidth)
                            val placeable = measurable.measure(
                                constraints.copy(
                                    minWidth = minPaneWidth.roundToPx(),
                                    maxWidth = width,
                                ),
                            )
                            layout(constraints.maxWidth, placeable.height) {
                                placeable.placeRelative(
                                    x = constraints.maxWidth -
                                            max(constraints.maxWidth, placeable.width),
                                    y = 0,
                                )
                            }
                        },
                ) {
                    AnimatedContent(walletRoute) { route ->
                        when (route) {
                            is TransactionOverviewRoute -> {
                                TransactionOverviewScreen(
                                    shouldShowTopBar = !scaffoldNavigator.isListPaneVisible(),
                                    onBackClick = {
                                        coroutineScope.launch {
                                            scaffoldNavigator.navigateBack()
                                        }
                                    },
                                    onTransactionClick = navigateToTransactionDialog,
                                )
                            }

                            is WalletRoute -> {
                                WalletScreen(
                                    onBackClick = {
                                        coroutineScope.launch {
                                            scaffoldNavigator.navigateBack()
                                        }
                                    },
                                    onTransfer = onTransfer,
                                    onEditWallet = onEditWallet,
                                    onDeleteClick = {
                                        coroutineScope.launch {
                                            scaffoldNavigator.navigateBack()
                                        }
                                        onWalletSelect(null)
                                        walletRoute = WalletPlaceholderRoute
                                        onDeleteWallet(it)
                                    },
                                    showNavigationIcon = !scaffoldNavigator.isListPaneVisible(),
                                    navigateToTransactionDialog = navigateToTransactionDialog,
                                    viewModel = hiltViewModel<WalletViewModel, WalletViewModel.Factory>(
                                        key = route.walletId,
                                    ) { factory ->
                                        factory.create(route.walletId)
                                    },
                                )
                            }

                            is WalletPlaceholderRoute -> {
                                EmptyState(
                                    messageRes = localesR.string.select_wallet,
                                    animationRes = R.raw.anim_select_wallet,
                                )
                            }
                        }
                    }
                }
            }
        },
        paneExpansionState = paneExpansionState,
        paneExpansionDragHandle = {
            VerticalDragHandle(
                modifier = Modifier.paneExpansionDraggable(
                    state = paneExpansionState,
                    minTouchTargetSize = LocalMinimumInteractiveComponentSize.current,
                    interactionSource = interactionSource,
                ),
                interactionSource = interactionSource,
            )
        },
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private fun <T> ThreePaneScaffoldNavigator<T>.isListPaneVisible(): Boolean =
    scaffoldValue[ListDetailPaneScaffoldRole.List] == PaneAdaptedValue.Expanded

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private fun <T> ThreePaneScaffoldNavigator<T>.isDetailPaneVisible(): Boolean =
    scaffoldValue[ListDetailPaneScaffoldRole.Detail] == PaneAdaptedValue.Expanded