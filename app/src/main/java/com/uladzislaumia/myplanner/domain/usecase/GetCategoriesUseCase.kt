package com.uladzislaumia.myplanner.domain.usecase

import com.uladzislaumia.myplanner.domain.model.Category
import com.uladzislaumia.myplanner.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository,
) {
    operator fun invoke(): Flow<List<Category>> {
        return categoryRepository.getCategories()
    }
}
