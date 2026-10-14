package com.uladzislaumia.myplanner.data.local.converter

import androidx.room.TypeConverter
import com.uladzislaumia.myplanner.data.local.entity.SyncState
import com.uladzislaumia.myplanner.domain.model.Priority
import java.util.Date

class AppConverters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromPriority(priority: Priority): String {
        return priority.name
    }

    @TypeConverter
    fun toPriority(value: String): Priority {
        return Priority.valueOf(value)
    }

    @TypeConverter
    fun fromSyncState(syncState: SyncState): String {
        return syncState.name
    }

    @TypeConverter
    fun toSyncState(value: String): SyncState {
        return SyncState.valueOf(value)
    }
}
