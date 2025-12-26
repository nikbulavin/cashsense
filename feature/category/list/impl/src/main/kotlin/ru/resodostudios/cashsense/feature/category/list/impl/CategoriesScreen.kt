package ru.resodostudios.cashsense.feature.category.list.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells.Adaptive
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.designsystem.component.CsTopAppBar
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.ui.CategoriesUiState
import ru.resodostudios.cashsense.core.ui.CategoriesUiState.Empty
import ru.resodostudios.cashsense.core.ui.CategoriesUiState.Loading
import ru.resodostudios.cashsense.core.ui.CategoriesUiState.Success
import ru.resodostudios.cashsense.core.ui.CategoryPreviewParameterProvider
import ru.resodostudios.cashsense.core.ui.component.EmptyState
import ru.resodostudios.cashsense.core.ui.component.LoadingState
import ru.resodostudios.cashsense.core.ui.util.TrackScreenViewEvent
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
internal fun CategoriesScreen(
    onEditCategory: (String) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    viewModel: CategoriesViewModel = hiltViewModel(),
) {
    val categoriesState by viewModel.categoriesUiState.collectAsStateWithLifecycle()

    CategoriesScreen(
        categoriesState = categoriesState,
        onCategoryEdit = onEditCategory,
        onShowSnackbar = onShowSnackbar,
        onCategorySelect = viewModel::updateSelectedCategory,
        onCategoryDelete = viewModel::deleteCategory,
        shouldDisplayUndoCategory = viewModel.shouldDisplayUndoCategory,
        undoCategoryRemoval = viewModel::undoCategoryRemoval,
        clearUndoState = viewModel::clearUndoState,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun CategoriesScreen(
    categoriesState: CategoriesUiState,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    onCategorySelect: (Category?) -> Unit,
    onCategoryEdit: (String) -> Unit,
    onCategoryDelete: (String) -> Unit,
    shouldDisplayUndoCategory: Boolean = false,
    undoCategoryRemoval: () -> Unit = {},
    clearUndoState: () -> Unit = {},
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

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
        val categoryDeletedMessage = stringResource(localesR.string.category_deleted)
        val undoText = stringResource(localesR.string.undo)

        LaunchedEffect(shouldDisplayUndoCategory) {
            if (shouldDisplayUndoCategory) {
                val snackBarResult = onShowSnackbar(categoryDeletedMessage, undoText)
                if (snackBarResult) {
                    undoCategoryRemoval()
                } else {
                    clearUndoState()
                }
            }
        }
        LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
            clearUndoState()
        }

        when (categoriesState) {
            Loading -> {
                LoadingState(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                )
            }

            Empty -> {
                EmptyState(
                    messageRes = localesR.string.categories_empty,
                    animationRes = R.raw.anim_categories_empty,
                    modifier = Modifier.padding(innerPadding),
                )
            }

            is Success -> {
                LazyVerticalGrid(
                    columns = Adaptive(300.dp),
                    contentPadding = PaddingValues(
                        top = innerPadding.calculateTopPadding(),
                        bottom = innerPadding.calculateBottomPadding() + 110.dp,
                        start = 16.dp,
                        end = 16.dp,
                    ),
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    categories(
                        categories = categoriesState.categories,
                        onCategoryClick = onCategorySelect,
                        selectedCategory = categoriesState.selectedCategory,
                        onCategoryEdit = onCategoryEdit,
                        onCategoryDelete = onCategoryDelete,
                    )
                }
            }
        }
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
                onShowSnackbar = { _, _ -> false },
                categoriesState = Success(
                    categories = categories,
                    selectedCategory = null,
                ),
                onCategorySelect = {},
                onCategoryEdit = {},
                onCategoryDelete = {},
            )
        }
    }
}