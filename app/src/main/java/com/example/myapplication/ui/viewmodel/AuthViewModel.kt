package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.FirebaseAuthRepositoryImpl
import com.example.myapplication.domain.model.AuthResult
import com.example.myapplication.domain.model.User
import com.example.myapplication.domain.usecase.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: User) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(
    private val loginUseCase: LoginUseCase = LoginUseCase(FirebaseAuthRepositoryImpl()),
    private val signUpUseCase: SignUpUseCase = SignUpUseCase(FirebaseAuthRepositoryImpl()),
    private val logoutUseCase: LogoutUseCase = LogoutUseCase(FirebaseAuthRepositoryImpl()),
    private val getCurrentUserUseCase: GetCurrentUserUseCase = GetCurrentUserUseCase(FirebaseAuthRepositoryImpl()),
    private val observeAuthStateUseCase: ObserveAuthStateUseCase = ObserveAuthStateUseCase(FirebaseAuthRepositoryImpl())
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val authState = observeAuthStateUseCase()

    val currentUser: User?
        get() = getCurrentUserUseCase()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Email and password cannot be empty")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            when (val result = loginUseCase(email, password)) {
                is AuthResult.Success -> _uiState.value = AuthUiState.Success(result.data)
                is AuthResult.Error -> _uiState.value = AuthUiState.Error(result.exception.localizedMessage ?: "Login failed")
            }
        }
    }

    fun signUp(name: String, email: String, password: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("All fields are required")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            when (val result = signUpUseCase(name, email, password)) {
                is AuthResult.Success -> _uiState.value = AuthUiState.Success(result.data)
                is AuthResult.Error -> _uiState.value = AuthUiState.Error(result.exception.localizedMessage ?: "Registration failed")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _uiState.value = AuthUiState.Idle
        }
    }

    fun clearError() {
        if (_uiState.value is AuthUiState.Error) {
            _uiState.value = AuthUiState.Idle
        }
    }
}
