package com.example.myapplication.domain.model

import java.util.Date

data class PlannerItem(
    val id: String,
    val title: String,
    val description: String,
    val categoryId: String?,
    val groupId: String?,
    val assigneeId: String?,
    val dueDate: Date?,
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.MEDIUM
)
