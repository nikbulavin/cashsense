package ru.resodostudios.cashsense.feature.category.detail.impl

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconButtonDefaults.mediumContainerSize
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import ru.resodostudios.cashsense.core.designsystem.component.CsAlertDialog
import ru.resodostudios.cashsense.core.designsystem.component.button.CsFilledIconButton
import ru.resodostudios.cashsense.core.designsystem.component.button.CsFilledTonalIconButton
import ru.resodostudios.cashsense.core.designsystem.component.button.CsIconButton
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.ArrowBack
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Delete
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Edit
import ru.resodostudios.cashsense.core.designsystem.theme.LocalSharedTransitionScope
import ru.resodostudios.cashsense.core.designsystem.theme.SharedElementKey
import ru.resodostudios.cashsense.core.designsystem.theme.SharedElementType
import ru.resodostudios.cashsense.core.designsystem.theme.sharedBoundsAdaptive
import ru.resodostudios.cashsense.core.model.Category
import ru.resodostudios.cashsense.core.model.Transaction
import ru.resodostudios.cashsense.core.ui.component.LoadingState
import ru.resodostudios.cashsense.core.ui.model.StoredIcon
import ru.resodostudios.cashsense.core.ui.transactions
import ru.resodostudios.cashsense.core.ui.util.TrackScreenViewEvent
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
internal fun CategoryScreen(
    onBackClick: () -> Unit,
    onEditCategory: (String) -> Unit,
    onTransactionClick: (String) -> Unit,
    shouldShowNavigationIcon: Boolean,
    shouldHighlightSelectedTransaction: Boolean,
    viewModel: CategoryViewModel = hiltViewModel(),
) {
    val categoryUiState by viewModel.categoryUiState.collectAsStateWithLifecycle()

    CategoryScreen(
        categoryUiState = categoryUiState,
        shouldShowNavigationIcon = shouldShowNavigationIcon,
        shouldHighlightSelectedTransaction = shouldHighlightSelectedTransaction,
        onBackClick = onBackClick,
        onCategoryEdit = onEditCategory,
        onCategoryDelete = {
            viewModel.deleteCategory()
            onBackClick()
        },
        onTransactionSelect = { transaction ->
            viewModel.updateSelectedTransaction(transaction)
            transaction?.id?.let { onTransactionClick(it) }
        },
    )
}

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalHazeMaterialsApi::class,
)
@Composable
private fun CategoryScreen(
    categoryUiState: CategoryUiState,
    shouldShowNavigationIcon: Boolean,
    shouldHighlightSelectedTransaction: Boolean,
    onBackClick: () -> Unit,
    onCategoryEdit: (String) -> Unit,
    onCategoryDelete: () -> Unit,
    onTransactionSelect: (Transaction?) -> Unit,
) {
    with(LocalSharedTransitionScope.current) {
        when (categoryUiState) {
            CategoryUiState.Loading -> LoadingState(Modifier.fillMaxSize())
            is CategoryUiState.Success -> {
                val category = categoryUiState.category
                val hazeState = rememberHazeState()
                val hazeStyle = HazeMaterials.thick(MaterialTheme.colorScheme.tertiaryContainer)
                val motionScheme = MaterialTheme.motionScheme
                val dateTextColor = MaterialTheme.colorScheme.onTertiaryContainer

                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .sharedBoundsAdaptive(
                            sharedContentState = rememberSharedContentState(
                                key = SharedElementKey(
                                    id = category.id,
                                    origin = category.id,
                                    type = SharedElementType.Bounds,
                                ),
                            ),
                            placeholderSize = SharedTransitionScope.PlaceholderSize.AnimatedSize,
                            clipShape = MaterialTheme.shapes.large,
                        ),
                ) {
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
                    )
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 16.dp),
                    ) {
                        item {
                            Header(
                                category = category,
                                onCategoryEdit = onCategoryEdit,
                                onCategoryDelete = onCategoryDelete,
                            )
                        }
                        transactions(
                            groupedTransactions = categoryUiState.groupedTransactions,
                            motionScheme = motionScheme,
                            dateTextColor = dateTextColor,
                            shouldHighlightSelectedTransaction = shouldHighlightSelectedTransaction,
                            onClick = onTransactionSelect,
                            selectedTransaction = categoryUiState.selectedTransaction,
                            hazeState = hazeState,
                            hazeStyle = hazeStyle,
                            walletIdsAndTitles = categoryUiState.walletIdsAndTitles,
                            shouldShowCategoryIcon = false,
                        )
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
    onCategoryDelete: () -> Unit,
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
                            type = SharedElementType.CategoryTitle,
                        ),
                    ),
                    resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(),
                ),
            )
            ActionButtons(
                onEditClick = { onCategoryEdit(category.id) },
                onDeleteClick = onCategoryDelete,
            )
        }
    }
}

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