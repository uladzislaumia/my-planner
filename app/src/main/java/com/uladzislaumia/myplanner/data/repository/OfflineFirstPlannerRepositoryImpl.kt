package com.uladzislaumia.myplanner.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.uladzislaumia.myplanner.data.local.dao.PlannerItemDao
import com.uladzislaumia.myplanner.data.local.entity.PlannerItemEntity
import com.uladzislaumia.myplanner.domain.model.PlannerItem
import com.uladzislaumia.myplanner.domain.model.Priority
import com.uladzislaumia.myplanner.domain.repository.AuthRepository
import com.uladzislaumia.myplanner.domain.repository.PlannerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

class OfflineFirstPlannerRepositoryImpl @Inject constructor(
    private val plannerItemDao: PlannerItemDao,
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository
) : PlannerRepository {

    private val userId: String?
        get() = authRepository.getCurrentUser()?.id

    private val itemsCollection
        get() = userId?.let {
            firestore.collection("users").document(it).collection("items")
        }

    override fun getItems(): Flow<List<PlannerItem>> {
        return plannerItemDao.getAllItems()
            .map { entities -> entities.map { it.toDomain() } }
            .onStart {
                // Background sync: Fetch from Firestore and update Room
                syncFromCloud()
            }
    }

    private suspend fun syncFromCloud() = withContext(Dispatchers.IO) {
        val collection = itemsCollection ?: return@withContext
        Timber.d("Starting background sync from cloud for user: $userId")
        try {
            val snapshot = collection.get().await()
            val remoteItems = snapshot.documents.mapNotNull { doc ->
                try {
                    PlannerItemEntity(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        description = doc.getString("description") ?: "",
                        categoryId = doc.getString("categoryId"),
                        groupId = doc.getString("groupId"),
                        assigneeId = doc.getString("assigneeId"),
                        dueDate = doc.getDate("dueDate"),
                        isCompleted = doc.getBoolean("isCompleted") ?: false,
                        priority = Priority.valueOf(doc.getString("priority") ?: "MEDIUM")
                    )
                } catch (e: Exception) {
                    Timber.e(e, "Error parsing firestore document: ${doc.id}")
                    null
                }
            }
            val localCount = plannerItemDao.getItemCount()
            if (remoteItems.isEmpty() && localCount == 0) {
                Timber.d("No items found in cloud or local DB. Seeding 5 demo items for user: $userId")
                seedDemoItems()
            } else {
                // Update local database with fresh cloud data
                remoteItems.forEach { plannerItemDao.insertItem(it) }
                Timber.d("Synced ${remoteItems.size} items from cloud")
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to sync from cloud")
            if (plannerItemDao.getItemCount() == 0) {
                Timber.d("Offline or cloud error and local DB is empty. Seeding 5 demo items for user: $userId")
                seedDemoItems()
            }
        }
    }

    private suspend fun seedDemoItems() {
        val demoItems = listOf(
            PlannerItem(
                id = UUID.randomUUID().toString(),
                title = "Welcome to My Planner! 👋",
                description = "This is your first personal task. You can edit, delete, or complete it.",
                categoryId = null,
                groupId = null,
                assigneeId = null,
                dueDate = null,
                priority = Priority.HIGH,
                isCompleted = false
            ),
            PlannerItem(
                id = UUID.randomUUID().toString(),
                title = "Explore App Features 🚀",
                description = "Get familiar with the planner grid, task priorities, and automatic cloud synchronization.",
                categoryId = null,
                groupId = null,
                assigneeId = null,
                dueDate = null,
                priority = Priority.MEDIUM,
                isCompleted = false
            ),
            PlannerItem(
                id = UUID.randomUUID().toString(),
                title = "Plan Your Week 📅",
                description = "Add important meetings, upcoming deadlines, and daily to-do lists.",
                categoryId = null,
                groupId = null,
                assigneeId = null,
                dueDate = null,
                priority = Priority.HIGH,
                isCompleted = false
            ),
            PlannerItem(
                id = UUID.randomUUID().toString(),
                title = "Set Up User Profile 👤",
                description = "Fill in your personal profile information in account settings.",
                categoryId = null,
                groupId = null,
                assigneeId = null,
                dueDate = null,
                priority = Priority.LOW,
                isCompleted = false
            ),
            PlannerItem(
                id = UUID.randomUUID().toString(),
                title = "Sign Up for My Planner ✅",
                description = "You have successfully created an account and signed in.",
                categoryId = null,
                groupId = null,
                assigneeId = null,
                dueDate = null,
                priority = Priority.LOW,
                isCompleted = true
            )
        )
        demoItems.forEach { addItem(it) }
    }

    override suspend fun getItemById(id: String): PlannerItem? = withContext(Dispatchers.IO) {
        plannerItemDao.getItemById(id)?.toDomain()
    }

    override suspend fun addItem(item: PlannerItem): Unit = withContext(Dispatchers.IO) {
        Timber.d("Adding item: ${item.id}")
        // 1. Save to Local immediately (Instant UI feedback)
        val entity = PlannerItemEntity.fromDomain(item)
        plannerItemDao.insertItem(entity)

        // 2. Sync to Cloud
        try {
            itemsCollection?.document(item.id)?.set(
                mapOf(
                    "title" to item.title,
                    "description" to item.description,
                    "categoryId" to item.categoryId,
                    "groupId" to item.groupId,
                    "assigneeId" to item.assigneeId,
                    "dueDate" to item.dueDate,
                    "isCompleted" to item.isCompleted,
                    "priority" to item.priority.name
                )
            )?.await()
            Timber.d("Successfully synced item ${item.id} to cloud")
        } catch (e: Exception) {
            Timber.e(e, "Failed to sync item ${item.id} to cloud")
        }
        Unit
    }

    override suspend fun updateItem(item: PlannerItem) {
        addItem(item) // In Firestore/Room set/insert with same ID works as update
    }

    override suspend fun deleteItem(id: String): Unit = withContext(Dispatchers.IO) {
        Timber.d("Deleting item: $id")
        // 1. Delete from Local immediately
        plannerItemDao.deleteItemById(id)

        // 2. Sync to Cloud
        try {
            itemsCollection?.document(id)?.delete()?.await()
            Timber.d("Successfully deleted item $id from cloud")
        } catch (e: Exception) {
            Timber.e(e, "Failed to delete item $id from cloud")
        }
        Unit
    }
}
