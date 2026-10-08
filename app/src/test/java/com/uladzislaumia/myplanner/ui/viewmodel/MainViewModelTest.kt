package com.uladzislaumia.myplanner.ui.viewmodel

import app.cash.turbine.test
import com.uladzislaumia.myplanner.MainDispatcherRule
import com.uladzislaumia.myplanner.domain.model.Category
import com.uladzislaumia.myplanner.domain.model.PlannerItem
import com.uladzislaumia.myplanner.domain.model.Priority
import com.uladzislaumia.myplanner.domain.usecase.AddCategoryUseCase
import com.uladzislaumia.myplanner.domain.usecase.AddPlannerItemUseCase
import com.uladzislaumia.myplanner.domain.usecase.DeletePlannerItemUseCase
import com.uladzislaumia.myplanner.domain.usecase.GetCategoriesUseCase
import com.uladzislaumia.myplanner.domain.usecase.GetPlannerItemsUseCase
import com.uladzislaumia.myplanner.domain.usecase.UpdatePlannerItemUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getPlannerItemsUseCase: GetPlannerItemsUseCase = mockk()
    private val addPlannerItemUseCase: AddPlannerItemUseCase = mockk(relaxed = true)
    private val updatePlannerItemUseCase: UpdatePlannerItemUseCase = mockk(relaxed = true)
    private val deletePlannerItemUseCase: DeletePlannerItemUseCase = mockk(relaxed = true)
    private val getCategoriesUseCase: GetCategoriesUseCase = mockk()
    private val addCategoryUseCase: AddCategoryUseCase = mockk(relaxed = true)

    private val testItem1 = PlannerItem(
        id = "1",
        title = "Task 1",
        description = "Desc 1",
        categoryId = "groceries",
        groupId = null,
        assigneeId = null,
        dueDate = null,
        priority = Priority.HIGH,
        isCompleted = false,
    )

    private val testItem2 = PlannerItem(
        id = "2",
        title = "Task 2",
        description = "Desc 2",
        categoryId = "work",
        groupId = null,
        assigneeId = null,
        dueDate = null,
        priority = Priority.LOW,
        isCompleted = false,
    )

    @Before
    fun setup() {
        coEvery { getPlannerItemsUseCase() } returns flowOf(listOf(testItem1, testItem2))
        coEvery { getCategoriesUseCase() } returns flowOf(Category.DEFAULT_CATEGORIES)
    }

    private fun createViewModel() = MainViewModel(
        getPlannerItemsUseCase = getPlannerItemsUseCase,
        addPlannerItemUseCase = addPlannerItemUseCase,
        updatePlannerItemUseCase = updatePlannerItemUseCase,
        deletePlannerItemUseCase = deletePlannerItemUseCase,
        getCategoriesUseCase = getCategoriesUseCase,
        addCategoryUseCase = addCategoryUseCase,
    )

    @Test
    fun `categories emits list of categories from getCategoriesUseCase`() = runTest {
        val viewModel = createViewModel()

        viewModel.categories.test {
            val categoryList = awaitItem()
            assertEquals(Category.DEFAULT_CATEGORIES, categoryList)
        }
    }

    @Test
    fun `uiState emits Success with all items initially`() = runTest {
        val viewModel = createViewModel()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue("Expected MainUiState.Success, but was $state", state is MainUiState.Success)
            if (state is MainUiState.Success) {
                assertEquals(listOf(testItem1, testItem2), state.items)
            }
        }
    }

    @Test
    fun `selectCategory filters items by categoryId`() = runTest {
        val viewModel = createViewModel()

        viewModel.selectCategory("groceries")

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue("Expected MainUiState.Success, but was $state", state is MainUiState.Success)
            if (state is MainUiState.Success) {
                assertEquals(listOf(testItem1), state.items)
            }
        }
    }

    @Test
    fun `uiState emits Error when getPlannerItemsUseCase throws exception`() = runTest {
        coEvery { getPlannerItemsUseCase() } returns flow { throw IllegalStateException("Database Connection Failed") }
        val viewModel = createViewModel()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue("Expected MainUiState.Error, but was $state", state is MainUiState.Error)
            if (state is MainUiState.Error) {
                assertEquals("Database Connection Failed", state.message)
            }
        }
    }

    @Test
    fun `addCategory invokes addCategoryUseCase when name is valid`() = runTest {
        val viewModel = createViewModel()

        viewModel.addCategory("Shopping", "🛍️", "#3F51B5")

        coVerify { addCategoryUseCase(any()) }
    }

    @Test
    fun `addCategory does not invoke addCategoryUseCase when name is blank`() = runTest {
        val viewModel = createViewModel()

        viewModel.addCategory("   ", "🛍️", "#3F51B5")

        coVerify(exactly = 0) { addCategoryUseCase(any()) }
    }

    @Test
    fun `addItem invokes addPlannerItemUseCase when title is valid`() = runTest {
        val viewModel = createViewModel()

        viewModel.addItem("New Task", "New Desc", Priority.MEDIUM, "groceries")

        coVerify { addPlannerItemUseCase(any()) }
    }

    @Test
    fun `addItem does not invoke addPlannerItemUseCase when title is blank`() = runTest {
        val viewModel = createViewModel()

        viewModel.addItem("  ", "New Desc", Priority.MEDIUM, "groceries")

        coVerify(exactly = 0) { addPlannerItemUseCase(any()) }
    }

    @Test
    fun `updateItem invokes updatePlannerItemUseCase`() = runTest {
        val viewModel = createViewModel()

        viewModel.updateItem(testItem1)

        coVerify { updatePlannerItemUseCase(testItem1) }
    }

    @Test
    fun `deleteItem invokes deletePlannerItemUseCase`() = runTest {
        val viewModel = createViewModel()

        viewModel.deleteItem("1")

        coVerify { deletePlannerItemUseCase("1") }
    }
}
