package com.uladzislaumia.myplanner.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uladzislaumia.myplanner.domain.model.PlannerItem
import com.uladzislaumia.myplanner.domain.model.Priority
import com.uladzislaumia.myplanner.domain.usecase.AddPlannerItemUseCase
import com.uladzislaumia.myplanner.domain.usecase.GetPlannerItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private var fetchJob: Job? = null

    init {
        loadPlannerItems()
    }

    fun loadPlannerItems() {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            _uiState.value = MainUiState.Loading
            try {
                getPlannerItemsUseCase().collect { items ->
                    _uiState.value = MainUiState.Success(items)
                }
            } catch (e: Exception) {
                _uiState.value = MainUiState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    fun addItem(title: String, description: String, priority: Priority) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val newItem = PlannerItem(
                id = UUID.randomUUID().toString(),
                title = title,
                description = description,
                categoryId = null,
                groupId = null,
                assigneeId = null,
                dueDate = null,
                priority = priority,
                isCompleted = false
            )
            addPlannerItemUseCase(newItem)
        }
    }
}
