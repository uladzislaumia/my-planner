package com.uladzislaumia.myplanner.data.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.uladzislaumia.myplanner.domain.model.AuthResult
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import com.google.firebase.auth.AuthResult as FirebaseAuthResult

class FirebaseAuthRepositoryImplTest {
    private val firebaseAuth: FirebaseAuth = mockk()
    private val firebaseAnalytics: FirebaseAnalytics = mockk(relaxed = true)
    private val repository = FirebaseAuthRepositoryImpl(firebaseAuth, firebaseAnalytics)

    private fun completedTask(
        exception: Exception? = null,
        canceled: Boolean = false,
    ): Task<FirebaseAuthResult> =
        mockk {
            every { isComplete } returns true
            every { isCanceled } returns canceled
            every { this@mockk.exception } returns exception
        }

    @Test
    fun `login returns Error with the Firebase exception when sign in fails`() =
        runTest {
            val networkError = mockk<FirebaseNetworkException>(relaxed = true)
            every {
                firebaseAuth.signInWithEmailAndPassword("john@example.com", "password")
            } returns completedTask(exception = networkError)

            val result = repository.login("john@example.com", "password")

            assertEquals(AuthResult.Error(networkError), result)
        }

    @Test
    fun `login returns Error when sign in fails with an unexpected exception`() =
        runTest {
            val unexpected = IllegalStateException("Unexpected")
            every {
                firebaseAuth.signInWithEmailAndPassword("john@example.com", "password")
            } returns completedTask(exception = unexpected)

            val result = repository.login("john@example.com", "password")

            assertEquals(AuthResult.Error(unexpected), result)
        }

    @Test
    fun `login propagates cancellation instead of reporting it as a failed sign in`() =
        runTest {
            every {
                firebaseAuth.signInWithEmailAndPassword("john@example.com", "password")
            } returns completedTask(canceled = true)

            try {
                repository.login("john@example.com", "password")
                fail("Expected the cancellation to propagate")
            } catch (_: CancellationException) {
                // Expected
            }
        }
}
