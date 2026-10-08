package com.uladzislaumia.myplanner.domain.usecase

import com.uladzislaumia.myplanner.domain.model.AuthResult
import com.uladzislaumia.myplanner.domain.model.User
import com.uladzislaumia.myplanner.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SignUpUseCaseTest {

    private val authRepository: AuthRepository = mockk()
    private val signUpUseCase = SignUpUseCase(authRepository)

    @Test
    fun `invoke calls signUp on repository and returns AuthResult`() = runTest {
        val testUser = User(id = "user-1", name = "John", email = "john@example.com", avatarUrl = null)
        val expectedResult = AuthResult.Success(testUser)

        coEvery { authRepository.signUp("John", "john@example.com", "password123") } returns expectedResult

        val result = signUpUseCase("John", "john@example.com", "password123")

        assertEquals(expectedResult, result)
    }
}
