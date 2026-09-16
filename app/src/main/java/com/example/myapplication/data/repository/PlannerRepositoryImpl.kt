package com.example.myapplication.data.repository

import com.example.myapplication.domain.model.PlannerItem
import com.example.myapplication.domain.model.Priority
import com.example.myapplication.domain.repository.PlannerRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Date
import java.util.UUID

class PlannerRepositoryImpl : PlannerRepository {
    
    // В будущем здесь будет Room или Retrofit
    override fun getItems(): Flow<List<PlannerItem>> = flow {
        delay(1000) // Имитация загрузки
        val mockItems = List(15) { i ->
            PlannerItem(
                id = UUID.randomUUID().toString(),
                title = "Задача №$i",
                description = "Описание для планировщика сферы ${if (i % 2 == 0) "Спорт" else "Дом"}",
                categoryId = "cat_$i",
                groupId = "group_1",
                assigneeId = "user_1",
                dueDate = Date(),
                priority = when (i % 3) {
                    0 -> Priority.LOW
                    1 -> Priority.MEDIUM
                    else -> Priority.HIGH
                }
            )
        }
        emit(mockItems)
    }

    override suspend fun getItemById(id: String): PlannerItem? = null
    override suspend fun addItem(item: PlannerItem) {}
    override suspend fun updateItem(item: PlannerItem) {}
    override suspend fun deleteItem(id: String) {}
}
