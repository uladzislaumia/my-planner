package com.uladzislaumia.myplanner.domain.usecase

import com.uladzislaumia.myplanner.domain.model.PlannerItem
import com.uladzislaumia.myplanner.domain.repository.PlannerRepository
import javax.inject.Inject

class UpdatePlannerItemUseCase @Inject constructor(
    private val plannerRepository: PlannerRepository,
) {
    suspend operator fun invoke(item: PlannerItem) {
        plannerRepository.updateItem(item)
    }
}
