package com.example.myapplication.data.repository

import com.example.myapplication.data.local.dao.PlannerItemDao
import com.example.myapplication.data.local.entity.PlannerItemEntity
import com.example.myapplication.domain.model.PlannerItem
import com.example.myapplication.domain.model.Priority
import com.example.myapplication.domain.repository.PlannerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID

class RoomPlannerRepositoryImpl(private val plannerItemDao: PlannerItemDao) : PlannerRepository {

    override fun getItems(): Flow<List<PlannerItem>> {
        return plannerItemDao.getAllItems()
            .map { entities -> entities.map { it.toDomain() } }
            .onStart {
                // Pre-populate the database with mock data if it is empty
                withContext(Dispatchers.IO) {
                    // If empty, add initial data
                    if (plannerItemDao.getItemById("check") == null) {
                        // Arbitrary verification element to prevent re-loading
                        plannerItemDao.insertItem(
                            PlannerItemEntity("check", "System Check", "", null, null, null, null, false, Priority.LOW)
                        )
                        // Add 15 mock tasks
                        List(15) { i ->
                            PlannerItem(
                                id = UUID.randomUUID().toString(),
                                title = "Задача №$i (Room)",
                                description = "Описание для планировщика сферы ${if (i % 2 == 0) "Спорт" else "Дом"}",
                                categoryId = "cat_$i",
                                groupId = "group_1",
                                assigneeId = "user_1",
                                dueDate = Date(),
                                isCompleted = false,
                                priority = when (i % 3) {
                                    0 -> Priority.LOW
                                    1 -> Priority.MEDIUM
                                    else -> Priority.HIGH
                                }
                            )
                        }.forEach { item ->
                            plannerItemDao.insertItem(PlannerItemEntity.fromDomain(item))
                        }
                    }
                }
            }
    }

    override suspend fun getItemById(id: String): PlannerItem? = withContext(Dispatchers.IO) {
        plannerItemDao.getItemById(id)?.toDomain()
    }

    override suspend fun addItem(item: PlannerItem) = withContext(Dispatchers.IO) {
        plannerItemDao.insertItem(PlannerItemEntity.fromDomain(item))
    }

    override suspend fun updateItem(item: PlannerItem) = withContext(Dispatchers.IO) {
        plannerItemDao.updateItem(PlannerItemEntity.fromDomain(item))
    }

    override suspend fun deleteItem(id: String) = withContext(Dispatchers.IO) {
        plannerItemDao.deleteItemById(id)
    }
}
