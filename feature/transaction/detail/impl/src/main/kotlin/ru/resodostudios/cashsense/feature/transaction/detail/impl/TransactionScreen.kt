package ru.resodostudios.cashsense.feature.transaction.detail.impl

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateBounds
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import ru.resodostudios.cashsense.core.designsystem.component.CsAlertDialog
import ru.resodostudios.cashsense.core.designsystem.component.CsListItem
import ru.resodostudios.cashsense.core.designsystem.component.CsTag
import ru.resodostudios.cashsense.core.designsystem.component.button.CsIconButton
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.ArrowBack
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Block
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Delete
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Edit
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Pending
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Redo
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.SendMoney
import ru.resodostudios.cashsense.core.designsystem.theme.LocalSharedTransitionScope
import ru.resodostudios.cashsense.core.designsystem.theme.SharedElementKey
import ru.resodostudios.cashsense.core.designsystem.theme.sharedElementTransitionSpec
import ru.resodostudios.cashsense.core.model.data.DateFormatType
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.ui.component.LoadingState
import ru.resodostudios.cashsense.core.ui.component.SectionTitle
import ru.resodostudios.cashsense.core.ui.component.StoredIcon
import ru.resodostudios.cashsense.core.ui.util.formatAmount
import ru.resodostudios.cashsense.core.ui.util.formatDate
import java.time.format.FormatStyle
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
internal fun TransactionScreen(
    onBackClick: () -> Unit,
    onRepeatClick: (walletId: String, transactionId: String) -> Unit,
    onEditClick: (walletId: String, transactionId: String) -> Unit,
    viewModel: TransactionViewModel = hiltViewModel(),
) {
    val transactionUiState by viewModel.transactionUiState.collectAsStateWithLifecycle()

    TransactionScreen(
        onBackClick = onBackClick,
        onRepeatClick = onRepeatClick,
        onEditClick = onEditClick,
        onDeleteClick = {
            viewModel.deleteTransaction(it)
            onBackClick()
        },
        transactionState = transactionUiState,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TransactionScreen(
    onBackClick: () -> Unit,
    onRepeatClick: (walletId: String, transactionId: String) -> Unit,
    onEditClick: (walletId: String, transactionId: String) -> Unit,
    onDeleteClick: (Transaction) -> Unit,
    transactionState: TransactionUiState,
) {
    when (transactionState) {
        TransactionUiState.Loading -> LoadingState(Modifier.fillMaxSize())
        is TransactionUiState.Success -> {
            val transaction = transactionState.transaction
            val category = transactionState.transaction.category
            val (categoryIcon, categoryTitle) = if (transaction.transferId != null) {
                CsIcons.Outlined.SendMoney to stringResource(localesR.string.transfers)
            } else {
                val iconId = category?.iconId ?: StoredIcon.TRANSACTION.storedId
                val title = category?.title ?: stringResource(localesR.string.uncategorized)
                StoredIcon.asImageVector(iconId) to title
            }
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = transaction.timestamp.formatDate(
                                    DateFormatType.DATE_TIME,
                                    FormatStyle.MEDIUM,
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                        subtitle = {},
                        navigationIcon = {
                            CsIconButton(
                                onClick = onBackClick,
                                icon = CsIcons.Outlined.ArrowBack,
                                contentDescription = stringResource(localesR.string.navigation_back_icon_description),
                                tooltipPosition = TooltipAnchorPosition.Right,
                            )
                        },
                        titleHorizontalAlignment = Alignment.CenterHorizontally,
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    with(LocalSharedTransitionScope.current) {
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = MaterialShapes.Cookie7Sided.toShape(),
                            modifier = Modifier.size(128.dp),
                        ) {
                            Icon(
                                imageVector = categoryIcon,
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(32.dp)
                                    .sharedBounds(
                                        sharedContentState = rememberSharedContentState(
                                            SharedElementKey.CategoryIcon(transaction.id)
                                        ),
                                        animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                                        boundsTransform = MaterialTheme.motionScheme.sharedElementTransitionSpec,
                                    ),
                            )
                        }
                        Text(
                            text = categoryTitle,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.sharedBounds(
                                sharedContentState = rememberSharedContentState(
                                    SharedElementKey.CategoryTitle(
                                        transactionId = transaction.id,
                                        title = categoryTitle,
                                    )
                                ),
                                animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                                resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(),
                                boundsTransform = MaterialTheme.motionScheme.sharedElementTransitionSpec,
                            ),
                        )
                        TagsSection(
                            isTransactionIgnored = transaction.ignored,
                            isTransactionPending = !transaction.completed,
                        )
                        val formattedAmount = transaction.amount.formatAmount(
                            currency = transactionState.transaction.currency,
                            plusPrefix = true,
                        )
                        Text(
                            text = formattedAmount,
                            style = MaterialTheme.typography.headlineLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.sharedBounds(
                                sharedContentState = rememberSharedContentState(
                                    SharedElementKey.TransactionAmount(
                                        transactionId = transaction.id,
                                        amount = formattedAmount,
                                    )
                                ),
                                animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                                resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(),
                                boundsTransform = MaterialTheme.motionScheme.sharedElementTransitionSpec,
                            ),
                        )
                    }
                    ActionButtons(
                        transaction = transaction,
                        onRepeatClick = onRepeatClick,
                        onEditClick = onEditClick,
                        onDeleteClick = onDeleteClick,
                        categoryTitle = categoryTitle,
                        categoryIcon = categoryIcon,
                    )
                    transaction.description?.let { description ->
                        TransactionDescription(
                            description = description,
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private fun ActionButtons(
    transaction: Transaction,
    onRepeatClick: (String, String) -> Unit,
    onEditClick: (String, String) -> Unit,
    onDeleteClick: (Transaction) -> Unit,
    categoryTitle: String,
    categoryIcon: ImageVector,
) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (transaction.transferId == null) {
            FilledIconButton(
                shapes = IconButtonDefaults.shapes(shape = IconButtonDefaults.mediumSquareShape),
                onClick = { onRepeatClick(transaction.walletOwnerId, transaction.id) },
                modifier = Modifier.size(IconButtonDefaults.mediumContainerSize()),
            ) {
                Icon(
                    imageVector = CsIcons.Outlined.Redo,
                    contentDescription = stringResource(localesR.string.repeat),
                    modifier = Modifier.size(IconButtonDefaults.mediumIconSize),
                )
            }
            FilledIconButton(
                shapes = IconButtonDefaults.shapes(shape = IconButtonDefaults.mediumSquareShape),
                onClick = { onEditClick(transaction.walletOwnerId, transaction.id) },
                modifier = Modifier.size(IconButtonDefaults.mediumContainerSize()),
            ) {
                Icon(
                    imageVector = CsIcons.Outlined.Edit,
                    contentDescription = stringResource(localesR.string.edit),
                    modifier = Modifier.size(IconButtonDefaults.mediumIconSize),
                )
            }
        }
        var shouldShowDeletionDialog by rememberSaveable { mutableStateOf(false) }
        FilledIconButton(
            shapes = IconButtonDefaults.shapes(shape = IconButtonDefaults.mediumSquareShape),
            onClick = { shouldShowDeletionDialog = true },
            modifier = Modifier.size(IconButtonDefaults.mediumContainerSize()),
        ) {
            Icon(
                imageVector = CsIcons.Outlined.Delete,
                contentDescription = stringResource(localesR.string.delete),
                modifier = Modifier.size(IconButtonDefaults.mediumIconSize),
            )
        }
        if (shouldShowDeletionDialog) {
            CsAlertDialog(
                titleRes = localesR.string.delete_transaction,
                icon = CsIcons.Outlined.Delete,
                confirmButtonTextRes = localesR.string.delete,
                dismissButtonTextRes = localesR.string.cancel,
                onConfirm = {
                    onDeleteClick(transaction)
                    shouldShowDeletionDialog = false
                },
                onDismiss = { shouldShowDeletionDialog = false },
                content = {
                    Column {
                        Text(stringResource(localesR.string.permanently_delete_transaction))
                        CsListItem(
                            headlineContent = {
                                Text(
                                    text = transaction.amount.formatAmount(
                                        transaction.currency,
                                        true,
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            },
                            supportingContent = {
                                Text(
                                    text = categoryTitle,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            },
                            leadingContent = {
                                Icon(
                                    imageVector = categoryIcon,
                                    contentDescription = null,
                                )
                            },
                        )
                    }
                },
            )
        }
    }
}

@OptIn(
    ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
private fun TagsSection(
    isTransactionIgnored: Boolean,
    isTransactionPending: Boolean,
    modifier: Modifier = Modifier,
) {
    LookaheadScope {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = modifier.animateContentSize(),
        ) {
            val spatialSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
            val effectsSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Float>()
            AnimatedVisibility(
                visible = isTransactionIgnored,
                enter = fadeIn(effectsSpec) + scaleIn(spatialSpec),
                exit = fadeOut(effectsSpec) + scaleOut(spatialSpec),
                modifier = Modifier.animateBounds(this@LookaheadScope),
            ) {
                CsTag(
                    text = stringResource(localesR.string.transaction_ignored),
                    icon = CsIcons.Outlined.Block,
                    color = MaterialTheme.colorScheme.errorContainer,
                    textColor = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
            AnimatedVisibility(
                visible = isTransactionPending,
                enter = fadeIn(effectsSpec) + scaleIn(spatialSpec),
                exit = fadeOut(effectsSpec) + scaleOut(spatialSpec),
                modifier = Modifier.animateBounds(this@LookaheadScope),
            ) {
                CsTag(
                    text = stringResource(localesR.string.pending),
                    icon = CsIcons.Outlined.Pending,
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    textColor = MaterialTheme.colorScheme.onTertiaryContainer,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TransactionDescription(
    description: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        SectionTitle(
            titleRes = localesR.string.description,
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
        ) {
            Text(
                text = description,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}
