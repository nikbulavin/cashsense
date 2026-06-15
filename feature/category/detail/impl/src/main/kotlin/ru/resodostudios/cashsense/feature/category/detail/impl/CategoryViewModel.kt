package ru.resodostudios.cashsense.feature.category.detail.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.data.repository.CategoriesRepository
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.feature.category.detail.api.CategoryNavKey
import kotlin.time.Duration.Companion.seconds

@HiltViewModel(assistedFactory = CategoryViewModel.Factory::class)
internal class CategoryViewModel @AssistedInject constructor(
    @Assisted private val key: CategoryNavKey,
    private val categoriesRepository: CategoriesRepository,
) : ViewModel() {

    val categoryUiState: StateFlow<CategoryUiState> =
        categoriesRepository.getCategory(key.categoryId)
            .map(CategoryUiState::Success)
            .catch {}
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5.seconds),
                initialValue = CategoryUiState.Loading,
            )

    fun deleteCategory(id: String) {
        viewModelScope.launch {
            categoriesRepository.deleteCategory(id)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(key: CategoryNavKey): CategoryViewModel
    }
}

internal sealed interface CategoryUiState {

    data object Loading : CategoryUiState

    data class Success(
        val category: Category,
    ) : CategoryUiState
}