package com.uladzislaumia.myplanner.domain.usecase

import com.uladzislaumia.myplanner.domain.model.User
import com.uladzislaumia.myplanner.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAuthStateUseCase
    @Inject
    constructor(
        private val repository: AuthRepository,
    ) {
        operator fun invoke(): Flow<User?> = repository.observeAuthState()
    }
