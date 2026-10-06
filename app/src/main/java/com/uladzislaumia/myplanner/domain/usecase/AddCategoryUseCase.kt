package com.uladzislaumia.myplanner.domain.usecase

import com.uladzislaumia.myplanner.domain.model.Category
import com.uladzislaumia.myplanner.domain.repository.CategoryRepository
import javax.inject.Inject

class AddCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository,
) {
    suspend operator fun invoke(category: Category) {
        categoryRepository.addCategory(category)
    }
}
