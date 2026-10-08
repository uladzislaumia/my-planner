package com.uladzislaumia.myplanner.domain.usecase

import app.cash.turbine.test
import com.uladzislaumia.myplanner.domain.model.User
import com.uladzislaumia.myplanner.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ObserveAuthStateUseCaseTest {

    private val authRepository: AuthRepository = mockk()
    private val observeAuthStateUseCase = ObserveAuthStateUseCase(authRepository)

    @Test
    fun `invoke returns flow of user auth state from repository`() = runTest {
        val testUser = User(id = "user-1", name = "John", email = "john@example.com", avatarUrl = null)
        coEvery { authRepository.observeAuthState() } returns flowOf(testUser)

        observeAuthStateUseCase().test {
            val user = awaitItem()
            assertEquals(testUser, user)
            awaitComplete()
        }
    }
}
