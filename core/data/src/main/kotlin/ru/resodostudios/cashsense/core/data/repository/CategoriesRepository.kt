package ru.resodostudios.cashsense.core.data.repository

import kotlinx.coroutines.flow.Flow
import ru.resodostudios.cashsense.core.model.Category

interface CategoriesRepository {

    fun getCategory(id: String): Flow<Category>

    fun getCategories(): Flow<List<Category>>

    suspend fun upsertCategory(category: Category)

    suspend fun deleteCategory(id: String)
}