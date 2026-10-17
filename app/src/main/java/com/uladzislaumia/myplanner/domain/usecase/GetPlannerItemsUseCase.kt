package com.uladzislaumia.myplanner.domain.usecase

import com.uladzislaumia.myplanner.domain.model.PlannerItem
import com.uladzislaumia.myplanner.domain.repository.PlannerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPlannerItemsUseCase
    @Inject
    constructor(
        private val repository: PlannerRepository,
    ) {
        operator fun invoke(): Flow<List<PlannerItem>> = repository.getItems()
    }
