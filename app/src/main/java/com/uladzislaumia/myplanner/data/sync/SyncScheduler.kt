package com.uladzislaumia.myplanner.data.sync

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    /**
     * Schedules an upload of pending local changes.
     * Each call replaces the previously scheduled one and restarts the delay, so a burst
     * of edits results in a single sync once the user stops changing things.
     */
    fun requestSync(delaySeconds: Long = SYNC_DELAY_SECONDS) {
        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
            .setInitialDelay(delaySeconds, TimeUnit.SECONDS)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, BACKOFF_DELAY_SECONDS, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(SyncWorker.WORK_NAME, ExistingWorkPolicy.REPLACE, request)
    }

    private companion object {
        const val SYNC_DELAY_SECONDS = 5L
        const val BACKOFF_DELAY_SECONDS = 30L
    }
}
