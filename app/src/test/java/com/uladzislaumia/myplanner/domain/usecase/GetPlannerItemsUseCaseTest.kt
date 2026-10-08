package com.uladzislaumia.myplanner.domain.usecase

import app.cash.turbine.test
import com.uladzislaumia.myplanner.domain.model.PlannerItem
import com.uladzislaumia.myplanner.domain.model.Priority
import com.uladzislaumia.myplanner.domain.repository.PlannerRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetPlannerItemsUseCaseTest {

    private val plannerRepository: PlannerRepository = mockk()
    private val getPlannerItemsUseCase = GetPlannerItemsUseCase(plannerRepository)

    @Test
    fun `invoke returns flow of items from repository`() = runTest {
        val testItem = PlannerItem(
            id = "1",
            title = "Test Task",
            description = "Description",
            categoryId = null,
            groupId = null,
            assigneeId = null,
            dueDate = null,
            priority = Priority.HIGH,
            isCompleted = false,
        )
        coEvery { plannerRepository.getItems() } returns flowOf(listOf(testItem))

        getPlannerItemsUseCase().test {
            val items = awaitItem()
            assertEquals(listOf(testItem), items)
            awaitComplete()
        }
    }
}
