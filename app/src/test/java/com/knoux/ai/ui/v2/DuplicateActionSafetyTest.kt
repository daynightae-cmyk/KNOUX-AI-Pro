package com.knoux.ai.ui.v2

import org.junit.Assert.*
import org.junit.Test

class DuplicateActionSafetyTest {
    private fun file(uri: String, bytes: Long = 12L) = DuplicateScanFile(uri, uri, bytes, false, null)
    private fun group(kind: DuplicateMatchKind = DuplicateMatchKind.EXACT) =
        DuplicateScanGroup("g", listOf(file("keeper"), file("copy1"), file("copy2")), kind)
    private fun plan() = buildDuplicateDeletePlan(listOf(group()), setOf("copy1", "copy2"), emptyMap())

    @Test fun bulkSelectionNeverIncludesKeeperOrVisualCandidates() {
        val visual = DuplicateScanGroup("v", listOf(file("visual-keeper"), file("similar")), DuplicateMatchKind.VISUAL)
        assertEquals(setOf("copy1", "copy2"), exactDuplicateExtras(listOf(group(), visual), emptyMap()))
    }

    @Test fun choosingAnotherKeeperProtectsThatFile() {
        assertEquals(setOf("keeper", "copy2"), exactDuplicateExtras(listOf(group()), mapOf("g" to "copy1")))
    }

    @Test(expected = IllegalArgumentException::class) fun deletingKeeperIsRejected() {
        buildDuplicateDeletePlan(listOf(group()), setOf("keeper", "copy1"), emptyMap())
    }

    @Test(expected = IllegalArgumentException::class) fun staleUrisAreRejected() {
        buildDuplicateDeletePlan(listOf(group()), setOf("unknown"), emptyMap())
    }

    @Test fun selectedBatchIsBoundedButNeverChangesTheKeeper() {
        val plan = buildDuplicateDeletePlan(listOf(group()), setOf("copy1", "copy2"), emptyMap(), batchLimit = 1)
        assertEquals(1, plan.files.size)
        assertEquals("keeper", plan.keepers.single().uriString)
    }

    @Test fun successRequiresProviderAbsenceAndAStillPresentKeeper() {
        val result = verifyDuplicateDelete(plan(), true, mapOf(
            "keeper" to DuplicateMediaPresence.PRESENT,
            "copy1" to DuplicateMediaPresence.ABSENT,
            "copy2" to DuplicateMediaPresence.ABSENT,
        ))
        assertEquals("VERIFIED", result.status)
        assertEquals(24L, result.deletedBytes)
        assertTrue(result.keepersIntact)
    }

    @Test fun consentIsNotProofOfDeletion() {
        val result = verifyDuplicateDelete(plan(), true, mapOf(
            "keeper" to DuplicateMediaPresence.PRESENT,
            "copy1" to DuplicateMediaPresence.PRESENT,
            "copy2" to DuplicateMediaPresence.PRESENT,
        ))
        assertEquals("NO_CHANGE", result.status)
        assertEquals(0L, result.deletedBytes)
    }

    @Test fun queryFailureNeverBecomesADeletedFile() {
        val result = verifyDuplicateDelete(plan(), true, mapOf(
            "keeper" to DuplicateMediaPresence.PRESENT,
            "copy1" to DuplicateMediaPresence.UNKNOWN,
            "copy2" to DuplicateMediaPresence.ABSENT,
        ))
        assertEquals("PARTIAL", result.status)
        assertEquals(setOf("copy2"), result.deletedUris)
        assertEquals(12L, result.deletedBytes)
        assertEquals(1, result.unknownCount)
    }

    @Test fun missingKeeperPreventsVerifiedEvenWhenEveryCopyIsGone() {
        val result = verifyDuplicateDelete(plan(), true, mapOf(
            "keeper" to DuplicateMediaPresence.UNKNOWN,
            "copy1" to DuplicateMediaPresence.ABSENT,
            "copy2" to DuplicateMediaPresence.ABSENT,
        ))
        assertEquals("PARTIAL", result.status)
        assertFalse(result.keepersIntact)
    }

    @Test fun cancellationCannotClaimBytesEvenIfOtherProcessDeletedFiles() {
        val result = verifyDuplicateDelete(plan(), false, mapOf(
            "keeper" to DuplicateMediaPresence.PRESENT,
            "copy1" to DuplicateMediaPresence.ABSENT,
            "copy2" to DuplicateMediaPresence.ABSENT,
        ))
        assertEquals("CANCELLED", result.status)
        assertEquals(0L, result.deletedBytes)
        assertTrue(result.deletedUris.isEmpty())
    }

    @Test fun missingObservationIsUnknownNotAbsence() {
        val result = verifyDuplicateDelete(plan(), true, mapOf("keeper" to DuplicateMediaPresence.PRESENT))
        assertEquals("PARTIAL", result.status)
        assertEquals(2, result.unknownCount)
        assertTrue(result.deletedUris.isEmpty())
    }
}
