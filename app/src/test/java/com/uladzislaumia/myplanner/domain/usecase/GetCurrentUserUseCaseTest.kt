package com.uladzislaumia.myplanner.domain.usecase

import com.uladzislaumia.myplanner.domain.model.User
import com.uladzislaumia.myplanner.domain.repository.AuthRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class GetCurrentUserUseCaseTest {

    private val authRepository: AuthRepository = mockk()
    private val getCurrentUserUseCase = GetCurrentUserUseCase(authRepository)

    @Test
    fun `invoke returns current user from repository`() {
        val testUser = User(id = "user-1", name = "John", email = "john@example.com", avatarUrl = null)
        every { authRepository.getCurrentUser() } returns testUser

        val user = getCurrentUserUseCase()

        assertEquals(testUser, user)
    }
}
