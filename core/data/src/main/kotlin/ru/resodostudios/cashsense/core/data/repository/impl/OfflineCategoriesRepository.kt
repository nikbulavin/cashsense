package ru.resodostudios.cashsense.core.data.repository.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.resodostudios.cashsense.core.data.model.asEntity
import ru.resodostudios.cashsense.core.data.repository.CategoriesRepository
import ru.resodostudios.cashsense.core.database.dao.CategoryDao
import ru.resodostudios.cashsense.core.database.model.CategoryEntity
import ru.resodostudios.cashsense.core.database.model.asExternalModel
import ru.resodostudios.cashsense.core.model.data.Category
import javax.inject.Inject

internal class OfflineCategoriesRepository @Inject constructor(
    private val dao: CategoryDao
) : CategoriesRepository {

    override fun getCategory(id: String): Flow<Category> =
        dao.getCategoryEntity(id).map { it.asExternalModel() }

    override fun getCategories(): Flow<List<Category>> =
        dao.getCategoryEntities().map { it.map(CategoryEntity::asExternalModel) }

    override suspend fun upsertCategory(category: Category) =
        dao.upsertCategory(category.asEntity()!!)

    override suspend fun deleteCategory(id: String) =
        dao.deleteCategory(id)
}