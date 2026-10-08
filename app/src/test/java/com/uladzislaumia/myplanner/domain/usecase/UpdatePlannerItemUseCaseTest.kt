package com.uladzislaumia.myplanner.domain.usecase

import com.uladzislaumia.myplanner.domain.model.PlannerItem
import com.uladzislaumia.myplanner.domain.model.Priority
import com.uladzislaumia.myplanner.domain.repository.PlannerRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class UpdatePlannerItemUseCaseTest {

    private val plannerRepository: PlannerRepository = mockk(relaxed = true)
    private val updatePlannerItemUseCase = UpdatePlannerItemUseCase(plannerRepository)

    @Test
    fun `invoke calls updateItem on repository`() = runTest {
        val testItem = PlannerItem(
            id = "1",
            title = "Updated Task",
            description = "Updated Desc",
            categoryId = null,
            groupId = null,
            assigneeId = null,
            dueDate = null,
            priority = Priority.HIGH,
            isCompleted = true,
        )

        updatePlannerItemUseCase(testItem)

        coVerify { plannerRepository.updateItem(testItem) }
    }
}
