package com.knoux.ai.ui.v2

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ServiceShellMathTest {

    @Test
    fun heroHeight_givesRealRoomAt360dp() {
        // On a 360dp phone the hero is 374dp, leaving ~286dp for results + CTA.
        val height = serviceHeroHeight(maxWidth = 360.dp)
        assertTrue("360dp hero must be ~374dp, was $height", height in 370.dp..379.dp)
        assertTrue("360dp hero must leave room for results, was $height", 660.dp - height >= 250.dp)
    }

    @Test
    fun heroHeight_keepsResultsReachableAt320dp() {
        val height = serviceHeroHeight(maxWidth = 320.dp)
        assertTrue("320dp hero clamps to minimum 340dp, was $height", height >= 340.dp)
        assertTrue("320dp window keeps a usable list", 520.dp - height >= 150.dp)
    }

    @Test
    fun heroHeight_scalesUpOnWidePhones() {
        val height = serviceHeroHeight(maxWidth = 412.dp)
        assertTrue("412dp hero must be ~428dp, was $height", height in 420.dp..430.dp)
        assertTrue("412dp hero must never exceed 430dp, was $height", height <= 430.dp)
    }

    @Test
    fun heroHeight_growsWithWidthAndClamps() {
        val narrow = serviceHeroHeight(maxWidth = 320.dp) // 332.8 -> clamped to 340
        val wide = serviceHeroHeight(maxWidth = 412.dp)   // 428.48
        assertTrue("hero grows with width, narrow=$narrow wide=$wide", wide > narrow)
        assertTrue("narrow width clamps to the 340dp minimum, was $narrow", narrow == 340.dp)
    }

    @Test
    fun titleSize_keepsLongServiceNamesOnTheHero() {
        assertTrue(serviceTitleSize(12) >= 26f)
        assertTrue(serviceTitleSize(24) in 21f..24f)
        assertTrue(serviceTitleSize(40) <= 21f)
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
