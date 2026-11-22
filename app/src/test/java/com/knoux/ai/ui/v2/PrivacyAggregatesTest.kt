package com.knoux.ai.ui.v2

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Sec7 privacy aggregates: pure functions over the real package list and
 * platform state. The hub, the panel strip and the service screens must all
 * agree — and an empty device must yield an empty plan, never a fixture.
 */
class PrivacyAggregatesTest {

    private fun app(vararg perms: String) = InstalledAppFacts(
        label = "app", packageName = "p${perms.hashCode()}", versionName = null,
        isSystem = false, isEnabled = true, icon = null,
        declaredPermissions = perms.toList(),
        sensitivePermissions = perms.filter { it in listOf("android.permission.CAMERA", "android.permission.READ_SMS") },
    )

    @Test
    fun overlayCountOnlyCountsSystemAlertWindowDeclarations() {
        val apps = listOf(
            app("android.permission.SYSTEM_ALERT_WINDOW"),
            app("android.permission.CAMERA"),
            app("android.permission.SYSTEM_ALERT_WINDOW", "android.permission.INTERNET"),
        )
        assertEquals(2, overlayDeclaredCount(apps))
    }

    @Test
    fun captureRiskCountsCameraMicAndProjectionOncePerApp() {
        val apps = listOf(
            app("android.permission.CAMERA", "android.permission.RECORD_AUDIO"),
            app("android.permission.FOREGROUND_SERVICE_MEDIA_PROJECTION"),
            app("android.permission.INTERNET"),
        )
        assertEquals(2, captureRiskCount(apps))
    }

    @Test
    fun trackerCountCountsAdIdOrLocationDeclarations() {
        val apps = listOf(
            app("com.google.android.gms.permission.AD_ID"),
            app("android.permission.ACCESS_FINE_LOCATION"),
            app("android.permission.ACCESS_COARSE_LOCATION", "com.google.android.gms.permission.AD_ID"),
            app("android.permission.CAMERA"),
        )
        assertEquals(3, trackerDeclaredCount(apps))
    }

    @Test
    fun clipboardAlertsOnlyCountBackgroundClipboardDeclarations() {
        val apps = listOf(
            app("android.permission.READ_CLIPBOARD_IN_BACKGROUND"),
            app("android.permission.CAMERA"),
        )
        assertEquals(1, clipboardAlertCount(apps))
    }

    @Test
    fun riskPercentIsSensitiveOverInstalledAndClamped() {
        assertEquals(0, privacyRiskPercent(0, 0))
        assertEquals(0, privacyRiskPercent(5, 0))
        assertEquals(18, privacyRiskPercent(48, 265))
        assertEquals(100, privacyRiskPercent(300, 265))
    }

    @Test
    fun riskBandsMatchApprovedArtLabels() {
        assertEquals("منخفض", privacyRiskLabel(0))
        assertEquals("منخفض", privacyRiskLabel(25))
        assertEquals("متوسط", privacyRiskLabel(26))
        assertEquals("متوسط", privacyRiskLabel(60))
        assertEquals("مرتفع", privacyRiskLabel(61))
    }

    @Test
    fun actionPlanCountsOneItemPerRealFinding() {
        // sensitive + overlay + capture + no lock + no usage = 5
        assertEquals(5, privacyActionPlanCount(6, 3, 4, lockSecure = false, usageGranted = false))
        // only sensitive apps remain
        assertEquals(1, privacyActionPlanCount(6, 0, 0, lockSecure = true, usageGranted = true))
        // clean device: empty plan, never a fixture
        assertEquals(0, privacyActionPlanCount(0, 0, 0, lockSecure = true, usageGranted = true))
    }
}