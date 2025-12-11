package ru.resodostudios.cashsense.feature.category.dialog.impl

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.analytics.AnalyticsEvent
import ru.resodostudios.cashsense.core.analytics.AnalyticsHelper
import ru.resodostudios.cashsense.core.data.repository.CategoriesRepository
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.network.di.ApplicationScope
import ru.resodostudios.cashsense.core.ui.util.logNewItemAdded
import ru.resodostudios.cashsense.feature.category.dialog.api.CategoryDialogNavKey
import kotlin.uuid.Uuid

@HiltViewModel(assistedFactory = CategoryDialogViewModel.Factory::class)
internal class CategoryDialogViewModel @AssistedInject constructor(
    private val categoriesRepository: CategoriesRepository,
    @ApplicationScope private val appScope: CoroutineScope,
    private val analyticsHelper: AnalyticsHelper,
    @Assisted val key: CategoryDialogNavKey,
) : ViewModel() {

    private val _categoryDialogUiState = MutableStateFlow(CategoryDialogUiState())
    val categoryDialogUiState = _categoryDialogUiState.asStateFlow()

    init {
        key.categoryId?.let(::loadCategory)
    }

    private fun loadCategory(id: String) {
        viewModelScope.launch {
            _categoryDialogUiState.update { CategoryDialogUiState(isLoading = true) }
            val category = categoriesRepository.getCategory(id).first()
            _categoryDialogUiState.update {
                CategoryDialogUiState(
                    id = id,
                    title = category.title,
                    iconId = category.iconId,
                )
            }
        }
    }

    fun saveCategory(state: CategoryDialogUiState) {
        appScope.launch {
            if (state.id.isBlank()) {
                analyticsHelper.logNewItemAdded(
                    itemType = AnalyticsEvent.ItemTypes.CATEGORY,
                )
            }
            categoriesRepository.upsertCategory(state.asCategory())
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

    @AssistedFactory
    interface Factory {
        fun create(key: CategoryDialogNavKey): CategoryDialogViewModel
    }
}

@Immutable
data class CategoryDialogUiState(
    val id: String = "",
    val title: String = "",
    val iconId: Int = 0,
    val isLoading: Boolean = false,
)

fun CategoryDialogUiState.asCategory(): Category {
    return Category(
        id = id.ifBlank { Uuid.random().toHexString() },
        title = title.trim(),
        iconId = iconId,
    )
}