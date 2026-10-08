package com.uladzislaumia.myplanner.ui.viewmodel

import app.cash.turbine.test
import com.uladzislaumia.myplanner.MainDispatcherRule
import com.uladzislaumia.myplanner.domain.model.AuthResult
import com.uladzislaumia.myplanner.domain.model.User
import com.uladzislaumia.myplanner.domain.usecase.GetCurrentUserUseCase
import com.uladzislaumia.myplanner.domain.usecase.LoginUseCase
import com.uladzislaumia.myplanner.domain.usecase.LogoutUseCase
import com.uladzislaumia.myplanner.domain.usecase.ObserveAuthStateUseCase
import com.uladzislaumia.myplanner.domain.usecase.SignUpUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AuthViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val loginUseCase: LoginUseCase = mockk()
    private val signUpUseCase: SignUpUseCase = mockk()
    private val logoutUseCase: LogoutUseCase = mockk(relaxed = true)
    private val getCurrentUserUseCase: GetCurrentUserUseCase = mockk()
    private val observeAuthStateUseCase: ObserveAuthStateUseCase = mockk()

    private val testUser = User(
        id = "user-1",
        name = "John",
        email = "john@example.com",
        avatarUrl = null,
    )

    @Before
    fun setup() {
        every { getCurrentUserUseCase() } returns testUser
        every { observeAuthStateUseCase() } returns flowOf(testUser)
    }

    private fun createViewModel() = AuthViewModel(
        loginUseCase = loginUseCase,
        signUpUseCase = signUpUseCase,
        logoutUseCase = logoutUseCase,
        getCurrentUserUseCase = getCurrentUserUseCase,
        observeAuthStateUseCase = observeAuthStateUseCase,
    )

    @Test
    fun `initial uiState is Idle`() = runTest {
        val viewModel = createViewModel()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue("Expected AuthUiState.Idle, but was $state", state is AuthUiState.Idle)
        }
    }

    @Test
    fun `login with empty fields sets Error state`() = runTest {
        val viewModel = createViewModel()

        viewModel.login("", "password123")

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue("Expected AuthUiState.Error, but was $state", state is AuthUiState.Error)
            if (state is AuthUiState.Error) {
                assertEquals("Email and password cannot be empty", state.message)
            }
        }
        coVerify(exactly = 0) { loginUseCase(any(), any()) }
    }

    @Test
    fun `login success emits Success state with user`() = runTest {
        coEvery { loginUseCase("john@example.com", "password123") } returns AuthResult.Success(testUser)
        val viewModel = createViewModel()

        viewModel.login("john@example.com", "password123")

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue("Expected AuthUiState.Success, but was $state", state is AuthUiState.Success)
            if (state is AuthUiState.Success) {
                assertEquals(testUser, state.user)
            }
        }
    }

    @Test
    fun `login failure emits Error state`() = runTest {
        val exception = Exception("Invalid credentials")
        coEvery { loginUseCase("john@example.com", "wrong") } returns AuthResult.Error(exception)
        val viewModel = createViewModel()

        viewModel.login("john@example.com", "wrong")

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue("Expected AuthUiState.Error, but was $state", state is AuthUiState.Error)
            if (state is AuthUiState.Error) {
                assertEquals("Invalid credentials", state.message)
            }
        }
    }

    @Test
    fun `signUp with blank fields sets Error state`() = runTest {
        val viewModel = createViewModel()

        viewModel.signUp("", "john@example.com", "password123")

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue("Expected AuthUiState.Error, but was $state", state is AuthUiState.Error)
            if (state is AuthUiState.Error) {
                assertEquals("All fields are required", state.message)
            }
        }
        coVerify(exactly = 0) { signUpUseCase(any(), any(), any()) }
    }

    @Test
    fun `signUp success emits Success state with user`() = runTest {
        coEvery { signUpUseCase("John", "john@example.com", "password123") } returns AuthResult.Success(testUser)
        val viewModel = createViewModel()

        viewModel.signUp("John", "john@example.com", "password123")

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue("Expected AuthUiState.Success, but was $state", state is AuthUiState.Success)
            if (state is AuthUiState.Success) {
                assertEquals(testUser, state.user)
            }
        }
    }

    @Test
    fun `logout invokes logoutUseCase and resets uiState to Idle`() = runTest {
        val viewModel = createViewModel()

        viewModel.logout()

        coVerify { logoutUseCase() }
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue("Expected AuthUiState.Idle, but was $state", state is AuthUiState.Idle)
        }
    }

    @Test
    fun `clearError resets Error state to Idle`() = runTest {
        val viewModel = createViewModel()

        viewModel.login("", "") // Triggers Error
        viewModel.clearError()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue("Expected AuthUiState.Idle, but was $state", state is AuthUiState.Idle)
        }
    }

    @Test
    fun `currentUser returns user from getCurrentUserUseCase`() {
        val viewModel = createViewModel()

        assertEquals(testUser, viewModel.currentUser)
    }

    @Test
    fun `authState observes user state from observeAuthStateUseCase`() = runTest {
        val viewModel = createViewModel()

        viewModel.authState.test {
            val user = awaitItem()
            assertEquals(testUser, user)
            awaitComplete()
        }
    }
}
