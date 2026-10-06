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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

sealed class MainUiState {
    object Loading : MainUiState()
    data class Success(val items: List<PlannerItem>) : MainUiState()
    data class Error(val message: String) : MainUiState()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getPlannerItemsUseCase: GetPlannerItemsUseCase,
    private val addPlannerItemUseCase: AddPlannerItemUseCase,
    private val updatePlannerItemUseCase: UpdatePlannerItemUseCase,
    private val deletePlannerItemUseCase: DeletePlannerItemUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val addCategoryUseCase: AddCategoryUseCase,
) : ViewModel() {

    val categories: StateFlow<List<Category>> = getCategoriesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedCategoryId = MutableStateFlow<String?>(null)

    val uiState: StateFlow<MainUiState> = combine(
        getPlannerItemsUseCase(),
        selectedCategoryId
    ) { items, selectedCatId ->
        val filtered = if (selectedCatId == null) {
            items
        } else {
            items.filter { it.categoryId == selectedCatId }
        }
        MainUiState.Success(filtered) as MainUiState
    }
        .catch { e ->
            emit(MainUiState.Error(e.message ?: "Unknown Error"))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MainUiState.Loading)

    fun selectCategory(categoryId: String?) {
        selectedCategoryId.value = categoryId
    }

    fun addCategory(name: String, icon: String, colorHex: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val category = Category(
                id = UUID.randomUUID().toString(),
                name = name,
                icon = icon,
                colorHex = colorHex
            )
            addCategoryUseCase(category)
        }
    }

    fun addItem(
        title: String,
        description: String,
        priority: Priority,
        categoryId: String? = null,
    ) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val newItem = PlannerItem(
                id = UUID.randomUUID().toString(),
                title = title,
                description = description,
                categoryId = categoryId,
                groupId = null,
                assigneeId = null,
                dueDate = null,
                priority = priority,
                isCompleted = false
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
