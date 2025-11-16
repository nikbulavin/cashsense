package ru.resodostudios.cashsense.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.resodostudios.cashsense.core.model.data.Category

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