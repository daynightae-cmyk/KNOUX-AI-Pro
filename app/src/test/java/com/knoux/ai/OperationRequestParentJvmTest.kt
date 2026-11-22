package com.knoux.ai

import com.knoux.ai.database.OperationRequestDao
import com.knoux.ai.database.OperationRequestEntity
import com.knoux.ai.database.ScanSessionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.assertThrows
import org.junit.Test

/** Runs the production DAO default method with a FK-checking fake, not device SQLite. */
class OperationRequestParentJvmTest {
    @Test
    fun emptyLegacyInputsCreateUniqueParentsBeforeRequestsWithoutInventingScanResults() = runBlocking {
        val dao = ParentCheckingDao()
        val services = listOf("app_residue", "app_manager", "duplicate_finder", "app_residue")
        services.forEachIndexed { index, service ->
            val request = request("", "operation-$index").copy(serviceId = service)
            dao.insertWithLegacyParent(request)
            val stored = dao.requests.last()
            val parent = dao.parents.getValue(stored.scanSessionId)
            assertTrue(parent.id.isNotEmpty())
            assertEquals(request.copy(scanSessionId = parent.id), stored)
            assertEquals(service, parent.serviceId)
            assertEquals(service, parent.serviceName)
            assertEquals(request.createdAt, parent.startedAt)
            assertEquals(request.createdAt, parent.endedAt)
            assertEquals("LEGACY_OPERATION", parent.status)
            assertFalse(parent.permissionGranted)
            assertEquals(0, parent.itemsFound)
            assertEquals(0L, parent.bytesFound)
            assertEquals(0L, parent.bytesReclaimable)
            assertFalse(stored.consentGiven)
        }
        assertEquals(services.size, dao.parents.size)
        assertEquals(services.size, dao.requests.map { it.scanSessionId }.toSet().size)
        assertEquals(services.flatMap { listOf("parent", "operation") }, dao.events)
    }

    @Test
    fun suppliedNonemptyIdsIncludingWhitespaceArePreservedAndParentsAreNotReplaced() = runBlocking {
        listOf("existing-scan", " ").forEach { sessionId ->
            val dao = ParentCheckingDao()
            val existing = ScanSessionEntity(
                id = sessionId, serviceId = "original", serviceName = "Original scan",
                startedAt = 1L, status = "COMPLETE", permissionGranted = true,
                itemsFound = 3, bytesFound = 42L,
            )
            dao.parents[sessionId] = existing
            val request = request(sessionId)
            dao.insertWithLegacyParent(request)
            assertEquals(listOf(request), dao.requests)
            assertEquals(mapOf(sessionId to existing), dao.parents)
            assertEquals(listOf("operation"), dao.events)
        }
    }

    @Test
    fun missingNonemptyParentsStillFailTheFkModelRatherThanBeingSilentlyCreated() {
        listOf("missing-scan", " ").forEach { sessionId ->
            val dao = ParentCheckingDao()
            assertThrows(IllegalStateException::class.java) {
                runBlocking { dao.insertWithLegacyParent(request(sessionId)) }
            }
            assertTrue(dao.parents.isEmpty())
            assertTrue(dao.requests.isEmpty())
            assertEquals(listOf("operation"), dao.events)
        }
    }

    @Test
    fun parentInsertionFailurePreventsRequestInsertion() {
        val dao = ParentCheckingDao(failParentInsert = true)
        assertThrows(IllegalStateException::class.java) {
            runBlocking { dao.insertWithLegacyParent(request("")) }
        }
        assertTrue(dao.requests.isEmpty())
        assertEquals(listOf("parent"), dao.events)
    }

    @Test
    fun sourceContractUsesRoomTransactionAndAbortParentInsertionFromRepository() {
        val source = appContractFile("src/main/java/com/knoux/ai/database/AppDatabase.kt").readText()
        assertTrue(Regex("@Transaction\\s+suspend fun insertWithLegacyParent\\(").containsMatchIn(source))
        assertTrue(Regex(
            "@Insert\\(onConflict = OnConflictStrategy.ABORT\\)\\s+suspend fun insertLegacyParent\\(",
        ).containsMatchIn(source))
        val createRequest = source.substringAfter("suspend fun createOperationRequest(")
            .substringBefore("suspend fun recordConsent(")
        assertTrue(createRequest.contains("database.operationRequestDao().insertWithLegacyParent("))
        val operationEntity = source.substringAfter("tableName = \"operation_requests\",")
            .substringBefore("data class OperationRequestEntity(")
        assertTrue(operationEntity.contains("entity = ScanSessionEntity::class"))
        assertTrue(operationEntity.contains("parentColumns = [\"id\"]"))
        assertTrue(operationEntity.contains("childColumns = [\"scanSessionId\"]"))
        assertTrue(operationEntity.contains("onDelete = ForeignKey.CASCADE"))
    }

    private fun request(sessionId: String, id: String = "operation") = OperationRequestEntity(
        id = id, scanSessionId = sessionId, serviceId = "duplicate_finder",
        requestedAction = "delete_duplicates", selectionCount = 2,
        keeperIds = "keeper", exclusionIds = "excluded", predictedEligibleBytes = 123L,
        createdAt = 1234L,
    )

    private class ParentCheckingDao(private val failParentInsert: Boolean = false) : OperationRequestDao {
        val parents = linkedMapOf<String, ScanSessionEntity>()
        val requests = mutableListOf<OperationRequestEntity>()
        val events = mutableListOf<String>()

        override suspend fun insertLegacyParent(session: ScanSessionEntity) {
            events += "parent"
            check(!failParentInsert) { "Injected parent insertion failure" }
            check(session.id !in parents) { "Parent conflict must abort, not replace" }
            parents[session.id] = session
        }

        override suspend fun insert(request: OperationRequestEntity) {
            events += "operation"
            check(request.scanSessionId in parents) { "FOREIGN KEY constraint failed (JVM model)" }
            requests += request
        }

        override suspend fun update(request: OperationRequestEntity): Unit = error("Unexpected update")
        override suspend fun getById(id: String) = requests.firstOrNull { it.id == id }
        override fun getBySession(scanSessionId: String): Flow<List<OperationRequestEntity>> =
            flowOf(requests.filter { it.scanSessionId == scanSessionId })
        override fun getByStatus(status: String): Flow<List<OperationRequestEntity>> =
            flowOf(requests.filter { it.status == status })
        override fun getRecent(limit: Int): Flow<List<OperationRequestEntity>> = flowOf(requests.take(limit))
        override fun getAll(): Flow<List<OperationRequestEntity>> = flowOf(requests.toList())
        override suspend fun updateStatus(id: String, status: String): Unit = error("Unexpected status update")
        override suspend fun countByService(serviceId: String) = requests.count { it.serviceId == serviceId }
        override suspend fun countAll() = requests.size
    }
}
