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
        value = """
            SELECT c.id, c.title, c.icon_id FROM categories c
            LEFT JOIN (
                SELECT tc.category_id, count(tc.category_id) AS usage_count
                FROM transactions_categories tc
                GROUP BY tc.category_id
            ) AS usage ON c.id = usage.category_id
            ORDER BY usage.usage_count DESC;
        """,
    )
    fun getCategoryEntities(): Flow<List<CategoryEntity>>

    @Upsert
    suspend fun upsertCategory(category: CategoryEntity)

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteCategory(id: String)
}