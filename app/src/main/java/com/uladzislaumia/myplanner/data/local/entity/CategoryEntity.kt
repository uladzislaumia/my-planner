package com.uladzislaumia.myplanner.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.uladzislaumia.myplanner.domain.model.Category

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val icon: String,
    val colorHex: String,
) {
    fun toDomain(): Category {
        return Category(
            id = id,
            name = name,
            icon = icon,
            colorHex = colorHex,
        )
    }

    companion object {
        fun fromDomain(category: Category): CategoryEntity {
            return CategoryEntity(
                id = category.id,
                name = category.name,
                icon = category.icon,
                colorHex = category.colorHex,
            )
        }
    }
}
