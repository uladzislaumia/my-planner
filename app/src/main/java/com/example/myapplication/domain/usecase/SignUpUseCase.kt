package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.AuthResult
import com.example.myapplication.domain.model.User
import com.example.myapplication.domain.repository.AuthRepository

class SignUpUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(name: String, email: String, password: String): AuthResult<User> {
        return repository.signUp(name, email, password)
    }
}
