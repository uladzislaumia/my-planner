package com.uladzislaumia.myplanner.domain.usecase

import com.uladzislaumia.myplanner.domain.repository.PlannerRepository
import javax.inject.Inject

class DeletePlannerItemUseCase @Inject constructor(
    private val plannerRepository: PlannerRepository,
) {
    suspend operator fun invoke(id: String) {
        plannerRepository.deleteItem(id)
    }
}
