package com.uladzislaumia.myplanner.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uladzislaumia.myplanner.domain.model.AuthResult
import com.uladzislaumia.myplanner.domain.model.User
import com.uladzislaumia.myplanner.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: User) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val observeAuthStateUseCase: ObserveAuthStateUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val authState = observeAuthStateUseCase()

    val currentUser: User?
        get() = getCurrentUserUseCase()

    fun login(email: String, password: String) {
        Timber.d("Login attempt for email: $email")
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Email and password cannot be empty")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            when (val result = loginUseCase(email, password)) {
                is AuthResult.Success -> {
                    Timber.d("Login successful for user: ${result.data.id}")
                    _uiState.value = AuthUiState.Success(result.data)
                }
                is AuthResult.Error -> {
                    Timber.e(result.exception, "Login failed")
                    _uiState.value = AuthUiState.Error(result.exception.localizedMessage ?: "Login failed")
                }
            }
        }
    }

    fun signUp(name: String, email: String, password: String) {
        Timber.d("SignUp attempt for email: $email")
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("All fields are required")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            when (val result = signUpUseCase(name, email, password)) {
                is AuthResult.Success -> {
                    Timber.d("SignUp successful for user: ${result.data.id}")
                    _uiState.value = AuthUiState.Success(result.data)
                }
                is AuthResult.Error -> {
                    Timber.e(result.exception, "SignUp failed")
                    _uiState.value = AuthUiState.Error(result.exception.localizedMessage ?: "Registration failed")
                }
            }
        }
    }

    fun logout() {
        Timber.d("Logging out user: ${currentUser?.id}")
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
