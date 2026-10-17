package com.uladzislaumia.myplanner.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.uladzislaumia.myplanner.data.local.converter.AppConverters
import com.uladzislaumia.myplanner.data.local.dao.CategoryDao
import com.uladzislaumia.myplanner.data.local.dao.PlannerItemDao
import com.uladzislaumia.myplanner.data.local.dao.PlannerSyncDao
import com.uladzislaumia.myplanner.data.local.entity.CategoryEntity
import com.uladzislaumia.myplanner.data.local.entity.PlannerItemEntity

@Database(
    entities = [PlannerItemEntity::class, CategoryEntity::class],
    version = 4,
    exportSchema = false,
)
@TypeConverters(AppConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun plannerItemDao(): PlannerItemDao

    abstract fun plannerSyncDao(): PlannerSyncDao

    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                val newInstance =
                    Room
                        .databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java,
                            "planner_database",
                        ).fallbackToDestructiveMigration(dropAllTables = true)
                        .build()
                instance = newInstance
                newInstance
            }
    }
}
