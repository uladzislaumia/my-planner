package com.uladzislaumia.myplanner.domain.usecase

import com.uladzislaumia.myplanner.domain.model.AuthResult
import com.uladzislaumia.myplanner.domain.model.User
import com.uladzislaumia.myplanner.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class LoginUseCaseTest {

    private val authRepository: AuthRepository = mockk()
    private val loginUseCase = LoginUseCase(authRepository)

    @Test
    fun `invoke calls login on repository and returns AuthResult`() = runTest {
        val testUser = User(id = "user-1", name = "John", email = "john@example.com", avatarUrl = null)
        val expectedResult = AuthResult.Success(testUser)

        coEvery { authRepository.login("john@example.com", "password123") } returns expectedResult

        val result = loginUseCase("john@example.com", "password123")

        assertEquals(expectedResult, result)
    }
}
