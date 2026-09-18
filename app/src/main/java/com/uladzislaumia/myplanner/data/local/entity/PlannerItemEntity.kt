package com.uladzislaumia.myplanner.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.uladzislaumia.myplanner.domain.model.PlannerItem
import com.uladzislaumia.myplanner.domain.model.Priority
import java.util.Date

@Entity(tableName = "planner_items")
data class PlannerItemEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val categoryId: String?,
    val groupId: String?,
    val assigneeId: String?,
    val dueDate: Date?,
    val isCompleted: Boolean,
    val priority: Priority
) {
    fun toDomain(): PlannerItem = PlannerItem(
        id = id,
        title = title,
        description = description,
        categoryId = categoryId,
        groupId = groupId,
        assigneeId = assigneeId,
        dueDate = dueDate,
        isCompleted = isCompleted,
        priority = priority
    )

    companion object {
        fun fromDomain(item: PlannerItem): PlannerItemEntity = PlannerItemEntity(
            id = item.id,
            title = item.title,
            description = item.description,
            categoryId = item.categoryId,
            groupId = item.groupId,
            assigneeId = item.assigneeId,
            dueDate = item.dueDate,
            isCompleted = item.isCompleted,
            priority = item.priority
        )
    }
}
