package com.uladzislaumia.myplanner.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.uladzislaumia.myplanner.data.local.entity.PlannerItemEntity
import com.uladzislaumia.myplanner.data.local.entity.SyncState
import com.uladzislaumia.myplanner.domain.model.Priority
import com.uladzislaumia.myplanner.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlannerRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository,
) {

    /**
     * Id of the signed-in user, or null when signed out.
     * Callers read it once and pass it explicitly, so a sign-out or account switch in the middle
     * of an operation can't redirect it to another user's collection.
     */
    val currentUserId: String?
        get() = authRepository.getCurrentUser()?.id

    private fun itemsCollection(uid: String) =
        firestore.collection("users").document(uid).collection("items")

    suspend fun fetchItems(uid: String): List<PlannerItemEntity> {
        val snapshot = itemsCollection(uid).get().await()
        return snapshot.documents.mapNotNull { doc ->
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
                    priority = Priority.valueOf(doc.getString("priority") ?: "MEDIUM"),
                )
            } catch (e: Exception) {
                Timber.e(e, "Error parsing firestore document: ${doc.id}")
                null
            }
        }
    }

    /**
     * Uploads all changes in a single batch (one network round trip).
     * Deleted items are removed from Firestore, the rest are written in full.
     * Throws if the write is rejected, e.g. when [uid] is no longer signed in.
     */
    suspend fun pushChanges(uid: String, items: List<PlannerItemEntity>) {
        val collection = itemsCollection(uid)
        items.chunked(MAX_BATCH_SIZE).forEach { chunk ->
            val batch = firestore.batch()
            chunk.forEach { item ->
                val doc = collection.document(item.id)
                if (item.syncState == SyncState.PENDING_DELETE) batch.delete(doc) else batch.set(doc, item.toFirestoreMap())
            }
            batch.commit().await()
        }
    }

    private fun PlannerItemEntity.toFirestoreMap() = mapOf(
        "title" to title,
        "description" to description,
        "categoryId" to categoryId,
        "groupId" to groupId,
        "assigneeId" to assigneeId,
        "dueDate" to dueDate,
        "isCompleted" to isCompleted,
        "priority" to priority.name,
    )

    private companion object {
        const val MAX_BATCH_SIZE = 500
    }
}
