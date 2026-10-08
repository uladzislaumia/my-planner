package com.uladzislaumia.myplanner.domain.usecase

import com.uladzislaumia.myplanner.domain.model.PlannerItem
import com.uladzislaumia.myplanner.domain.model.Priority
import com.uladzislaumia.myplanner.domain.repository.PlannerRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class AddPlannerItemUseCaseTest {

    private val plannerRepository: PlannerRepository = mockk(relaxed = true)
    private val addPlannerItemUseCase = AddPlannerItemUseCase(plannerRepository)

    @Test
    fun `invoke calls addItem on repository`() = runTest {
        val testItem = PlannerItem(
            id = "1",
            title = "Task",
            description = "Desc",
            categoryId = null,
            groupId = null,
            assigneeId = null,
            dueDate = null,
            priority = Priority.HIGH,
            isCompleted = false,
        )

        addPlannerItemUseCase(testItem)

        coVerify { plannerRepository.addItem(testItem) }
    }
}
