package ru.resodostudios.cashsense.feature.category.dialog

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.data.repository.CategoriesRepository
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.network.di.ApplicationScope
import ru.resodostudios.cashsense.feature.category.dialog.navigation.CategoryDialogRoute
import javax.inject.Inject
import kotlin.uuid.Uuid

@HiltViewModel
class CategoryDialogViewModel @Inject constructor(
    private val categoriesRepository: CategoriesRepository,
    savedStateHandle: SavedStateHandle,
    @ApplicationScope private val appScope: CoroutineScope,
) : ViewModel() {

    private val categoryDialogDestination: CategoryDialogRoute = savedStateHandle.toRoute()

    private val _categoryDialogUiState = MutableStateFlow(CategoryDialogUiState())
    val categoryDialogUiState = _categoryDialogUiState.asStateFlow()

    init {
        categoryDialogDestination.categoryId?.let(::loadCategory)
    }

    private fun loadCategory(id: String) {
        viewModelScope.launch {
            _categoryDialogUiState.update { CategoryDialogUiState(isLoading = true) }
            val category = categoriesRepository.getCategory(id).first()
            _categoryDialogUiState.update {
                CategoryDialogUiState(
                    id = id,
                    title = category.title.toString(),
                    iconId = category.iconId ?: 0,
                )
            }
        }
    }

    fun saveCategory(category: Category) {
        appScope.launch {
            categoriesRepository.upsertCategory(category)
        }
    }

    fun updateTitle(title: String) {
        _categoryDialogUiState.update {
            it.copy(title = title)
        }
    }

    fun updateIconId(iconId: Int) {
        _categoryDialogUiState.update {
            it.copy(iconId = iconId)
        }
    }
}

@Immutable
data class CategoryDialogUiState(
    val id: String = "",
    val title: String = "",
    val iconId: Int = 0,
    val isLoading: Boolean = false,
)

fun CategoryDialogUiState.asCategory() =
    Category(
        id = id.ifBlank { Uuid.random().toHexString() },
        title = title,
        iconId = iconId,
    )