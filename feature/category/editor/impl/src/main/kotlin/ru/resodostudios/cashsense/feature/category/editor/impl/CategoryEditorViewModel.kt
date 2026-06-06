package ru.resodostudios.cashsense.feature.category.editor.impl

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.analytics.AnalyticsEvent
import ru.resodostudios.cashsense.core.analytics.AnalyticsHelper
import ru.resodostudios.cashsense.core.common.di.ApplicationScope
import ru.resodostudios.cashsense.core.data.repository.CategoriesRepository
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.ui.util.logNewItemAdded
import ru.resodostudios.cashsense.feature.category.editor.api.CategoryEditorNavKey
import kotlin.uuid.Uuid

@HiltViewModel(assistedFactory = CategoryEditorViewModel.Factory::class)
internal class CategoryEditorViewModel @AssistedInject constructor(
    private val categoriesRepository: CategoriesRepository,
    @ApplicationScope private val appScope: CoroutineScope,
    private val analyticsHelper: AnalyticsHelper,
    @Assisted private val key: CategoryEditorNavKey,
) : ViewModel() {

    val categoryEditorState: StateFlow<CategoryEditorState>
        field = MutableStateFlow(CategoryEditorState())

    init {
        key.categoryId?.let(::loadCategory)
    }

    private fun loadCategory(id: String) {
        viewModelScope.launch {
            categoryEditorState.update { it.copy(isLoading = true) }
            val category = categoriesRepository.getCategory(id).first()
            categoryEditorState.update {
                it.copy(
                    id = id,
                    title = category.title,
                    iconId = category.iconId,
                    isLoading = false,
                )
            }
        }
    }

    fun saveCategory() {
        val state = categoryEditorState.value
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
        categoryEditorState.update {
            it.copy(title = title)
        }
    }

    fun updateIconId(iconId: Int) {
        categoryEditorState.update {
            it.copy(iconId = iconId)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(key: CategoryEditorNavKey): CategoryEditorViewModel
    }
}

@Immutable
internal data class CategoryEditorState(
    val id: String = "",
    val title: String = "",
    val iconId: Int = 0,
    val isLoading: Boolean = false,
)

internal fun CategoryEditorState.asCategory(): Category {
    return Category(
        id = id.ifBlank { Uuid.random().toHexString() },
        title = title.trim(),
        iconId = iconId,
    )
}
