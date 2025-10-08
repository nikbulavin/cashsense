package ru.resodostudios.cashsense.core.ui

import ru.resodostudios.cashsense.core.model.data.Category

sealed interface CategoriesUiState {

    data object Loading : CategoriesUiState

    data object Empty : CategoriesUiState

    data class Success(
        val categories: List<Category>,
        val selectedCategory: Category?,
    ) : CategoriesUiState
}