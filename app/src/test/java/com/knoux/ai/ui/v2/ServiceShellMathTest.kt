package com.knoux.ai.ui.v2

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ServiceShellMathTest {

    @Test
    fun headerHeight_staysCompactAt360dp() {
        // On a 360dp phone the Home-style header is 176dp; results get the rest.
        val height = serviceHeaderHeight(maxWidth = 360.dp, compact = false)
        assertTrue("360dp header must be ~176dp, was $height", height in 166.dp..196.dp)
        assertTrue("360dp header must leave the results screen, was $height", 800.dp - height >= 600.dp)
    }

    @Test
    fun headerHeight_keepsResultsReachableAt320dp() {
        val height = serviceHeaderHeight(maxWidth = 320.dp, compact = false)
        assertTrue("320dp header clamps to the 166dp minimum, was $height", height >= 166.dp)
        assertTrue("320dp window keeps a usable list", 640.dp - height >= 450.dp)
    }

    @Test
    fun headerHeight_scalesUpOnWidePhones() {
        val height = serviceHeaderHeight(maxWidth = 412.dp, compact = false)
        assertTrue("412dp header must be 196dp, was $height", height in 190.dp..196.dp)
        assertTrue("412dp header must never exceed 196dp, was $height", height <= 196.dp)
    }

    @Test
    fun headerHeight_usesAShorterLandscapeLane() {
        assertEquals(120.dp, serviceHeaderHeight(maxWidth = 720.dp, compact = true))
        assertTrue(
            "landscape header is shorter than portrait",
            serviceHeaderHeight(maxWidth = 720.dp, compact = true) <
                serviceHeaderHeight(maxWidth = 360.dp, compact = false),
        )
    }

    @Test
    fun stag_scalesWithHeaderAndStaysInsideTheWidth() {
        val header = serviceHeaderHeight(maxWidth = 360.dp, compact = false)
        val stag = serviceStagSize(headerHeight = header, maxWidth = 360.dp)
        assertTrue("stag must be large behind the header, was $stag", stag >= 150.dp)
        assertTrue("stag must not exceed the header, was $stag", stag <= header)
        assertTrue("stag must not cross half the width, was $stag", stag <= 360.dp * .55f)
    }

    @Test
    fun titleSize_keepsLongServiceNamesInTheCompactHeader() {
        assertTrue(serviceTitleSize(12) >= 26f)
        assertTrue(serviceTitleSize(24) in 21f..24f)
        assertTrue(serviceTitleSize(40) <= 20f)
        assertTrue(compactServiceTitleSize(40) < serviceTitleSize(40))
    }

    @Test
    fun sonarAngle_rotatesForward() {
        val angle = sonarNextAngle(current = 0f, deltaMs = 1000f, degreesPerMs = .075f)
        assertEquals(75f, angle, 0.001f)
    }

    @Test
    fun sonarAngle_wrapsAtFullTurn() {
        val angle = sonarNextAngle(current = 350f, deltaMs = 1000f, degreesPerMs = .075f)
        assertEquals(65f, angle, 0.001f)
    }

    @Test
    fun sonarAngle_ignoresInvalidFrameDeltas() {
        assertEquals(42f, sonarNextAngle(42f, 0f, .075f), 0.001f)
        assertEquals(42f, sonarNextAngle(42f, Float.NaN, .075f), 0.001f)
        assertEquals(42f, sonarNextAngle(42f, -20f, .075f), 0.001f)
    }

    @Test
    fun blipLightsOnlyWhileSweepIsNearIt() {
        val lit = sonarBlipAlpha(blipAngle = 34f, sweepAngle = 40f, active = true)
        val idle = sonarBlipAlpha(blipAngle = 34f, sweepAngle = 200f, active = true)
        val stopped = sonarBlipAlpha(blipAngle = 34f, sweepAngle = 40f, active = false)
        assertTrue("blip must light when the sweep is close", lit > .8f)
        assertTrue("blip must dim when the sweep is far", idle < .2f)
        assertEquals(.16f, stopped, 0.001f)
    }

    @Test
    fun blipHandlesSweepWrappingPastZero() {
        val justAfterWrap = sonarBlipAlpha(blipAngle = 350f, sweepAngle = 6f, active = true)
        assertTrue("blip at 350 must light just after the sweep wraps", justAfterWrap > .2f)
    }

    @Test
    fun completedStagesReflectsRealState() {
        assertEquals(0, DeepScanUiState.Idle().completedStages())
        assertEquals(3, DeepScanUiState.Cancelled(3, DeviceFacts()).completedStages())
        assertEquals(
            6,
            DeepScanUiState.Completed(DeviceFacts(), completedAt = 1L).completedStages(),
        )
        assertEquals(
            2,
            DeepScanUiState.Running(2, DeepScanStage.PRIVACY, DeviceFacts()).completedStages(),
        )
    }
}
