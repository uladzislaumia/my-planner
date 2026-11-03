package com.uladzislaumia.myplanner.util

import kotlinx.coroutines.CancellationException

/**
 * Like [runCatching], but lets cancellation through instead of reporting it as a failure,
 * so a cancelled coroutine stops rather than running the failure path.
 * Catches [Exception] only: errors such as [OutOfMemoryError] still reach Crashlytics as crashes.
 */
inline fun <T> runCatchingCancellable(block: () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
