package ru.resodostudios.cashsense.feature.category.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.data.repository.CategoriesRepository
import ru.resodostudios.cashsense.core.data.repository.TransactionsRepository
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.TransactionCategoryCrossRef
import ru.resodostudios.cashsense.core.ui.CategoriesUiState
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
internal class CategoriesViewModel @Inject constructor(
    private val categoriesRepository: CategoriesRepository,
    private val transactionsRepository: TransactionsRepository,
) : ViewModel() {

    var shouldDisplayUndoCategory by mutableStateOf(false)
    private var lastRemovedCategory: Pair<Category, List<String>>? = null

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

    fun deleteCategory(id: String) {
        viewModelScope.launch {
            val category = categoriesRepository.getCategory(id).first()
            val transactionIds = transactionsRepository.getTransactionCategoryCrossRefs(id)
                .first()
                .map { it.transactionId }
            lastRemovedCategory = category to transactionIds
            categoriesRepository.deleteCategory(id)
            shouldDisplayUndoCategory = true
            updateSelectedCategory(null)
        }
    }

    fun undoCategoryRemoval() {
        viewModelScope.launch {
            lastRemovedCategory?.let {
                categoriesRepository.upsertCategory(it.first)
                it.second.forEach { transactionId ->
                    it.first.id?.let { categoryId ->
                        val crossRef = TransactionCategoryCrossRef(
                            transactionId = transactionId,
                            categoryId = categoryId,
                        )
                        transactionsRepository.upsertTransactionCategoryCrossRef(crossRef)
                    }
                }
            }
            clearUndoState()
        }
    }

    fun clearUndoState() {
        lastRemovedCategory = null
        shouldDisplayUndoCategory = false
    }
}