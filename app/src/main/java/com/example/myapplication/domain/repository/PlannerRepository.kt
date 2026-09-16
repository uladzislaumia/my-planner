package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.PlannerItem
import kotlinx.coroutines.flow.Flow

interface PlannerRepository {
    fun getItems(): Flow<List<PlannerItem>>
    suspend fun getItemById(id: String): PlannerItem?
    suspend fun addItem(item: PlannerItem)
    suspend fun updateItem(item: PlannerItem)
    suspend fun deleteItem(id: String)
}
