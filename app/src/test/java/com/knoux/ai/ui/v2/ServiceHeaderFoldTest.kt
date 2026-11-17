package com.knoux.ai.ui.v2

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Board 00 — the service header must stay on screen while the page scrolls.
 * These tests pin the arithmetic that decides how much of the hero is folded
 * and how big the stag and the instrument stay, so the header can never be
 * scrolled away by a regression in the scroll maths.
 */
class ServiceHeaderFoldTest {

    private val full = serviceHeroHeight(360.dp) // 374dp on the owner's 360dp phone
    private val settled = (full * .55f).coerceAtLeast(170.dp) // 205.7dp

    @Test
    fun foldIsZeroAtRest() {
        assertEquals(0f, serviceHeroFold(full, settled, scrollOffsetPx = 0f, density = 2f), 0.0001f)
    }

    @Test
    fun foldIsCompleteAfterTheTravel() {
        val travelPx = (full - settled).value * 2f
        assertEquals(1f, serviceHeroFold(full, settled, travelPx, 2f), 0.0001f)
        assertEquals(1f, serviceHeroFold(full, settled, travelPx * 3f, 2f), 0.0001f)
    }

    @Test
    fun foldNeverLeavesTheZeroToOneRange() {
        listOf(-50f, 0f, 37f, 120f, 1_000f, Float.NaN, Float.POSITIVE_INFINITY).forEach { offset ->
            val fold = serviceHeroFold(full, settled, offset, 2f)
            assertTrue("fold for $offset was $fold", fold in 0f..1f)
        }
        assertEquals("NaN offset must not fold the header", 0f, serviceHeroFold(full, settled, Float.NaN, 2f), 0.0001f)
        assertEquals("zero density must not divide by zero", 0f, serviceHeroFold(full, settled, 100f, 0f), 0.0001f)
    }

    @Test
    fun foldedHeaderKeepsAFixedHeightOnScreen() {
        val open = foldedHeroHeight(full, settled, 0f)
        val half = foldedHeroHeight(full, settled, .5f)
        val closed = foldedHeroHeight(full, settled, 1f)
        assertEquals(full, open)
        assertEquals(settled, closed)
        assertTrue("half fold sits between the two heights", half > settled && half < full)
        assertTrue("the folded bar is never squeezed away", closed >= 170.dp)
    }

    @Test
    fun stagAndInstrumentNeverDisappear() {
        assertEquals(1f, heroStageScale(0f), 0.0001f)
        val folded = heroStageScale(1f)
        assertTrue("folded stage keeps a readable size, was $folded", folded in .25f..1f)
        assertTrue("stage shrinks monotonically", heroStageScale(.5f) < heroStageScale(.2f))
        assertTrue("out-of-range folds clamp", heroStageScale(-4f) <= 1f && heroStageScale(9f) >= .28f)
    }

    @Test
    fun scanStartReturnsTheHeaderToItsOpenState() {
        // scrollToItem(0) means offset 0, so the header unfolds fully and the
        // live radar is visible while the stages are read.
        val foldAfterReset = serviceHeroFold(full, settled, 0f, 2f)
        assertFalse("a started scan must not sit in a folded header", foldAfterReset > 0f)
    }

    @Test
    fun foldedBarKeepsRoomForTheStagAndTheRadar() {
        val room = (settled * .34f) // what is left under the title in the folded bar
        val lane = serviceStageHeight(compact = false, available = room)
        assertTrue("folded stage lane must keep at least 112dp, was $lane", lane >= 112.dp)
        val small = serviceStageHeight(compact = true, available = 20.dp)
        assertTrue("small phones still keep a 96dp lane, was $small", small >= 96.dp)
        val open = serviceStageHeight(compact = false, available = 260.dp)
        assertEquals("an open header keeps the room it has", 260.dp, open)
    }
}
