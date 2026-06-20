package ru.resodostudios.cashsense.core.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow
import ru.resodostudios.cashsense.core.database.model.CategoryEntity

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories WHERE id = :id")
    fun getCategoryEntity(id: String): Flow<CategoryEntity>

    @Query(
        """
        SELECT c.* FROM categories c
        LEFT JOIN transactions t ON c.id = t.category_id
        GROUP BY c.id
        ORDER BY count(t.id) DESC
    """
    )
    fun getCategoryEntities(): Flow<List<CategoryEntity>>

    @Upsert
    suspend fun upsertCategory(category: CategoryEntity)

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteCategory(id: String)
}