package com.uladzislaumia.myplanner.data.remote

import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.uladzislaumia.myplanner.domain.model.Priority
import com.uladzislaumia.myplanner.domain.repository.AuthRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Date

class PlannerRemoteDataSourceTest {
    private val firestore: FirebaseFirestore = mockk()
    private val dataSource = PlannerRemoteDataSource(firestore, mockk<AuthRepository>())

    private fun document(
        id: String,
        fields: Map<String, Any?>,
    ): DocumentSnapshot =
        mockk {
            every { this@mockk.id } returns id
            every { this@mockk.get(any<String>()) } answers { fields[firstArg<String>()] }
        }

    private fun givenDocuments(vararg documents: DocumentSnapshot) {
        val snapshot: QuerySnapshot = mockk { every { this@mockk.documents } returns documents.toList() }
        val task: Task<QuerySnapshot> =
            mockk {
                every { isComplete } returns true
                every { isCanceled } returns false
                every { exception } returns null
                every { result } returns snapshot
            }
        every {
            firestore
                .collection("users")
                .document("user-1")
                .collection("items")
                .get()
        } returns task
    }

    @Test
    fun `fetchItems maps document fields`() =
        runTest {
            val dueDate = Date(1_000_000L)
            givenDocuments(
                document(
                    id = "1",
                    fields =
                        mapOf(
                            "title" to "Title",
                            "description" to "Description",
                            "categoryId" to "work",
                            "dueDate" to Timestamp(dueDate),
                            "isCompleted" to true,
                            "priority" to "HIGH",
                        ),
                ),
            )

            val item = dataSource.fetchItems("user-1").single()

            assertEquals("Title", item.title)
            assertEquals("Description", item.description)
            assertEquals("work", item.categoryId)
            assertEquals(dueDate, item.dueDate)
            assertEquals(true, item.isCompleted)
            assertEquals(Priority.HIGH, item.priority)
        }

    @Test
    fun `fetchItems falls back to defaults for fields of another type`() =
        runTest {
            givenDocuments(
                document(
                    id = "1",
                    fields =
                        mapOf(
                            "title" to 42,
                            "dueDate" to "tomorrow",
                            "isCompleted" to "yes",
                            "priority" to 1,
                        ),
                ),
            )

            val item = dataSource.fetchItems("user-1").single()

            assertEquals("", item.title)
            assertNull(item.dueDate)
            assertFalse(item.isCompleted)
            assertEquals(Priority.MEDIUM, item.priority)
        }

    @Test
    fun `fetchItems skips a document with an unknown priority`() =
        runTest {
            givenDocuments(
                document(id = "bad", fields = mapOf("priority" to "URGENT")),
                document(id = "good", fields = mapOf("priority" to "LOW")),
            )

            val items = dataSource.fetchItems("user-1")

            assertEquals(listOf("good"), items.map { it.id })
        }
}
