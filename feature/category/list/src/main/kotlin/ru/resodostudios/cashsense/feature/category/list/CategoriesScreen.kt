package ru.resodostudios.cashsense.feature.category.list

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.ui.CategoriesUiState
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
    modifier: Modifier = Modifier,
    viewModel: CategoriesViewModel = hiltViewModel(),
) {
    val categoriesState by viewModel.categoriesUiState.collectAsStateWithLifecycle()

    CategoriesScreen(
        categoriesState = categoriesState,
        onEditCategory = onEditCategory,
        onShowSnackbar = onShowSnackbar,
        modifier = modifier,
        onUpdateCategoryId = viewModel::updateCategoryId,
        deleteCategory = viewModel::deleteCategory,
        undoCategoryRemoval = viewModel::undoCategoryRemoval,
        clearUndoState = viewModel::clearUndoState,
    )
}

@Composable
internal fun CategoriesScreen(
    categoriesState: CategoriesUiState,
    onEditCategory: (String) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    onUpdateCategoryId: (String) -> Unit,
    modifier: Modifier = Modifier,
    deleteCategory: (String) -> Unit = {},
    undoCategoryRemoval: () -> Unit = {},
    clearUndoState: () -> Unit = {},
) {
    when (categoriesState) {
        Loading -> LoadingState(modifier.fillMaxSize())
        is Success -> {
            val categoryDeletedMessage = stringResource(localesR.string.category_deleted)
            val undoText = stringResource(localesR.string.undo)

            LaunchedEffect(categoriesState.shouldDisplayUndoCategory) {
                if (categoriesState.shouldDisplayUndoCategory) {
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

            if (categoriesState.categories.isNotEmpty()) {
                var showCategoryBottomSheet by rememberSaveable { mutableStateOf(false) }

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(300.dp),
                    contentPadding = PaddingValues(
                        bottom = 110.dp,
                    ),
                    modifier = modifier.fillMaxSize(),
                ) {
                    categories(
                        categoriesState = categoriesState,
                        onCategoryClick = {
                            onUpdateCategoryId(it)
                            showCategoryBottomSheet = true
                        },
                    )
                }
                if (showCategoryBottomSheet && categoriesState.selectedCategory != null) {
                    CategoryBottomSheet(
                        category = categoriesState.selectedCategory!!,
                        onDismiss = { showCategoryBottomSheet = false },
                        onEdit = onEditCategory,
                        onDelete = deleteCategory,
                    )
                }
            } else {
                EmptyState(
                    messageRes = localesR.string.categories_empty,
                    animationRes = R.raw.anim_categories_empty,
                    modifier = modifier,
                )
            }
        }
    }
    TrackScreenViewEvent(screenName = "Categories")
}

private fun LazyGridScope.categories(
    categoriesState: CategoriesUiState,
    onCategoryClick: (String) -> Unit,
) {
    when (categoriesState) {
        Loading -> Unit
        is Success -> {
            items(
                items = categoriesState.categories,
                key = { it.id!! },
            ) { category ->
                CategoryItem(
                    category = category,
                    onClick = onCategoryClick,
                    modifier = Modifier.animateItem(),
                )
            }
        }
    }
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
                onEditCategory = {},
                categoriesState = Success(
                    shouldDisplayUndoCategory = false,
                    categories = categories,
                    selectedCategory = null,
                ),
                onUpdateCategoryId = {},
            )
        }
    }
}