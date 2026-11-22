package com.knoux.ai.ui.v2

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Owner direction (20 Nov): the service page opens on the royal stage from
 * the reference boards — large fixed header with the live instrument at
 * stage scale, the stag behind it, results scrolling beneath. These tests
 * pin the new size contract.
 */
class ServiceHeaderLayoutTest {

    private val header = serviceHeaderHeight(maxWidth = 360.dp, compact = false)

    @Test
    fun headerIsTheRoyalStageWithResultsBeneath() {
        assertTrue("360dp stage must be at least 264dp, was $header", header >= 264.dp)
        assertTrue("360dp stage must stay under 320dp, was $header", header <= 320.dp)
        assertTrue("360dp stage must leave the results visible", 800.dp - header >= 480.dp)
    }

    @Test
    fun headerHeightIsMonotonicAndClamped() {
        val narrow = serviceHeaderHeight(maxWidth = 320.dp, compact = false)
        val mid = serviceHeaderHeight(maxWidth = 360.dp, compact = false)
        val wide = serviceHeaderHeight(maxWidth = 412.dp, compact = false)
        assertTrue("header grows with width", wide >= mid && mid >= narrow)
        assertTrue("narrow header keeps the minimum lane", narrow >= 264.dp)
        assertTrue("wide header is capped", wide <= 312.dp)
        assertEquals(140.dp, serviceHeaderHeight(maxWidth = 640.dp, compact = true))
    }

    @Test
    fun stagFloatsLargeBehindTheStage() {
        val stag = serviceStagSize(headerHeight = header, maxWidth = 360.dp)
        assertTrue("stag must own the stage backdrop, was $stag", stag >= header * .60f)
        assertTrue("stag must stay inside the stage, was $stag", stag <= header)
        assertTrue("stag must keep the title lane open, was $stag", stag <= 360.dp * .65f)
    }

    @Test
    fun stagIsBoundedOnVeryWideAndVeryShortScreens() {
        val wide = serviceStagSize(headerHeight = 312.dp, maxWidth = 500.dp)
        assertTrue("stag is capped at 252dp, was $wide", wide <= 252.dp)
        val tiny = serviceStagSize(headerHeight = 60.dp, maxWidth = 320.dp)
        assertTrue("stag keeps a visible floor, was $tiny", tiny >= 120.dp)
    }

    @Test
    fun titleFitsOnOneLineAcrossServiceNames() {
        assertTrue(serviceTitleSize(11) >= 27f)
        assertTrue(serviceTitleSize(20) in 24f..26f)
        assertTrue(serviceTitleSize(27) in 22f..23f)
        assertTrue(serviceTitleSize(40) in 20f..21f)
        assertTrue("landscape title is shorter", compactServiceTitleSize(20) < serviceTitleSize(20))
    }

    @Test
    fun duplicateGaugeShowsRedundantShareInsteadOfZero() {
        val groups = listOf(
            MediaDuplicateGroup("a", 100L, 3, listOf("u1", "u2", "u3"), false),
            MediaDuplicateGroup("b", 50L, 2, listOf("u4", "u5"), false),
        )
        val facts = DeviceFacts(
            media = MediaFacts(
                permissionGranted = true,
                images = 10,
                imagesBytes = 2_000L,
                duplicateGroups = groups,
                duplicateCandidates = groups.sumOf { it.count },
            ),
            computedAt = 1L,
        )
        val identity = sectionIdentity("duplicates", facts)
        // 5 candidates across 2 groups => 3 redundant copies => 60%.
        assertEquals(60, identity.gaugePercent)
        assertEquals("مساحة قابلة للاستعادة", identity.gaugeLabel)
        assertEquals("250 B", identity.gaugeValue)
    }
}
