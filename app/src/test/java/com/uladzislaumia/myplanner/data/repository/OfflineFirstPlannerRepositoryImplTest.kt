package com.uladzislaumia.myplanner.data.repository

import com.google.firebase.firestore.FirebaseFirestoreException
import com.uladzislaumia.myplanner.data.local.dao.PlannerItemDao
import com.uladzislaumia.myplanner.data.local.dao.PlannerSyncDao
import com.uladzislaumia.myplanner.data.remote.PlannerRemoteDataSource
import com.uladzislaumia.myplanner.data.sync.SyncScheduler
import com.uladzislaumia.myplanner.notification.NotificationScheduler
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class OfflineFirstPlannerRepositoryImplTest {
    private val plannerItemDao: PlannerItemDao = mockk(relaxed = true)
    private val plannerSyncDao: PlannerSyncDao = mockk(relaxed = true)
    private val remoteDataSource: PlannerRemoteDataSource = mockk()
    private val syncScheduler: SyncScheduler = mockk(relaxed = true)
    private val notificationScheduler: NotificationScheduler = mockk(relaxed = true)

    private val repository =
        OfflineFirstPlannerRepositoryImpl(
            plannerItemDao = plannerItemDao,
            plannerSyncDao = plannerSyncDao,
            remoteDataSource = remoteDataSource,
            syncScheduler = syncScheduler,
            notificationScheduler = notificationScheduler,
        )

    @Before
    fun setup() {
        every { remoteDataSource.currentUserId } returns "user-1"
        every { plannerItemDao.getAllItems() } returns flowOf(emptyList())
        coEvery { plannerItemDao.getItemCount() } returns 0
    }

    @Test
    fun `getItems seeds demo items when the cloud fails and the local database is empty`() =
        runTest {
            coEvery { remoteDataSource.fetchItems("user-1") } throws mockk<FirebaseFirestoreException>(relaxed = true)

            assertEquals(emptyList<Any>(), repository.getItems().first())

            coVerify(atLeast = 1) { plannerItemDao.insertItem(any()) }
        }

    @Test
    fun `getItems keeps working when the sync fails with an unexpected exception`() =
        runTest {
            coEvery { remoteDataSource.fetchItems("user-1") } throws IllegalStateException("Unexpected")

            assertEquals(emptyList<Any>(), repository.getItems().first())
        }

    @Test
    fun `getItems doesn't take a cancelled sync for a cloud failure and seed demo items`() =
        runTest {
            coEvery { remoteDataSource.fetchItems("user-1") } throws CancellationException("Collector left")

            try {
                repository.getItems().first()
                fail("Expected the cancellation to propagate")
            } catch (_: CancellationException) {
                // Expected
            }

            coVerify(exactly = 0) { plannerItemDao.insertItem(any()) }
        }
}
