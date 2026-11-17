package com.knoux.ai.ui.v2

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The service header is now a compact Home-style banner: the stag floats
 * behind the identity block and the results start right under it. These tests
 * pin the size contract so a future change cannot turn the header back into a
 * full-screen poster.
 */
class ServiceHeaderLayoutTest {

    private val header = serviceHeaderHeight(maxWidth = 360.dp, compact = false)

    @Test
    fun headerIsAContentBannerNotAPoster() {
        assertTrue("360dp header must stay under 200dp, was $header", header <= 200.dp)
        assertTrue("360dp header must leave the results visible", 800.dp - header >= 600.dp)
    }

    @Test
    fun headerHeightIsMonotonicAndClamped() {
        val narrow = serviceHeaderHeight(maxWidth = 320.dp, compact = false)
        val mid = serviceHeaderHeight(maxWidth = 360.dp, compact = false)
        val wide = serviceHeaderHeight(maxWidth = 412.dp, compact = false)
        assertTrue("header grows with width", wide >= mid && mid >= narrow)
        assertTrue("narrow header keeps the minimum lane", narrow >= 166.dp)
        assertTrue("wide header is capped", wide <= 196.dp)
        assertEquals(120.dp, serviceHeaderHeight(maxWidth = 640.dp, compact = true))
    }

    @Test
    fun stagIsLargeBehindTheHeader() {
        val stag = serviceStagSize(headerHeight = header, maxWidth = 360.dp)
        assertTrue("stag must dominate the header height, was $stag", stag >= header * .90f)
        assertTrue("stag must stay inside the header, was $stag", stag <= header)
        assertTrue("stag must not cover the title lane, was $stag", stag <= 360.dp * .55f)
    }

    @Test
    fun stagIsBoundedOnVeryWideAndVeryShortScreens() {
        val wide = serviceStagSize(headerHeight = 196.dp, maxWidth = 500.dp)
        assertTrue("stag is capped at 194dp, was $wide", wide <= 194.dp)
        val tiny = serviceStagSize(headerHeight = 60.dp, maxWidth = 320.dp)
        assertTrue("stag keeps a visible floor, was $tiny", tiny >= 104.dp)
    }

    @Test
    fun titleFitsOnOneLineAcrossServiceNames() {
        assertTrue(serviceTitleSize(11) >= 26f)
        assertTrue(serviceTitleSize(20) in 23f..25f)
        assertTrue(serviceTitleSize(27) in 21f..22f)
        assertTrue(serviceTitleSize(40) in 19f..20f)
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
        assertEquals("نسخ زائدة", identity.gaugeLabel)
    }
}
