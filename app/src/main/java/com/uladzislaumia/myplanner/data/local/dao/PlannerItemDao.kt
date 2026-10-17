package com.uladzislaumia.myplanner.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.uladzislaumia.myplanner.data.local.entity.PlannerItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlannerItemDao {
    @Query("SELECT * FROM planner_items WHERE syncState != 'PENDING_DELETE'")
    fun getAllItems(): Flow<List<PlannerItemEntity>>

    @Query("SELECT * FROM planner_items WHERE id = :id AND syncState != 'PENDING_DELETE'")
    suspend fun getItemById(id: String): PlannerItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: PlannerItemEntity)

    @Update
    suspend fun updateItem(item: PlannerItemEntity)

    @Query("DELETE FROM planner_items WHERE id = :id")
    suspend fun deleteItemById(id: String)

    @Query("SELECT COUNT(*) FROM planner_items WHERE syncState != 'PENDING_DELETE'")
    suspend fun getItemCount(): Int

    @Query(
        """
        UPDATE planner_items
        SET syncState = 'PENDING_DELETE', localUpdatedAt = :localUpdatedAt
        WHERE id = :id
        """,
    )
    suspend fun markDeleted(
        id: String,
        localUpdatedAt: Long,
    )
}
