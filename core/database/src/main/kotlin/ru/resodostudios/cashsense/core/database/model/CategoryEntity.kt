package ru.resodostudios.cashsense.core.database.model

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import ru.resodostudios.cashsense.core.model.Category

@Entity(
    tableName = "categories",
)
data class CategoryEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    @ColumnInfo(name = "icon_id", defaultValue = "0")
    val iconId: Int,
)

fun CategoryEntity.asExternalModel(): Category {
    return Category(
        id = id,
        title = title,
        iconId = iconId,
    )
}