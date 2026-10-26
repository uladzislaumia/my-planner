package com.uladzislaumia.myplanner.data.repository

import com.uladzislaumia.myplanner.data.local.dao.PlannerItemDao
import com.uladzislaumia.myplanner.data.local.dao.PlannerSyncDao
import com.uladzislaumia.myplanner.data.local.entity.PlannerItemEntity
import com.uladzislaumia.myplanner.data.local.entity.SyncState
import com.uladzislaumia.myplanner.data.remote.PlannerRemoteDataSource
import com.uladzislaumia.myplanner.data.sync.SyncScheduler
import com.uladzislaumia.myplanner.domain.model.Category
import com.uladzislaumia.myplanner.domain.model.PlannerItem
import com.uladzislaumia.myplanner.domain.model.Priority
import com.uladzislaumia.myplanner.domain.repository.PlannerRepository
import com.uladzislaumia.myplanner.notification.NotificationScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

/**
 * Room is the single source of truth: every change is written locally right away and marked
 * as pending ([SyncState]), then [SyncScheduler] uploads pending changes to Firestore in the background.
 */
class OfflineFirstPlannerRepositoryImpl
    @Inject
    constructor(
        private val plannerItemDao: PlannerItemDao,
        private val plannerSyncDao: PlannerSyncDao,
        private val remoteDataSource: PlannerRemoteDataSource,
        private val syncScheduler: SyncScheduler,
        private val notificationScheduler: NotificationScheduler,
    ) : PlannerRepository {
        override fun getItems(): Flow<List<PlannerItem>> =
            plannerItemDao
                .getAllItems()
                .map { entities -> entities.map { it.toDomain() } }
                .onStart {
                    // Background sync: Fetch from Firestore and update Room
                    syncFromCloud()
                }

        private suspend fun syncFromCloud() =
            withContext(Dispatchers.IO) {
                val uid = remoteDataSource.currentUserId ?: return@withContext
                Timber.d("Starting background sync from cloud")
                try {
                    val remoteItems = remoteDataSource.fetchItems(uid)
                    val localCount = plannerItemDao.getItemCount()
                    if (remoteItems.isEmpty() && localCount == 0) {
                        Timber.d("No items found in cloud or local DB. Seeding 5 demo items")
                        seedDemoItems()
                    } else {
                        // Update local database with fresh cloud data, keeping not yet uploaded local edits
                        val applied = plannerSyncDao.upsertFromRemote(remoteItems)
                        applied.forEach { notificationScheduler.scheduleTaskReminder(it.toDomain()) }
                        Timber.d("Synced ${applied.size} items from cloud")
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Failed to sync from cloud")
                    if (plannerItemDao.getItemCount() == 0) {
                        Timber.d("Offline or cloud error and local DB is empty. Seeding 5 demo items")
                        seedDemoItems()
                    }
                }
                // Push anything left over from previous sessions (e.g. edits made offline)
                syncScheduler.requestSync(delaySeconds = 0)
            }

        private suspend fun seedDemoItems() {
            // Each account gets its own ids, so the templates are copied with fresh UUIDs
            DEMO_ITEMS.forEach { addItem(it.copy(id = UUID.randomUUID().toString())) }
        }

        override suspend fun getItemById(id: String): PlannerItem? =
            withContext(Dispatchers.IO) {
                plannerItemDao.getItemById(id)?.toDomain()
            }

        override suspend fun addItem(item: PlannerItem): Unit =
            withContext(Dispatchers.IO) {
                Timber.d("Saving item locally: ${item.id}")
                val entity =
                    PlannerItemEntity.fromDomain(
                        item = item,
                        syncState = SyncState.PENDING_UPSERT,
                        localUpdatedAt = System.currentTimeMillis(),
                    )
                plannerItemDao.insertItem(entity)
                notificationScheduler.scheduleTaskReminder(item)
                syncScheduler.requestSync()
            }

        override suspend fun updateItem(item: PlannerItem) {
            addItem(item)
        }

        override suspend fun deleteItem(id: String): Unit =
            withContext(Dispatchers.IO) {
                Timber.d("Deleting item locally: $id")
                // Keep a tombstone until the deletion reaches Firestore
                plannerItemDao.markDeleted(id, localUpdatedAt = System.currentTimeMillis())
                notificationScheduler.cancelTaskReminder(id)
                syncScheduler.requestSync()
            }
    }

/** Sample tasks for new accounts; ids are assigned when seeding. */
private val DEMO_ITEMS =
    listOf(
        PlannerItem(
            id = "",
            title = "Buy ingredients for dinner",
            description = "Get fresh vegetables, pasta, and spices for tonight's meal.",
            categoryId = Category.GROCERIES.id,
            groupId = null,
            assigneeId = null,
            dueDate = null,
            priority = Priority.MEDIUM,
            isCompleted = false,
        ),
        PlannerItem(
            id = "",
            title = "Book tickets and hotel for vacation",
            description = "Reserve flight tickets and hotel accommodation for the upcoming trip.",
            categoryId = Category.TRAVEL.id,
            groupId = null,
            assigneeId = null,
            dueDate = null,
            priority = Priority.HIGH,
            isCompleted = false,
        ),
        PlannerItem(
            id = "",
            title = "Go to the cinema this weekend",
            description = "Watch the new movie premiere with friends on Saturday night.",
            categoryId = Category.ENTERTAINMENT.id,
            groupId = null,
            assigneeId = null,
            dueDate = null,
            priority = Priority.LOW,
            isCompleted = false,
        ),
        PlannerItem(
            id = "",
            title = "Do a morning yoga session",
            description = "Complete a 30-minute stretching and yoga workout.",
            categoryId = Category.SPORTS.id,
            groupId = null,
            assigneeId = null,
            dueDate = null,
            priority = Priority.MEDIUM,
            isCompleted = false,
        ),
        PlannerItem(
            id = "",
            title = "Visit the doctor for a check-up",
            description = "Schedule and attend annual medical health check-up.",
            categoryId = Category.HEALTH.id,
            groupId = null,
            assigneeId = null,
            dueDate = null,
            priority = Priority.HIGH,
            isCompleted = true,
        ),
    )
