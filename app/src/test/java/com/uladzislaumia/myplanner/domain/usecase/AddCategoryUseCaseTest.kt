package com.uladzislaumia.myplanner.domain.usecase

import com.uladzislaumia.myplanner.domain.model.Category
import com.uladzislaumia.myplanner.domain.repository.CategoryRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class AddCategoryUseCaseTest {

    private val categoryRepository: CategoryRepository = mockk(relaxed = true)
    private val addCategoryUseCase = AddCategoryUseCase(categoryRepository)

    @Test
    fun `invoke calls addCategory on repository`() = runTest {
        val testCategory = Category.GROCERIES

        addCategoryUseCase(testCategory)

        coVerify { categoryRepository.addCategory(testCategory) }
    }
}
