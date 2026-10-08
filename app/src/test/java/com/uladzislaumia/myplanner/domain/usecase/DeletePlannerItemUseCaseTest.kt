package com.uladzislaumia.myplanner.domain.usecase

import com.uladzislaumia.myplanner.domain.repository.PlannerRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DeletePlannerItemUseCaseTest {

    private val plannerRepository: PlannerRepository = mockk(relaxed = true)
    private val deletePlannerItemUseCase = DeletePlannerItemUseCase(plannerRepository)

    @Test
    fun `invoke calls deleteItem on repository`() = runTest {
        val taskId = "task-123"

        deletePlannerItemUseCase(taskId)

        coVerify { plannerRepository.deleteItem(taskId) }
    }
}
