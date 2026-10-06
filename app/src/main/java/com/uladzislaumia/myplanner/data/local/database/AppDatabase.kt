package com.uladzislaumia.myplanner.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.uladzislaumia.myplanner.data.local.converter.AppConverters
import com.uladzislaumia.myplanner.data.local.dao.CategoryDao
import com.uladzislaumia.myplanner.data.local.dao.PlannerItemDao
import com.uladzislaumia.myplanner.data.local.entity.CategoryEntity
import com.uladzislaumia.myplanner.data.local.entity.PlannerItemEntity

@Database(
    entities = [PlannerItemEntity::class, CategoryEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(AppConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun plannerItemDao(): PlannerItemDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "planner_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
