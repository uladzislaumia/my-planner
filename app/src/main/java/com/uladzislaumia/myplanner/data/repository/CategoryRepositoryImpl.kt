package com.uladzislaumia.myplanner.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.uladzislaumia.myplanner.data.local.dao.CategoryDao
import com.uladzislaumia.myplanner.data.local.entity.CategoryEntity
import com.uladzislaumia.myplanner.domain.model.Category
import com.uladzislaumia.myplanner.domain.repository.AuthRepository
import com.uladzislaumia.myplanner.domain.repository.CategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository,
) : CategoryRepository {

    private val userId: String?
        get() = authRepository.getCurrentUser()?.id

    private val categoriesCollection
        get() = userId?.let {
            firestore.collection("users").document(it).collection("categories")
        }

    override fun getCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories()
            .map { entities -> entities.map { it.toDomain() } }
            .onStart {
                syncCategoriesFromCloud()
            }
    }

    private suspend fun syncCategoriesFromCloud() = withContext(Dispatchers.IO) {
        val collection = categoriesCollection ?: return@withContext
        Timber.d("Starting background category sync from cloud for user: $userId")
        try {
            val snapshot = collection.get().await()
            val remoteCategories = snapshot.documents.mapNotNull { doc ->
                try {
                    CategoryEntity(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        icon = doc.getString("icon") ?: "📂",
                        colorHex = doc.getString("colorHex") ?: "#4CAF50",
                    )
                } catch (e: Exception) {
                    Timber.e(e, "Error parsing firestore category document: ${doc.id}")
                    null
                }
            }

            val localCount = categoryDao.getCategoryCount()
            if (remoteCategories.isEmpty() && localCount == 0) {
                Timber.d("No categories in cloud or local DB. Seeding default categories for user: $userId")
                seedDefaultCategories()
            } else {
                remoteCategories.forEach { categoryDao.insertCategory(it) }
                Timber.d("Synced ${remoteCategories.size} categories from cloud")
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to sync categories from cloud")
            if (categoryDao.getCategoryCount() == 0) {
                seedDefaultCategories()
            }
        }
    }

    private suspend fun seedDefaultCategories() {
        Category.DEFAULT_CATEGORIES.forEach { addCategory(it) }
    }

    override suspend fun addCategory(category: Category): Unit = withContext(Dispatchers.IO) {
        // 1. Save to Local immediately (Instant UI feedback)
        val entity = CategoryEntity.fromDomain(category)
        categoryDao.insertCategory(entity)

        // 2. Sync to Cloud
        try {
            categoriesCollection?.document(category.id)?.set(
                mapOf(
                    "name" to category.name,
                    "icon" to category.icon,
                    "colorHex" to category.colorHex
                )
            )?.await()
            Timber.d("Successfully synced category ${category.id} to cloud")
        } catch (e: Exception) {
            Timber.e(e, "Failed to sync category ${category.id} to cloud")
        }
        Unit
    }
}
