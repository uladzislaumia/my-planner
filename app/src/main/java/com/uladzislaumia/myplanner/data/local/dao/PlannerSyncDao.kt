package com.uladzislaumia.myplanner.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.uladzislaumia.myplanner.data.local.entity.PlannerItemEntity

/** Queries used by cloud synchronization: uploading pending changes and applying remote data. */
@Dao
interface PlannerSyncDao {
    @Query("SELECT * FROM planner_items WHERE syncState != 'SYNCED'")
    suspend fun getPendingSyncItems(): List<PlannerItemEntity>

    @Query("SELECT id FROM planner_items WHERE syncState != 'SYNCED'")
    suspend fun getPendingSyncIds(): List<String>

    // Only marks as synced if the item wasn't modified again while it was being uploaded
    @Query(
        """
        UPDATE planner_items
        SET syncState = 'SYNCED'
        WHERE id = :id AND localUpdatedAt = :localUpdatedAt
        """,
    )
    suspend fun markSynced(
        id: String,
        localUpdatedAt: Long,
    )

    @Query(
        """
        DELETE FROM planner_items
        WHERE id = :id AND syncState = 'PENDING_DELETE' AND localUpdatedAt = :localUpdatedAt
        """,
    )
    suspend fun deleteSyncedTombstone(
        id: String,
        localUpdatedAt: Long,
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<PlannerItemEntity>)

    /** Applies cloud data without overwriting local changes that haven't been uploaded yet. */
    @Transaction
    suspend fun upsertFromRemote(items: List<PlannerItemEntity>): List<PlannerItemEntity> {
        val pendingIds = getPendingSyncIds().toSet()
        val applied = items.filterNot { it.id in pendingIds }
        insertItems(applied)
        return applied
    }
}
