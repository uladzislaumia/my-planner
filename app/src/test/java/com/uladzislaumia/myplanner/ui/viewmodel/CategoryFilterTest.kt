package com.uladzislaumia.myplanner.ui.viewmodel

import com.uladzislaumia.myplanner.domain.model.PlannerItem
import com.uladzislaumia.myplanner.domain.model.Priority
import com.uladzislaumia.myplanner.ui.navigation.Route
import org.junit.Assert.assertEquals
import org.junit.Test

class CategoryFilterTest {
    private val groceriesItem = item(id = "1", categoryId = "groceries")
    private val workItem = item(id = "2", categoryId = "work")
    private val uncategorizedItem = item(id = "3", categoryId = null)
    private val completedGroceriesItem = item(id = "4", categoryId = "groceries", isCompleted = true)
    private val items = listOf(groceriesItem, workItem, uncategorizedItem, completedGroceriesItem)

    private fun item(
        id: String,
        categoryId: String?,
        isCompleted: Boolean = false,
    ) = PlannerItem(
        id = id,
        title = "Task $id",
        description = "",
        categoryId = categoryId,
        groupId = null,
        assigneeId = null,
        dueDate = null,
        isCompleted = isCompleted,
        priority = Priority.MEDIUM,
    )

    @Test
    fun `All matches every item`() {
        assertEquals(items, items.filter(CategoryFilter.All::matches))
    }

    @Test
    fun `Uncategorized matches only items without a category`() {
        assertEquals(listOf(uncategorizedItem), items.filter(CategoryFilter.Uncategorized::matches))
    }

    @Test
    fun `Of matches the category's items, completed ones included`() {
        assertEquals(
            listOf(groceriesItem, completedGroceriesItem),
            items.filter(CategoryFilter.Of("groceries")::matches),
        )
    }

    @Test
    fun `CategoryTasks route keeps the filter`() {
        listOf(CategoryFilter.All, CategoryFilter.Uncategorized, CategoryFilter.Of("work")).forEach { filter ->
            assertEquals(filter, Route.CategoryTasks.of(filter).filter)
        }
    }
}
