package com.uladzislaumia.myplanner.domain.repository

import com.uladzislaumia.myplanner.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getCategories(): Flow<List<Category>>
    suspend fun addCategory(category: Category)
}
