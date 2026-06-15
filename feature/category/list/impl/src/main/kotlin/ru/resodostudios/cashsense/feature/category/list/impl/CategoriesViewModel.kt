package ru.resodostudios.cashsense.feature.category.list.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import ru.resodostudios.cashsense.core.data.repository.CategoriesRepository
import ru.resodostudios.cashsense.core.model.data.Category
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
internal class CategoriesViewModel @Inject constructor(
    categoriesRepository: CategoriesRepository,
) : ViewModel() {

    private val selectedCategoryState = MutableStateFlow<Category?>(null)

    val categoriesUiState: StateFlow<CategoriesUiState> = combine(
        categoriesRepository.getCategories(),
        selectedCategoryState,
    ) { categories, selectedCategoryId ->
        if (categories.isEmpty()) {
             CategoriesUiState.Empty
        } else {
            CategoriesUiState.Success(
                categories = categories,
                selectedCategory = categories.find { it == selectedCategoryId },
            )
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = CategoriesUiState.Loading,
        )

    fun updateSelectedCategory(category: Category?) {
        selectedCategoryState.value = category
    }
}

internal sealed interface CategoriesUiState {

    data object Loading : CategoriesUiState

    data object Empty : CategoriesUiState

    data class Success(
        val categories: List<Category>,
        val selectedCategory: Category?,
    ) : CategoriesUiState
}