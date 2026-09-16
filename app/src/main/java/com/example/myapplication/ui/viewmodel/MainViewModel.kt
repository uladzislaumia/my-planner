package com.example.myapplication.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.database.AppDatabase
import com.example.myapplication.data.repository.RoomPlannerRepositoryImpl
import com.example.myapplication.domain.model.PlannerItem
import com.example.myapplication.domain.usecase.GetPlannerItemsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class MainUiState {
    object Loading : MainUiState()
    data class Success(val items: List<PlannerItem>) : MainUiState()
    data class Error(val message: String) : MainUiState()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = RoomPlannerRepositoryImpl(database.plannerItemDao())
    private val getPlannerItemsUseCase: GetPlannerItemsUseCase = GetPlannerItemsUseCase(repository)

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
}
