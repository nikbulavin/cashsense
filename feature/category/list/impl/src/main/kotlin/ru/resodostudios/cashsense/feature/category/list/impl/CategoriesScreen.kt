package ru.resodostudios.cashsense.feature.category.list.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.ToggleFloatingActionButtonDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.designsystem.component.CsTopAppBar
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.model.Category
import ru.resodostudios.cashsense.core.ui.CategoryPreviewParameterProvider
import ru.resodostudios.cashsense.core.ui.component.FabMenu
import ru.resodostudios.cashsense.core.ui.component.FabMenuItem
import ru.resodostudios.cashsense.core.ui.component.IllustratedMessage
import ru.resodostudios.cashsense.core.ui.component.LoadingState
import ru.resodostudios.cashsense.core.ui.util.TrackScreenViewEvent
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
internal fun CategoriesScreen(
    navigateToCategory: (String) -> Unit,
    shouldHighlightSelectedCategory: Boolean,
    shouldShowFab: Boolean = false,
    isNavRailVisible: Boolean = false,
    onFabMenuItemClick: (FabMenuItem) -> Unit = {},
    viewModel: CategoriesViewModel = hiltViewModel(),
) {
    val categoriesState by viewModel.categoriesUiState.collectAsStateWithLifecycle()

    CategoriesScreen(
        categoriesState = categoriesState,
        shouldHighlightSelectedCategory = shouldHighlightSelectedCategory,
        shouldShowFab = shouldShowFab,
        isNavRailVisible = isNavRailVisible,
        onFabMenuItemClick = onFabMenuItemClick,
        onCategorySelect = { category ->
            viewModel.updateSelectedCategory(category)
            navigateToCategory(category.id)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun CategoriesScreen(
    categoriesState: CategoriesUiState,
    shouldHighlightSelectedCategory: Boolean,
    shouldShowFab: Boolean,
    isNavRailVisible: Boolean,
    onFabMenuItemClick: (FabMenuItem) -> Unit,
    onCategorySelect: (Category) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Box {
        Scaffold(
            topBar = {
                CsTopAppBar(
                    titleRes = localesR.string.categories_title,
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.topAppBarColors().copy(
                        scrolledContainerColor = Color.Transparent,
                        containerColor = Color.Transparent,
                    ),
                )
            },
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        ) { innerPadding ->

            when (categoriesState) {
                CategoriesUiState.Loading -> {
                    LoadingState(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                    )
                }

                CategoriesUiState.Empty -> {
                    IllustratedMessage(
                        messageRes = localesR.string.categories_empty,
                        animationRes = R.raw.anim_categories_empty,
                        modifier = Modifier.padding(innerPadding),
                    )
                }

                is CategoriesUiState.Success -> {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            top = innerPadding.calculateTopPadding(),
                            bottom = innerPadding.calculateBottomPadding() + 110.dp,
                            start = 16.dp,
                            end = 16.dp,
                        ),
                        verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
                    ) {
                        categories(
                            categories = categoriesState.categories,
                            onCategoryClick = onCategorySelect,
                            selectedCategory = categoriesState.selectedCategory,
                            shouldHighlightSelectedCategory = shouldHighlightSelectedCategory,
                        )
                    }
                }
            }
        }
        FabMenu(
            visible = shouldShowFab,
            onMenuItemClick = onFabMenuItemClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .windowInsetsPadding(WindowInsets.systemBars),
            toggleContainerSize = if (isNavRailVisible) {
                ToggleFloatingActionButtonDefaults.containerSizeMedium()
            } else {
                ToggleFloatingActionButtonDefaults.containerSize()
            },
        )
    }
    TrackScreenViewEvent(screenName = "Categories")
}

@Preview
@Composable
private fun CategoriesScreenPreview(
    @PreviewParameter(CategoryPreviewParameterProvider::class)
    categories: List<Category>,
) {
    CsTheme {
        Surface {
            CategoriesScreen(
                categoriesState = CategoriesUiState.Success(
                    categories = categories,
                    selectedCategory = null,
                ),
                shouldHighlightSelectedCategory = false,
                shouldShowFab = false,
                isNavRailVisible = false,
                onFabMenuItemClick = {},
                onCategorySelect = {},
            )
        }
    }
}