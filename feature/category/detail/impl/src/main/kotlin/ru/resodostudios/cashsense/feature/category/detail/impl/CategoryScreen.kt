package ru.resodostudios.cashsense.feature.category.detail.impl

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconButtonDefaults.mediumContainerSize
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import ru.resodostudios.cashsense.core.designsystem.component.CsAlertDialog
import ru.resodostudios.cashsense.core.designsystem.component.CsSelectableListItem
import ru.resodostudios.cashsense.core.designsystem.component.CsTag
import ru.resodostudios.cashsense.core.designsystem.component.button.CsFilledIconButton
import ru.resodostudios.cashsense.core.designsystem.component.button.CsFilledTonalIconButton
import ru.resodostudios.cashsense.core.designsystem.component.button.CsIconButton
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.ArrowBack
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Block
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Delete
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Edit
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Pending
import ru.resodostudios.cashsense.core.designsystem.theme.LocalSharedTransitionScope
import ru.resodostudios.cashsense.core.designsystem.theme.SharedElementKey
import ru.resodostudios.cashsense.core.designsystem.theme.SharedElementType
import ru.resodostudios.cashsense.core.designsystem.theme.sharedBoundsAdaptive
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.DateFormatType
import ru.resodostudios.cashsense.core.ui.component.LoadingState
import ru.resodostudios.cashsense.core.ui.component.StoredIcon
import ru.resodostudios.cashsense.core.ui.util.TrackScreenViewEvent
import ru.resodostudios.cashsense.core.ui.util.formatAmount
import ru.resodostudios.cashsense.core.ui.util.formatDate
import java.time.format.FormatStyle
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
internal fun CategoryScreen(
    onBackClick: () -> Unit,
    onEditCategory: (String) -> Unit,
    shouldShowNavigationIcon: Boolean,
    viewModel: CategoryViewModel = hiltViewModel(),
) {
    val categoryUiState by viewModel.categoryUiState.collectAsStateWithLifecycle()

    CategoryScreen(
        categoryUiState = categoryUiState,
        shouldShowNavigationIcon = shouldShowNavigationIcon,
        onBackClick = onBackClick,
        onCategoryEdit = onEditCategory,
        onCategoryDelete = {
            viewModel.deleteCategory(it)
            onBackClick()
        },
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalHazeMaterialsApi::class,
    ExperimentalHazeApi::class,
)
@Composable
private fun CategoryScreen(
    categoryUiState: CategoryUiState,
    shouldShowNavigationIcon: Boolean,
    onBackClick: () -> Unit,
    onCategoryEdit: (String) -> Unit,
    onCategoryDelete: (String) -> Unit,
) {
    with(LocalSharedTransitionScope.current) {
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

        when (categoryUiState) {
            CategoryUiState.Loading -> LoadingState(Modifier.fillMaxSize())
            is CategoryUiState.Success -> {
                val category = categoryUiState.category
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {},
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
                            colors = TopAppBarDefaults.topAppBarColors().copy(
                                scrolledContainerColor = Color.Transparent,
                                containerColor = Color.Transparent,
                            ),
                            scrollBehavior = scrollBehavior,
                        )
                    },
                    modifier = Modifier
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                        .sharedBoundsAdaptive(
                            sharedContentState = rememberSharedContentState(
                                key = SharedElementKey(
                                    id = category.id,
                                    origin = category.id,
                                    type = SharedElementType.Bounds,
                                ),
                            ),
                            placeholderSize = SharedTransitionScope.PlaceholderSize.AnimatedSize,
                        ),
                ) { innerPadding ->
                    val hazeState = rememberHazeState()
                    val hazeStyle =
                        HazeMaterials.ultraThin(MaterialTheme.colorScheme.tertiaryContainer)
                    LazyColumn(
                        contentPadding = innerPadding,
                    ) {
                        item {
                            Header(
                                category = category,
                                onCategoryEdit = onCategoryEdit,
                                onCategoryDelete = onCategoryDelete,
                            )
                        }
                        categoryUiState.groupedTransactions.forEach { transactionGroup ->
                            stickyHeader(
                                contentType = "Date",
                            ) {
                                CsTag(
                                    text = transactionGroup.key.formatDate(
                                        DateFormatType.DATE,
                                        FormatStyle.MEDIUM,
                                    ),
                                    color = Color.Transparent,
                                    textColor = MaterialTheme.colorScheme.onTertiaryContainer,
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
                                val effectsSpec =
                                    MaterialTheme.motionScheme.defaultEffectsSpec<Float>()
                                val floatSpatialSpec =
                                    MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
                                val intSizeSpatialSpec =
                                    MaterialTheme.motionScheme.defaultSpatialSpec<IntSize>()

                                CsSelectableListItem(
                                    shapes = if (transactionGroup.value.size == 1) {
                                        ListItemDefaults.shapes(shape = RoundedCornerShape(16.dp))
                                    } else {
                                        ListItemDefaults.segmentedShapes(
                                            index,
                                            transactionGroup.value.size,
                                        )
                                    },
                                    onClick = {},
                                    selected = false,
                                    modifier = Modifier
                                        .hazeSource(hazeState)
                                        .padding(horizontal = 16.dp)
                                        .animateItem(
                                            fadeInSpec = motionScheme.defaultEffectsSpec(),
                                            fadeOutSpec = motionScheme.defaultEffectsSpec(),
                                            placementSpec = motionScheme.defaultSpatialSpec(),
                                        ),
                                    content = {
                                        Text(
                                            text = transaction.amount.formatAmount(
                                                currency = transaction.currency,
                                                plusPrefix = true,
                                            ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    },
                                    supportingContent = {
                                        Text(
                                            text = categoryUiState.walletTitles[transaction.walletOwnerId].toString(),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    },
                                    trailingContent = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.animateContentSize(
                                                intSizeSpatialSpec
                                            ),
                                        ) {
                                            AnimatedVisibility(
                                                visible = transaction.ignored,
                                                enter = fadeIn(effectsSpec) + scaleIn(
                                                    floatSpatialSpec
                                                ),
                                                exit = fadeOut(effectsSpec) + scaleOut(
                                                    floatSpatialSpec
                                                ),
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
                                                enter = fadeIn(effectsSpec) + scaleIn(
                                                    floatSpatialSpec
                                                ),
                                                exit = fadeOut(effectsSpec) + scaleOut(
                                                    floatSpatialSpec
                                                ),
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
        TrackScreenViewEvent(screenName = "Category")
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun Header(
    category: Category,
    onCategoryEdit: (String) -> Unit,
    onCategoryDelete: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    with(LocalSharedTransitionScope.current) {
        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = MaterialShapes.Pill.toShape(),
                modifier = Modifier.size(128.dp),
            ) {
                val icon = StoredIcon.asImageVector(category.iconId)
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(32.dp)
                        .sharedBoundsAdaptive(
                            sharedContentState = rememberSharedContentState(
                                key = SharedElementKey(
                                    id = category.id,
                                    origin = icon.toString(),
                                    type = SharedElementType.CategoryIcon,
                                ),
                            ),
                            resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(),
                        ),
                )
            }
            val title = category.title
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.sharedBoundsAdaptive(
                    sharedContentState = rememberSharedContentState(
                        key = SharedElementKey(
                            id = category.id,
                            origin = title,
                            type = SharedElementType.TransactionAmount,
                        ),
                    ),
                    resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(),
                ),
            )
            ActionButtons(
                onEditClick = { onCategoryEdit(category.id) },
                onDeleteClick = { onCategoryDelete(category.id) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ActionButtons(
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    var shouldShowDeletionDialog by rememberSaveable { mutableStateOf(false) }
    ButtonGroup(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        overflowIndicator = {},
    ) {
        customItem(
            buttonGroupContent = {
                val interactionSource = remember { MutableInteractionSource() }
                Box(
                    modifier = Modifier.animateWidth(interactionSource),
                ) {
                    CsFilledIconButton(
                        onClick = onEditClick,
                        icon = CsIcons.Outlined.Edit,
                        contentDescription = stringResource(localesR.string.edit),
                        interactionSource = interactionSource,
                        containerSize = mediumContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide),
                        iconSize = IconButtonDefaults.mediumIconSize,
                    )
                }
            },
            menuContent = {},
        )
        customItem(
            buttonGroupContent = {
                val interactionSource = remember { MutableInteractionSource() }
                Box(
                    modifier = Modifier.animateWidth(interactionSource),
                ) {
                    CsFilledTonalIconButton(
                        onClick = { shouldShowDeletionDialog = true },
                        icon = CsIcons.Outlined.Delete,
                        contentDescription = stringResource(localesR.string.delete),
                        containerSize = mediumContainerSize(),
                        iconSize = IconButtonDefaults.mediumIconSize,
                        interactionSource = interactionSource,
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                        )
                    )
                }
            },
            menuContent = {},
        )
    }
    if (shouldShowDeletionDialog) {
        CsAlertDialog(
            titleRes = localesR.string.delete_category,
            icon = CsIcons.Outlined.Delete,
            confirmButtonTextRes = localesR.string.delete,
            dismissButtonTextRes = localesR.string.cancel,
            onConfirm = {
                onDeleteClick()
                shouldShowDeletionDialog = false
            },
            onDismiss = { shouldShowDeletionDialog = false },
            content = {
                Column {
                    Text(stringResource(localesR.string.permanently_delete_category))
                }
            },
        )
    }
}