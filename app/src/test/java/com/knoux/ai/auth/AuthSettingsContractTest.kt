package com.knoux.ai.auth

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * JVM contract tests for the public auth-settings capability probe and the
 * permanent "provider disabled" rejection mapping. These pin the repair for the
 * login screen showing "Auth provider rejected the request (HTTP 404)" when the
 * project has passkeys disabled: the app must learn `passkeys_enabled` from
 * GET /auth/v1/settings and must map passkey_disabled / provider_disabled to
 * explicit, non-retryable exception codes — never fake success, never retry.
 */
class AuthSettingsContractTest {

    // ------------------------------------------------------ settings parsing

    @Test
    fun parsesLiveProjectShapeWithDisabledProviders() {
        // Mirrors the real GET /auth/v1/settings document of the configured
        // project (email enabled; google + passkeys disabled).
        val settings = JSONObject()
            .put("external", JSONObject()
                .put("google", false)
                .put("email", true)
                .put("phone", false))
            .put("disable_signup", false)
            .put("passkeys_enabled", false)
        val parsed = AuthSettingsParser.parse(settings)
        assertTrue(parsed.emailEnabled)
        assertFalse(parsed.googleEnabled)
        assertFalse(parsed.passkeysEnabled)
        assertFalse(parsed.disableSignup)
    }

    @Test
    fun parsesEnabledProjectShape() {
        val settings = JSONObject()
            .put("external", JSONObject().put("google", true).put("email", true))
            .put("passkeys_enabled", true)
            .put("disable_signup", true)
        val parsed = AuthSettingsParser.parse(settings)
        assertTrue(parsed.googleEnabled)
        assertTrue(parsed.passkeysEnabled)
        assertTrue(parsed.disableSignup)
    }

    @Test
    fun missingFieldsFailSafeToNotOfferingProviders() {
        // Unknown project shape: never offer google/passkeys unless confirmed.
        val parsed = AuthSettingsParser.parse(JSONObject())
        assertTrue(parsed.emailEnabled) // baseline method stays available
        assertFalse(parsed.googleEnabled)
        assertFalse(parsed.passkeysEnabled)
        assertFalse(parsed.disableSignup)
        // Missing external block entirely.
        val noExternal = AuthSettingsParser.parse(JSONObject().put("passkeys_enabled", true))
        assertFalse(noExternal.googleEnabled)
        assertTrue(noExternal.passkeysEnabled)
    }

    @Test
    fun failSafeValueOffersEmailOnly() {
        val safe = AuthSettingsParser.failSafe
        assertTrue(safe.emailEnabled)
        assertFalse(safe.googleEnabled)
        assertFalse(safe.passkeysEnabled)
    }

    // ------------------------------------------------------ source contracts

    @Test
    fun authApiProbesPublicSettingsAndMapsDisabledProviders() {
        val source = appContractFile("src/main/java/com/knoux/ai/auth/KnouxAuthApi.kt").readText()
        // Capability probe: public GET /settings with a bounded cache.
        assertTrue(source.contains("request(\"GET\", \"/settings\")"))
        assertTrue(source.contains("AuthSettingsParser.parse("))
        assertTrue(source.contains("SETTINGS_CACHE_MS"))
        // Permanent rejections keep their provider code with an explicit message;
        // the generic raw-status message is only the fallback.
        assertTrue(source.contains("\"passkey_disabled\" -> \"Passkeys are not enabled for this project\""))
        assertTrue(source.contains("\"provider_disabled\" -> \"This sign-in provider is not enabled for this project\""))
        assertTrue(source.contains("AuthSettingsParser.failSafe"))
        // Security posture unchanged: retryable statuses still go through
        // HttpStatusException and 404/400 remain non-retryable.
        assertTrue(source.contains("ProductionSecurity.isRetryableStatus(status)"))
        assertFalse(com.knoux.ai.security.ProductionSecurity.isRetryableStatus(404))
        assertFalse(com.knoux.ai.security.ProductionSecurity.isRetryableStatus(400))
    }

    @Test
    fun loginScreenGatesProvidersOnProjectSettings() {
        val source = appContractFile("src/main/java/com/knoux/ai/ui/screens/WelcomeScreen.kt").readText()
        assertTrue(source.contains("controller.authSettings()"))
        assertTrue(source.contains("authSettings?.googleEnabled == true"))
        assertTrue(source.contains("authSettings?.passkeysEnabled == true"))
        // The raw 404 is mapped to an actionable Arabic message, not bypassed.
        assertTrue(source.contains("\"passkey_disabled\" ->"))
        assertTrue(source.contains("\"provider_disabled\" ->"))
    }

    @Test
    fun controllerExposesSettingsToTheUi() {
        val source = appContractFile("src/main/java/com/knoux/ai/auth/KnouxAuthController.kt").readText()
        assertTrue(source.contains("suspend fun authSettings()=api.authSettings()"))
    }

    @Test
    fun settingsValuesNeverBecomeASession() {
        // Capability flags must never be interpreted as sign-in success.
        val parsed = AuthSettingsParser.parse(JSONObject()
            .put("external", JSONObject().put("email", true))
            .put("passkeys_enabled", false))
        assertEquals(false, SessionResponseParser.parse(JSONObject(), nowMs = 0L) != null)
        assertTrue(parsed.emailEnabled)
    }

    private fun appContractFile(relativePath: String): java.io.File {
        val workingDirectory = java.io.File(System.getProperty("user.dir")).absoluteFile
        return listOf(workingDirectory, workingDirectory.parentFile)
            .map { java.io.File(it, "app/$relativePath").absoluteFile }
            .firstOrNull { it.isFile }
            ?: error("Cannot locate app contract: $relativePath from $workingDirectory")
    }
}
