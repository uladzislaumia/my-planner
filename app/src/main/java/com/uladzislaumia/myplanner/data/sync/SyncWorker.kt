package com.uladzislaumia.myplanner.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.uladzislaumia.myplanner.data.local.dao.PlannerSyncDao
import com.uladzislaumia.myplanner.data.local.entity.SyncState
import com.uladzislaumia.myplanner.data.remote.PlannerRemoteDataSource
import com.uladzislaumia.myplanner.util.runCatchingCancellable
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
import kotlin.time.Duration.Companion.seconds

/** Pushes local rows whose [SyncState] is not [SyncState.SYNCED] to Firestore. */
@HiltWorker
class SyncWorker
    @AssistedInject
    constructor(
        @Assisted context: Context,
        @Assisted params: WorkerParameters,
        private val plannerSyncDao: PlannerSyncDao,
        private val remoteDataSource: PlannerRemoteDataSource,
    ) : CoroutineWorker(context, params) {
        // Whatever went wrong, pending changes must stay queued for a retry
        override suspend fun doWork(): Result {
            // Captured once: the whole run writes to this user's collection only
            val uid =
                remoteDataSource.currentUserId ?: run {
                    Timber.d("SyncWorker: user signed out, skipping")
                    return Result.success()
                }

            return runCatchingCancellable { uploadPendingChanges(uid) }
                .getOrElse { e ->
                    Timber.e(e, "SyncWorker: sync failed, attempt $runAttemptCount")
                    Result.retry()
                }
        }

        private suspend fun uploadPendingChanges(uid: String): Result {
            val pending = plannerSyncDao.getPendingSyncItems()
            if (pending.isEmpty()) return Result.success()

            Timber.d("SyncWorker: uploading ${pending.size} item(s)")
            // Firestore's await() may hang while the connection is flaky; let WorkManager retry instead
            withTimeoutOrNull(UPLOAD_TIMEOUT) {
                remoteDataSource.pushChanges(uid, pending)
            } ?: run {
                Timber.w("SyncWorker: upload timed out, attempt $runAttemptCount")
                return Result.retry()
            }
            pending.forEach { item ->
                when (item.syncState) {
                    SyncState.PENDING_DELETE -> plannerSyncDao.deleteSyncedTombstone(item.id, item.localUpdatedAt)
                    SyncState.PENDING_UPSERT -> plannerSyncDao.markSynced(item.id, item.localUpdatedAt)
                    SyncState.SYNCED -> Unit
                }
            }
            Timber.d("SyncWorker: sync completed")
            return Result.success()
        }

        companion object {
            const val WORK_NAME = "planner_sync"
            private val UPLOAD_TIMEOUT = 30.seconds
        }
    }
