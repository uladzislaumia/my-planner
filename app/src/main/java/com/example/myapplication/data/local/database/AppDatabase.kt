package com.example.myapplication.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myapplication.data.local.converter.AppConverters
import com.example.myapplication.data.local.dao.PlannerItemDao
import com.example.myapplication.data.local.entity.PlannerItemEntity

@Database(entities = [PlannerItemEntity::class], version = 1, exportSchema = false)
@TypeConverters(AppConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun plannerItemDao(): PlannerItemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "planner_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
