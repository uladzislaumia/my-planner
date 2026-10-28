package com.uladzislaumia.myplanner.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uladzislaumia.myplanner.domain.model.Category
import com.uladzislaumia.myplanner.domain.model.PlannerItem
import com.uladzislaumia.myplanner.domain.model.Priority
import com.uladzislaumia.myplanner.domain.usecase.AddCategoryUseCase
import com.uladzislaumia.myplanner.domain.usecase.AddPlannerItemUseCase
import com.uladzislaumia.myplanner.domain.usecase.DeletePlannerItemUseCase
import com.uladzislaumia.myplanner.domain.usecase.GetCategoriesUseCase
import com.uladzislaumia.myplanner.domain.usecase.GetPlannerItemsUseCase
import com.uladzislaumia.myplanner.domain.usecase.UpdatePlannerItemUseCase
import com.uladzislaumia.myplanner.ui.util.plusDays
import com.uladzislaumia.myplanner.ui.util.startOfDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject

sealed class MainUiState {
    object Loading : MainUiState()

    data class Success(
        val items: List<PlannerItem>,
        val calendarSections: List<DaySection> = emptyList(),
    ) : MainUiState()

    data class Error(
        val message: String,
    ) : MainUiState()
}

/** Tasks due on one day ([date] is midnight), sorted by time. */
data class DaySection(
    val date: Date,
    val items: List<PlannerItem>,
)

@HiltViewModel
class MainViewModel
    @Inject
    constructor(
        private val getPlannerItemsUseCase: GetPlannerItemsUseCase,
        private val addPlannerItemUseCase: AddPlannerItemUseCase,
        private val updatePlannerItemUseCase: UpdatePlannerItemUseCase,
        private val deletePlannerItemUseCase: DeletePlannerItemUseCase,
        private val getCategoriesUseCase: GetCategoriesUseCase,
        private val addCategoryUseCase: AddCategoryUseCase,
    ) : ViewModel() {
        val categories: StateFlow<List<Category>> =
            getCategoriesUseCase()
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        /** Selected day at midnight, today by default; a day is always selected. */
        val selectedDate = MutableStateFlow(Date().startOfDay())

        val uiState: StateFlow<MainUiState> =
            combine(
                getPlannerItemsUseCase(),
                selectedDate,
            ) { items, date ->
                MainUiState.Success(items, calendarSections(items, date)) as MainUiState
            }.catch { e ->
                emit(MainUiState.Error(e.message ?: "Unknown Error"))
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MainUiState.Loading)

        fun selectDate(date: Date) {
            selectedDate.value = date.startOfDay()
        }

        /**
         * Tasks with a deadline grouped by day, from today on, days in order; tasks without a deadline are left out.
         * A past day shows up only while it's selected. Today, tomorrow and the selected day are always there,
         * with an empty section when they have no tasks, so the list can scroll to them.
         */
        private fun calendarSections(
            items: List<PlannerItem>,
            selectedDate: Date,
        ): List<DaySection> {
            val today = Date().startOfDay()
            val sections =
                items
                    .mapNotNull { item -> item.dueDate?.let { it to item } }
                    .sortedBy { (dueDate, _) -> dueDate }
                    .groupBy({ (dueDate, _) -> dueDate.startOfDay() }, { (_, item) -> item })
                    .map { (day, dayItems) -> DaySection(day, dayItems) }
                    .filter { it.date >= today || it.date == selectedDate }
            val missingDays =
                setOf(today, today.plusDays(1), selectedDate)
                    .filter { day -> sections.none { it.date == day } }
                    .map { day -> DaySection(day, emptyList()) }
            return (sections + missingDays).sortedBy { it.date }
        }

        fun addCategory(
            name: String,
            icon: String,
            colorHex: String,
        ) {
            if (name.isBlank()) return
            viewModelScope.launch {
                val category =
                    Category(
                        id = UUID.randomUUID().toString(),
                        name = name,
                        icon = icon,
                        colorHex = colorHex,
                    )
                addCategoryUseCase(category)
            }
        }

        fun addItem(
            title: String,
            description: String,
            priority: Priority,
            categoryId: String? = null,
            dueDate: Date? = null,
        ) {
            if (title.isBlank()) return
            viewModelScope.launch {
                val newItem =
                    PlannerItem(
                        id = UUID.randomUUID().toString(),
                        title = title,
                        description = description,
                        categoryId = categoryId,
                        groupId = null,
                        assigneeId = null,
                        dueDate = dueDate,
                        priority = priority,
                        isCompleted = false,
                    )
                addPlannerItemUseCase(newItem)
            }
        }

        fun updateItem(item: PlannerItem) {
            viewModelScope.launch {
                updatePlannerItemUseCase(item)
            }
        }

        fun deleteItem(id: String) {
            viewModelScope.launch {
                deletePlannerItemUseCase(id)
            }
        }
    }
