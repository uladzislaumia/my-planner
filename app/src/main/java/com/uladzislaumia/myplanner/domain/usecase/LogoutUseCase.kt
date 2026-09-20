package com.uladzislaumia.myplanner.domain.usecase

import com.uladzislaumia.myplanner.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke() {
        repository.logout()
    }
}
