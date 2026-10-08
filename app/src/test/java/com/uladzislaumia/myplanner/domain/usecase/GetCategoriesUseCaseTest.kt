package com.uladzislaumia.myplanner.domain.usecase

import app.cash.turbine.test
import com.uladzislaumia.myplanner.domain.model.Category
import com.uladzislaumia.myplanner.domain.repository.CategoryRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetCategoriesUseCaseTest {

    private val categoryRepository: CategoryRepository = mockk()
    private val getCategoriesUseCase = GetCategoriesUseCase(categoryRepository)

    @Test
    fun `invoke returns flow of categories from repository`() = runTest {
        val testCategories = listOf(Category.GROCERIES, Category.WORK)
        coEvery { categoryRepository.getCategories() } returns flowOf(testCategories)

        getCategoriesUseCase().test {
            val categories = awaitItem()
            assertEquals(testCategories, categories)
            awaitComplete()
        }
    }
}
