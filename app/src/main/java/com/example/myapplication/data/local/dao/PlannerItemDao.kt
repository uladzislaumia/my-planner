package com.example.myapplication.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.myapplication.data.local.entity.PlannerItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlannerItemDao {
    @Query("SELECT * FROM planner_items")
    fun getAllItems(): Flow<List<PlannerItemEntity>>

    @Query("SELECT * FROM planner_items WHERE id = :id")
    suspend fun getItemById(id: String): PlannerItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: PlannerItemEntity)

    @Update
    suspend fun updateItem(item: PlannerItemEntity)

    @Query("DELETE FROM planner_items WHERE id = :id")
    suspend fun deleteItemById(id: String)
}
