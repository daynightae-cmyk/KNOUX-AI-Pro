package com.knoux.ai.ui.v2

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ServiceShellMathTest {

    @Test
    fun headerHeight_isTheRoyalStageAt360dp() {
        // Owner direction (20 Nov): the reference stage — ~288dp on a 360dp
        // phone — with results scrolling beneath.
        val height = serviceHeaderHeight(maxWidth = 360.dp, compact = false)
        assertTrue("360dp stage must be ~288dp, was $height", height in 264.dp..312.dp)
        assertTrue("360dp stage must leave the results screen, was $height", 800.dp - height >= 480.dp)
    }

    @Test
    fun headerHeight_keepsResultsReachableAt320dp() {
        val height = serviceHeaderHeight(maxWidth = 320.dp, compact = false)
        assertTrue("320dp stage clamps to the 264dp minimum, was $height", height >= 264.dp)
        assertTrue("320dp window keeps a usable list", 640.dp - height >= 360.dp)
    }

    @Test
    fun headerHeight_scalesUpOnWidePhones() {
        val height = serviceHeaderHeight(maxWidth = 412.dp, compact = false)
        assertTrue("412dp stage must be capped at 312dp, was $height", height in 300.dp..312.dp)
        assertTrue("412dp stage must never exceed 312dp, was $height", height <= 312.dp)
    }

    @Test
    fun headerHeight_usesAShorterLandscapeLane() {
        assertEquals(140.dp, serviceHeaderHeight(maxWidth = 720.dp, compact = true))
        assertTrue(
            "landscape header is shorter than portrait",
            serviceHeaderHeight(maxWidth = 720.dp, compact = true) <
                serviceHeaderHeight(maxWidth = 360.dp, compact = false),
        )
    }

    @Test
    fun stag_scalesWithStageAndStaysInsideTheWidth() {
        val header = serviceHeaderHeight(maxWidth = 360.dp, compact = false)
        val stag = serviceStagSize(headerHeight = header, maxWidth = 360.dp)
        assertTrue("stag must float large behind the stage, was $stag", stag >= 200.dp)
        assertTrue("stag must not exceed the stage, was $stag", stag <= header)
        assertTrue("stag keeps the title lane open, was $stag", stag <= 360.dp * .65f)
    }

    @Test
    fun titleSize_keepsLongServiceNamesReadableOnTheStage() {
        assertTrue(serviceTitleSize(12) >= 27f)
        assertTrue(serviceTitleSize(24) in 22f..25f)
        assertTrue(serviceTitleSize(40) <= 21f)
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
