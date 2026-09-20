package com.uladzislaumia.myplanner.domain.usecase

import com.uladzislaumia.myplanner.domain.model.PlannerItem
import com.uladzislaumia.myplanner.domain.repository.PlannerRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetPlannerItemsUseCase @Inject constructor(private val repository: PlannerRepository) {
    operator fun invoke(): Flow<List<PlannerItem>> {
        return repository.getItems()
    }
}
