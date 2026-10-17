package com.uladzislaumia.myplanner.data.local.converter

import androidx.room.TypeConverter
import com.uladzislaumia.myplanner.data.local.entity.SyncState
import com.uladzislaumia.myplanner.domain.model.Priority
import java.util.Date

class AppConverters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time

    @TypeConverter
    fun fromPriority(priority: Priority): String = priority.name

    @TypeConverter
    fun toPriority(value: String): Priority = Priority.valueOf(value)

    @TypeConverter
    fun fromSyncState(syncState: SyncState): String = syncState.name

    @TypeConverter
    fun toSyncState(value: String): SyncState = SyncState.valueOf(value)
}
