package com.uladzislaumia.myplanner.domain.usecase

import com.uladzislaumia.myplanner.domain.repository.AuthRepository

class LogoutUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke() {
        repository.logout()
    }
}
