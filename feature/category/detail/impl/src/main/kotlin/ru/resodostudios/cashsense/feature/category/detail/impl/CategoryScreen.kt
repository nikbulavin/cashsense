package ru.resodostudios.cashsense.feature.category.detail.impl

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.designsystem.component.button.CsIconButton
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.ArrowBack
import ru.resodostudios.cashsense.core.designsystem.theme.LocalSharedTransitionScope
import ru.resodostudios.cashsense.core.designsystem.theme.SharedElementKey
import ru.resodostudios.cashsense.core.designsystem.theme.SharedElementType
import ru.resodostudios.cashsense.core.designsystem.theme.sharedBoundsAdaptive
import ru.resodostudios.cashsense.core.ui.component.LoadingState
import ru.resodostudios.cashsense.core.ui.component.StoredIcon
import ru.resodostudios.cashsense.core.ui.util.TrackScreenViewEvent
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
        onCategoryDelete = viewModel::deleteCategory,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
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
                                    id = categoryUiState.category.id,
                                    origin = categoryUiState.category.id,
                                    type = SharedElementType.Bounds,
                                ),
                            ),
                            placeholderSize = SharedTransitionScope.PlaceholderSize.AnimatedSize,
                        ),
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(innerPadding)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = MaterialShapes.Pill.toShape(),
                            modifier = Modifier.size(128.dp),
                        ) {
                            val icon = StoredIcon.asImageVector(categoryUiState.category.iconId)
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(32.dp)
                                    .sharedBoundsAdaptive(
                                        sharedContentState = rememberSharedContentState(
                                            key = SharedElementKey(
                                                id = categoryUiState.category.id,
                                                origin = icon.toString(),
                                                type = SharedElementType.CategoryIcon,
                                            ),
                                        ),
                                        resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(),
                                    ),
                            )
                        }
                        val title = categoryUiState.category.title
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.sharedBoundsAdaptive(
                                sharedContentState = rememberSharedContentState(
                                    key = SharedElementKey(
                                        id = categoryUiState.category.id,
                                        origin = title,
                                        type = SharedElementType.TransactionAmount,
                                    ),
                                ),
                                resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(),
                            ),
                        )
                    }
                }
            }
        }


        TrackScreenViewEvent(screenName = "Category")
    }
}