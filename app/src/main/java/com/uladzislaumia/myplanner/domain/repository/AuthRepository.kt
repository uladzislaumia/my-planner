package com.uladzislaumia.myplanner.domain.repository

import com.uladzislaumia.myplanner.domain.model.AuthResult
import com.uladzislaumia.myplanner.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getCurrentUser(): User?
    fun observeAuthState(): Flow<User?>
    suspend fun login(email: String, password: String): AuthResult<User>
    suspend fun signUp(name: String, email: String, password: String): AuthResult<User>
    suspend fun logout()
}
