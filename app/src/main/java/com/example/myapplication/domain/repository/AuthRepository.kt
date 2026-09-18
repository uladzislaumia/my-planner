package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.AuthResult
import com.example.myapplication.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getCurrentUser(): User?
    fun observeAuthState(): Flow<User?>
    suspend fun login(email: String, password: String): AuthResult<User>
    suspend fun signUp(name: String, email: String, password: String): AuthResult<User>
    suspend fun logout()
}
