package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.PlannerItem
import com.example.myapplication.domain.repository.PlannerRepository
import kotlinx.coroutines.flow.Flow

class GetPlannerItemsUseCase(private val repository: PlannerRepository) {
    operator fun invoke(): Flow<List<PlannerItem>> {
        return repository.getItems()
    }
}
