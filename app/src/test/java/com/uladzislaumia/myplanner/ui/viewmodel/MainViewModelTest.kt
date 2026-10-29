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
import com.uladzislaumia.myplanner.ui.util.startOfDay
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Calendar
import java.util.Date

class MainViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getPlannerItemsUseCase: GetPlannerItemsUseCase = mockk()
    private val addPlannerItemUseCase: AddPlannerItemUseCase = mockk(relaxed = true)
    private val updatePlannerItemUseCase: UpdatePlannerItemUseCase = mockk(relaxed = true)
    private val deletePlannerItemUseCase: DeletePlannerItemUseCase = mockk(relaxed = true)
    private val getCategoriesUseCase: GetCategoriesUseCase = mockk()
    private val addCategoryUseCase: AddCategoryUseCase = mockk(relaxed = true)

    private val testItem1 =
        PlannerItem(
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

    private val testItem2 =
        PlannerItem(
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

    private val morningItem = testItem1.copy(id = "morning", dueDate = dateAt(daysFromToday = 1, hour = 8))
    private val eveningItem = testItem1.copy(id = "evening", dueDate = dateAt(daysFromToday = 1, hour = 20))
    private val nextDayItem = testItem1.copy(id = "next-day", dueDate = dateAt(daysFromToday = 2, hour = 9))
    private val undatedItem = testItem1.copy(id = "undated", dueDate = null)
    private val yesterdayItem = testItem1.copy(id = "yesterday", dueDate = dateAt(daysFromToday = -1, hour = 12))

    // Dates are relative to today, because the calendar hides days before today
    private fun dateAt(
        daysFromToday: Int,
        hour: Int,
    ): Date =
        Calendar
            .getInstance()
            .apply {
                time = Date().startOfDay()
                add(Calendar.DAY_OF_MONTH, daysFromToday)
                set(Calendar.HOUR_OF_DAY, hour)
            }.time

    @Before
    fun setup() {
        coEvery { getPlannerItemsUseCase() } returns flowOf(listOf(testItem1, testItem2))
        coEvery { getCategoriesUseCase() } returns flowOf(Category.DEFAULT_CATEGORIES)
    }

    private fun createViewModel() =
        MainViewModel(
            getPlannerItemsUseCase = getPlannerItemsUseCase,
            addPlannerItemUseCase = addPlannerItemUseCase,
            updatePlannerItemUseCase = updatePlannerItemUseCase,
            deletePlannerItemUseCase = deletePlannerItemUseCase,
            getCategoriesUseCase = getCategoriesUseCase,
            addCategoryUseCase = addCategoryUseCase,
        )

    @Test
    fun `categories emits list of categories from getCategoriesUseCase`() =
        runTest {
            val viewModel = createViewModel()

            viewModel.categories.test {
                val categoryList = awaitItem()
                assertEquals(Category.DEFAULT_CATEGORIES, categoryList)
            }
        }

    @Test
    fun `uiState emits Success with all items initially`() =
        runTest {
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
    fun `selectedDate is today by default`() {
        val viewModel = createViewModel()

        assertEquals(Date().startOfDay(), viewModel.selectedDate.value)
    }

    @Test
    fun `newTaskDueDate is 9 am of a selected future day`() {
        val viewModel = createViewModel()
        viewModel.selectDate(dateAt(daysFromToday = 2, hour = 15))

        assertEquals(dateAt(daysFromToday = 2, hour = 9), viewModel.newTaskDueDate())
    }

    @Test
    fun `newTaskDueDate is the next whole hour today`() {
        val viewModel = createViewModel()
        val halfPastTwo = Date(dateAt(daysFromToday = 0, hour = 14).time + HALF_HOUR_MILLIS)

        assertEquals(dateAt(daysFromToday = 0, hour = 15), viewModel.newTaskDueDate(now = halfPastTwo))
    }

    @Test
    fun `newTaskDueDate is null late in the evening and for a past day`() {
        val viewModel = createViewModel()
        val halfPastEleven = Date(dateAt(daysFromToday = 0, hour = 23).time + HALF_HOUR_MILLIS)

        assertNull(viewModel.newTaskDueDate(now = halfPastEleven))

        viewModel.selectDate(dateAt(daysFromToday = -1, hour = 15))
        assertNull(viewModel.newTaskDueDate())
    }

    @Test
    fun `calendarSections group dated items by day sorted by time and skip undated items`() =
        runTest {
            coEvery { getPlannerItemsUseCase() } returns
                flowOf(listOf(undatedItem, nextDayItem, eveningItem, morningItem))
            val viewModel = createViewModel()

            viewModel.uiState.test {
                val state = awaitItem()
                assertTrue("Expected MainUiState.Success, but was $state", state is MainUiState.Success)
                if (state is MainUiState.Success) {
                    assertEquals(
                        listOf(
                            DaySection(dateAt(daysFromToday = 0, hour = 0), emptyList()),
                            DaySection(dateAt(daysFromToday = 1, hour = 0), listOf(morningItem, eveningItem)),
                            DaySection(dateAt(daysFromToday = 2, hour = 0), listOf(nextDayItem)),
                        ),
                        state.calendarSections,
                    )
                }
            }
        }

    @Test
    fun `calendarSections always contain today and tomorrow`() =
        runTest {
            coEvery { getPlannerItemsUseCase() } returns flowOf(listOf(undatedItem))
            val viewModel = createViewModel()

            viewModel.uiState.test {
                val state = awaitItem()
                assertTrue("Expected MainUiState.Success, but was $state", state is MainUiState.Success)
                if (state is MainUiState.Success) {
                    assertEquals(
                        listOf(0, 1).map { DaySection(dateAt(daysFromToday = it, hour = 0), emptyList()) },
                        state.calendarSections,
                    )
                }
            }
        }

    @Test
    fun `selectDate adds an empty section for a day without items`() =
        runTest {
            coEvery { getPlannerItemsUseCase() } returns flowOf(listOf(morningItem))
            val viewModel = createViewModel()

            viewModel.selectDate(dateAt(daysFromToday = 3, hour = 15))

            viewModel.uiState.test {
                val state = awaitItem()
                assertTrue("Expected MainUiState.Success, but was $state", state is MainUiState.Success)
                if (state is MainUiState.Success) {
                    assertEquals(
                        listOf(0, 1, 3).map { dateAt(daysFromToday = it, hour = 0) },
                        state.calendarSections.map { it.date },
                    )
                    assertEquals(emptyList<PlannerItem>(), state.calendarSections.last().items)
                }
            }
        }

    @Test
    fun `selectDate on a day with items doesn't add a section`() =
        runTest {
            coEvery { getPlannerItemsUseCase() } returns flowOf(listOf(nextDayItem, morningItem))
            val viewModel = createViewModel()

            viewModel.selectDate(dateAt(daysFromToday = 2, hour = 15))

            viewModel.uiState.test {
                val state = awaitItem()
                assertTrue("Expected MainUiState.Success, but was $state", state is MainUiState.Success)
                if (state is MainUiState.Success) {
                    assertEquals(
                        listOf(0, 1, 2).map { dateAt(daysFromToday = it, hour = 0) },
                        state.calendarSections.map { it.date },
                    )
                }
            }
        }

    @Test
    fun `calendarSections hide past days unless the past day is selected`() =
        runTest {
            coEvery { getPlannerItemsUseCase() } returns flowOf(listOf(yesterdayItem, morningItem))
            val viewModel = createViewModel()
            viewModel.selectDate(dateAt(daysFromToday = 1, hour = 15))

            viewModel.uiState.test {
                val state = awaitItem()
                assertTrue("Expected MainUiState.Success, but was $state", state is MainUiState.Success)
                if (state is MainUiState.Success) {
                    assertEquals(listOf(morningItem), state.calendarSections.flatMap { it.items })
                }

                viewModel.selectDate(dateAt(daysFromToday = -1, hour = 15))
                val pastState = awaitItem()
                assertTrue("Expected MainUiState.Success, but was $pastState", pastState is MainUiState.Success)
                if (pastState is MainUiState.Success) {
                    assertEquals(listOf(yesterdayItem, morningItem), pastState.calendarSections.flatMap { it.items })
                }
            }
        }

    @Test
    fun `uiState emits Error when getPlannerItemsUseCase throws exception`() =
        runTest {
            coEvery { getPlannerItemsUseCase() } returns
                flow { throw IllegalStateException("Database Connection Failed") }
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
    fun `addCategory invokes addCategoryUseCase when name is valid`() =
        runTest {
            val viewModel = createViewModel()

            viewModel.addCategory("Shopping", "🛍️", "#3F51B5")

            coVerify { addCategoryUseCase(any()) }
        }

    @Test
    fun `addCategory does not invoke addCategoryUseCase when name is blank`() =
        runTest {
            val viewModel = createViewModel()

            viewModel.addCategory("   ", "🛍️", "#3F51B5")

            coVerify(exactly = 0) { addCategoryUseCase(any()) }
        }

    @Test
    fun `addItem invokes addPlannerItemUseCase when title is valid`() =
        runTest {
            val viewModel = createViewModel()

            viewModel.addItem("New Task", "New Desc", Priority.MEDIUM, "groceries")

            coVerify { addPlannerItemUseCase(any()) }
        }

    @Test
    fun `addItem does not invoke addPlannerItemUseCase when title is blank`() =
        runTest {
            val viewModel = createViewModel()

            viewModel.addItem("  ", "New Desc", Priority.MEDIUM, "groceries")

            coVerify(exactly = 0) { addPlannerItemUseCase(any()) }
        }

    @Test
    fun `updateItem invokes updatePlannerItemUseCase`() =
        runTest {
            val viewModel = createViewModel()

            viewModel.updateItem(testItem1)

            coVerify { updatePlannerItemUseCase(testItem1) }
        }

    @Test
    fun `deleteItem invokes deletePlannerItemUseCase`() =
        runTest {
            val viewModel = createViewModel()

            viewModel.deleteItem("1")

            coVerify { deletePlannerItemUseCase("1") }
        }
}

private const val HALF_HOUR_MILLIS = 30 * 60 * 1000L
