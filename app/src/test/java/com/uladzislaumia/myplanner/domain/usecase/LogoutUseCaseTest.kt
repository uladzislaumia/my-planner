package com.uladzislaumia.myplanner.domain.usecase

import com.uladzislaumia.myplanner.domain.repository.AuthRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class LogoutUseCaseTest {

    private val authRepository: AuthRepository = mockk(relaxed = true)
    private val logoutUseCase = LogoutUseCase(authRepository)

    @Test
    fun `invoke calls logout on repository`() = runTest {
        logoutUseCase()

        coVerify { authRepository.logout() }
    }
}
