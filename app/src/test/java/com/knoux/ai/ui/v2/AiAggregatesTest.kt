package com.knoux.ai.ui.v2

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Sec9 AI priority aggregates: deterministic scores over the same real
 * readings the recommendation engine consumes. The hub gauge band, the four
 * panel lines and the weekly strip must agree with these — and an empty or
 * healthy device must never invent urgency.
 */
class AiAggregatesTest {

    @Test
    fun cleanPriorityRisesWithStoragePressureCacheAndResidue() {
        assertEquals(40, aiCleanPriority(0.2f, 0L, 0L))
        assertEquals(52, aiCleanPriority(0.6f, 0L, 0L))
        assertEquals(65, aiCleanPriority(0.8f, 0L, 0L))
        assertEquals(75, aiCleanPriority(0.9f, 0L, 0L))
        // cache + residue weights stack on top of the pressure band
        assertEquals(93, aiCleanPriority(0.9f, 60L * 1024 * 1024, 1L))
        assertEquals(53, aiCleanPriority(0.2f, 1L, 1L))
    }

    @Test
    fun perfPriorityCombinesRamStorageAndLowBattery() {
        assertEquals(45, aiPerfPriority(0.2f, 0.2f, null))
        assertEquals(53, aiPerfPriority(0.5f, 0.2f, 80))
        assertEquals(60, aiPerfPriority(0.7f, 0.2f, 80))
        assertEquals(70, aiPerfPriority(0.85f, 0.2f, 80))
        assertEquals(78, aiPerfPriority(0.85f, 0.8f, 80))
        assertEquals(95, aiPerfPriority(0.85f, 0.9f, 20))
        // null battery level must not add the low-battery weight
        assertEquals(85, aiPerfPriority(0.85f, 0.9f, null))
    }

    @Test
    fun batteryPriorityRewardsFullCoolHealthyDevice() {
        assertEquals(40, aiBatteryPriority(90, 30f, true))
        assertEquals(50, aiBatteryPriority(50, 30f, true))
        assertEquals(40, aiBatteryPriority(51, 30f, true))
        assertEquals(63, aiBatteryPriority(30, 30f, true))
        assertEquals(75, aiBatteryPriority(15, 30f, true))
        assertEquals(98, aiBatteryPriority(15, 43f, false))
        // unknown level/temperature: only the health weight applies
        assertEquals(43, aiBatteryPriority(null, null, false))
        assertEquals(35, aiBatteryPriority(null, null, true))
    }

    @Test
    fun privacyPriorityAddsMissingLockOnTopOfRiskPercent() {
        assertEquals(0, aiPrivacyPriority(0, 100, true))
        assertEquals(15, aiPrivacyPriority(0, 100, false))
        assertEquals(18, aiPrivacyPriority(48, 265, true))
        assertEquals(33, aiPrivacyPriority(48, 265, false))
        assertEquals(99, aiPrivacyPriority(300, 265, false))
    }

    @Test
    fun scoreBandsMatchApprovedArtLabels() {
        assertEquals("ممتاز", aiScoreLabel(80))
        assertEquals("ممتاز", aiScoreLabel(100))
        assertEquals("جيد", aiScoreLabel(60))
        assertEquals("جيد", aiScoreLabel(79))
        assertEquals("يحتاج عناية", aiScoreLabel(59))
        assertEquals("يحتاج عناية", aiScoreLabel(0))
    }

    @Test
    fun weeklyInsightsCountOnlyLastSevenDays() {
        val day = 24L * 3600 * 1000
        val now = 100 * day
        fun entry(ageDays: Long) = ScanHistoryEntry(
            timestamp = now - ageDays * day, appsScanned = 1, privacyFindings = 0,
            cacheBytes = 0L, storageUsedBytes = 0L, batteryLevel = null,
        )
        val history = listOf(entry(0), entry(3), entry(7), entry(8), entry(30))
        assertEquals(3, weeklyInsightCount(history, now))
        assertEquals(0, weeklyInsightCount(emptyList(), now))
        // exactly seven days old still counts; one ms older does not
        assertEquals(1, weeklyInsightCount(listOf(entry(7)), now))
        assertEquals(0, weeklyInsightCount(listOf(ScanHistoryEntry(now - 7 * day - 1, 1, 0, 0L, 0L, null)), now))
    }
}
