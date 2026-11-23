package com.knoux.ai

import com.knoux.ai.database.ObservedItemEntity
import com.knoux.ai.database.OperationItemOutcomeDao
import com.knoux.ai.database.OperationItemOutcomeEntity
import com.knoux.ai.database.OperationOutcomeSummary
import com.knoux.ai.database.ScanSessionDao
import com.knoux.ai.database.ScanSessionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Executes the production @Transaction DAO default methods on the JVM against
 * FK-checking fakes. Verifies atomic journaling contracts: evidence rows and
 * the status they justify are written together, chunking preserves order, and
 * a missing parent aborts before any write. Real SQLite atomicity is covered
 * by Room's generated transaction wrapper (device/CI), not by these fakes.
 */
class JournalAtomicityJvmTest {

    // ------------------------------------------------ scan completion

    @Test
    fun completeWithItemsWritesEvidenceBeforeSummaryAndStampsSessionId() = runBlocking {
        val dao = ItemCheckingScanDao()
        dao.sessions["s1"] = session("s1", status = "SCANNING")
        val items = (1..3).map {
            ObservedItemEntity(scanSessionId = "WRONG", providerId = "p$it", sizeBytes = it.toLong())
        }
        val final = session("s1", status = "COMPLETED").copy(itemsFound = 3, bytesFound = 6L)
        dao.completeWithItems(final, items)

        assertEquals(listOf("items:3", "summary"), dao.events)
        assertEquals(listOf("p1", "p2", "p3"), dao.items.map { it.providerId })
        assertTrue(dao.items.all { it.scanSessionId == "s1" })
        assertEquals("COMPLETED", dao.sessions.getValue("s1").status)
    }

    @Test
    fun completeWithItemsChunksLargeScansWithoutLosingRows() = runBlocking {
        val dao = ItemCheckingScanDao()
        dao.sessions["s1"] = session("s1", status = "SCANNING")
        val items = (1..1201).map {
            ObservedItemEntity(scanSessionId = "s1", providerId = "p$it")
        }
        dao.completeWithItems(session("s1", status = "COMPLETED"), items)
        assertEquals(1201, dao.items.size)
        assertEquals(listOf("items:500", "items:500", "items:201", "summary"), dao.events)
        assertEquals(items.map { it.providerId }, dao.items.map { it.providerId })
    }

    @Test
    fun completeWithNoItemsStillFinalizesSummary() = runBlocking {
        val dao = ItemCheckingScanDao()
        dao.sessions["s1"] = session("s1", status = "SCANNING")
        dao.completeWithItems(session("s1", status = "COMPLETED"), emptyList())
        assertEquals(listOf("summary"), dao.events)
        assertTrue(dao.items.isEmpty())
    }

    @Test
    fun itemInsertionFailurePreventsSummaryUpdate() {
        val dao = ItemCheckingScanDao(failItems = true)
        dao.sessions["s1"] = session("s1", status = "SCANNING")
        assertThrows(IllegalStateException::class.java) {
            runBlocking {
                dao.completeWithItems(
                    session("s1", status = "COMPLETED"),
                    listOf(ObservedItemEntity("s1", "p1")),
                )
            }
        }
        assertEquals("SCANNING", dao.sessions.getValue("s1").status)
        assertEquals(listOf("items:1"), dao.events)
    }

    // ------------------------------------------------ outcome journaling

    @Test
    fun outcomesAndFinalStatusCommitTogetherStatusLast() = runBlocking {
        val dao = StatusCheckingOutcomeDao()
        dao.parents["op1"] = "OS_CONFIRMATION"
        val outcomes = listOf(
            outcome("op1", "a", "SUCCESS", 100L),
            outcome("op1", "b", "NO_CHANGE", 0L),
        )
        dao.insertAllWithStatus("op1", outcomes, "PARTIAL")
        assertEquals(listOf("outcomes:2", "status"), dao.events)
        assertEquals("PARTIAL", dao.parents.getValue("op1"))
        assertEquals(2, dao.outcomes.size)
    }

    @Test
    fun missingOperationParentAbortsBeforeAnyOutcomeWrite() {
        val dao = StatusCheckingOutcomeDao()
        assertThrows(IllegalStateException::class.java) {
            runBlocking {
                dao.insertAllWithStatus("ghost", listOf(outcome("ghost", "a", "SUCCESS", 1L)), "VERIFIED")
            }
        }
        assertTrue(dao.outcomes.isEmpty())
        assertTrue(dao.events.isEmpty())
    }

    @Test
    fun outcomeInsertionFailureLeavesParentStatusUntouched() {
        val dao = StatusCheckingOutcomeDao(failOutcomes = true)
        dao.parents["op1"] = "OS_CONFIRMATION"
        assertThrows(IllegalStateException::class.java) {
            runBlocking {
                dao.insertAllWithStatus("op1", listOf(outcome("op1", "a", "SUCCESS", 1L)), "VERIFIED")
            }
        }
        assertEquals("OS_CONFIRMATION", dao.parents.getValue("op1"))
        assertEquals(listOf("outcomes:1"), dao.events)
    }

    @Test
    fun outcomeRowsAreRestampedToTheOperationAndChunked() = runBlocking {
        val dao = StatusCheckingOutcomeDao()
        dao.parents["op1"] = "OS_CONFIRMATION"
        val outcomes = (1..700).map { outcome("WRONG", "i$it", "SUCCESS", 1L) }
        dao.insertAllWithStatus("op1", outcomes, "VERIFIED")
        assertEquals(700, dao.outcomes.size)
        assertTrue(dao.outcomes.all { it.operationId == "op1" })
        assertEquals(listOf("outcomes:500", "outcomes:200", "status"), dao.events)
    }

    // ------------------------------------------------ source contracts

    @Test
    fun productionDaosDeclareRoomTransactionsAndRepositoryRoutesThroughThem() {
        val source = appContractFile("src/main/java/com/knoux/ai/database/AppDatabase.kt").readText()
        assertTrue(Regex("@Transaction\\s+suspend fun completeWithItems\\(").containsMatchIn(source))
        assertTrue(Regex("@Transaction\\s+suspend fun insertAllWithStatus\\(").containsMatchIn(source))
        val repoOutcome = source.substringAfter("suspend fun recordItemOutcomes(")
            .substringBefore("suspend fun recordScopedGrant(")
        assertTrue(repoOutcome.contains("insertAllWithStatus(operationId, outcomes, finalStatus)"))
        val repoComplete = source.substringAfter("items: List<ObservedItemEntity>,\n    ) {")
            .substringBefore("suspend fun createOperationRequest(")
        assertTrue(repoComplete.contains("completeWithItems("))
    }

    @Test
    fun everyFinalStatusCallSiteJournalsEvidenceInTheSameCall() {
        // The five real operation pipelines must not write a final status
        // separately from its outcomes (crash would orphan the claim).
        val files = listOf(
            "src/main/java/com/knoux/ai/ui/v2/StorageAnalyzerViewModel.kt",
            "src/main/java/com/knoux/ai/ui/v2/DuplicateWorkspaceViewModel.kt",
            "src/main/java/com/knoux/ai/ui/v2/CleanDetailScreensV2.kt",
            "src/main/java/com/knoux/ai/ui/v2/MoreScreensV2.kt",
        )
        files.forEach { path ->
            val text = appContractFile(path).readText()
            val outcomeCalls = Regex("recordItemOutcomes\\(").findAll(text).count()
            val withStatus = Regex("finalStatus\\s*=").findAll(text).count()
            assertTrue("$path: every recordItemOutcomes must carry finalStatus", outcomeCalls > 0)
            assertEquals("$path: outcome/status calls must be paired", outcomeCalls, withStatus)
        }
    }

    // ------------------------------------------------ helpers & fakes

    private fun session(id: String, status: String) = ScanSessionEntity(
        id = id, serviceId = "storage_analyzer", serviceName = "Storage Analyzer",
        startedAt = 1L, status = status,
    )

    private fun outcome(op: String, item: String, result: String, bytes: Long) =
        OperationItemOutcomeEntity(
            operationId = op, itemId = item, result = result,
            measuredBytes = bytes, completedAt = 2L,
        )

    private class ItemCheckingScanDao(private val failItems: Boolean = false) : ScanSessionDao {
        val sessions = linkedMapOf<String, ScanSessionEntity>()
        val items = mutableListOf<ObservedItemEntity>()
        val events = mutableListOf<String>()

        override suspend fun observedItemInsertAll(items: List<ObservedItemEntity>) {
            events += "items:${items.size}"
            check(!failItems) { "Injected item insertion failure" }
            check(items.all { it.scanSessionId in sessions }) { "FOREIGN KEY constraint failed (JVM model)" }
            this.items += items
        }

        override suspend fun update(session: ScanSessionEntity) {
            events += "summary"
            check(session.id in sessions) { "Cannot finalize a missing session" }
            sessions[session.id] = session
        }

        override suspend fun insert(session: ScanSessionEntity) { sessions[session.id] = session }
        override suspend fun insertAll(sessions: List<ScanSessionEntity>) { sessions.forEach { this.sessions[it.id] = it } }
        override suspend fun getById(id: String) = sessions[id]
        override fun getByService(serviceId: String): Flow<List<ScanSessionEntity>> = flowOf(sessions.values.toList())
        override fun getRecent(limit: Int): Flow<List<ScanSessionEntity>> = flowOf(sessions.values.take(limit))
        override suspend fun deleteById(id: String) { sessions.remove(id) }
        override suspend fun countByService(serviceId: String) = sessions.values.count { it.serviceId == serviceId }
        override suspend fun countAll() = sessions.size
        override suspend fun countSince(sinceMs: Long) = sessions.values.count { it.startedAt >= sinceMs }
    }

    private class StatusCheckingOutcomeDao(private val failOutcomes: Boolean = false) : OperationItemOutcomeDao {
        val parents = linkedMapOf<String, String>()
        val outcomes = mutableListOf<OperationItemOutcomeEntity>()
        val events = mutableListOf<String>()

        override suspend fun parentStatus(operationId: String): String? = parents[operationId]

        override suspend fun updateParentStatus(id: String, status: String) {
            events += "status"
            check(id in parents) { "Cannot update a missing operation" }
            parents[id] = status
        }

        override suspend fun insertAll(outcomes: List<OperationItemOutcomeEntity>) {
            events += "outcomes:${outcomes.size}"
            check(!failOutcomes) { "Injected outcome insertion failure" }
            check(outcomes.all { it.operationId in parents }) { "FOREIGN KEY constraint failed (JVM model)" }
            this.outcomes += outcomes
        }

        override suspend fun insert(outcome: OperationItemOutcomeEntity) { insertAll(listOf(outcome)) }
        override fun getByOperation(operationId: String): Flow<List<OperationItemOutcomeEntity>> =
            flowOf(outcomes.filter { it.operationId == operationId })
        override fun getByOperationAndResult(operationId: String, result: String): Flow<List<OperationItemOutcomeEntity>> =
            flowOf(outcomes.filter { it.operationId == operationId && it.result == result })
        override fun summaries(): Flow<List<OperationOutcomeSummary>> = flowOf(emptyList())
        override suspend fun totals() = com.knoux.ai.database.OperationTotals(0, 0, 0L)
    }
}
