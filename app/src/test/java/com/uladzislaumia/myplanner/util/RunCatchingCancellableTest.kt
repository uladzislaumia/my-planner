package com.uladzislaumia.myplanner.util

import kotlinx.coroutines.CancellationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.fail
import org.junit.Test

class RunCatchingCancellableTest {
    @Test
    fun `returns success with the value`() {
        assertEquals(Result.success(42), runCatchingCancellable { 42 })
    }

    @Test
    fun `returns failure with the exception`() {
        val error = IllegalStateException("Failed")

        val result = runCatchingCancellable { throw error }

        assertSame(error, result.exceptionOrNull())
    }

    @Test
    fun `rethrows cancellation instead of returning a failure`() {
        try {
            runCatchingCancellable { throw CancellationException("Cancelled") }
            fail("Expected the cancellation to propagate")
        } catch (_: CancellationException) {
            // Expected
        }
    }
}
