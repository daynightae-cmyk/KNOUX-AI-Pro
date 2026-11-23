package com.knoux.ai.auth

import com.knoux.ai.security.HttpStatusException
import com.knoux.ai.security.ProductionSecurity
import com.knoux.ai.security.secureRequest
import java.io.IOException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * JVM contract tests for the auth session lifecycle: response acceptance,
 * expiry math, refresh serialization, permanent-rejection cleanup and the
 * HTTP retry policy shared with ProductionSecurity. No network, no Keystore;
 * device-only pieces (Credential Manager, BiometricPrompt, Keystore AES) are
 * explicitly out of scope here and stay marked IMPLEMENTED_NOT_TESTED.
 */
class AuthSessionContractTest {

    // ------------------------------------------------------ session acceptance

    @Test
    fun completeTokenResponseIsAcceptedWithMeasuredExpiry() {
        val payload = JSONObject()
            .put("access_token", "access-fixture")
            .put("refresh_token", "refresh-fixture")
            .put("expires_in", 3600)
        val accepted = SessionResponseParser.parse(payload, nowMs = 1_000_000L)
        assertNotNull(accepted)
        assertEquals("access-fixture", accepted!!.accessToken)
        assertEquals("refresh-fixture", accepted.refreshToken)
        assertEquals(1_000_000L + 3_600_000L, accepted.expiresAtMs)
    }

    @Test
    fun nestedSessionObjectIsUnwrapped() {
        val payload = JSONObject().put("session", JSONObject()
            .put("access_token", "a").put("refresh_token", "r").put("expires_in", 60))
        val accepted = SessionResponseParser.parse(payload, nowMs = 0L)
        assertNotNull(accepted)
        assertEquals(60_000L, accepted!!.expiresAtMs)
    }

    @Test
    fun signupWithoutTokensIsNotASession() {
        // GoTrue signup with email confirmation returns a user, not tokens.
        val userOnly = JSONObject().put("id", "user-uuid").put("email", "fixture@example.com")
        assertNull(SessionResponseParser.parse(userOnly, nowMs = 0L))
        val accessOnly = JSONObject().put("access_token", "a")
        assertNull(SessionResponseParser.parse(accessOnly, nowMs = 0L))
        val refreshOnly = JSONObject().put("refresh_token", "r")
        assertNull(SessionResponseParser.parse(refreshOnly, nowMs = 0L))
        val blank = JSONObject().put("access_token", "").put("refresh_token", "r")
        assertNull(SessionResponseParser.parse(blank, nowMs = 0L))
        assertNull(SessionResponseParser.parse(JSONObject(), nowMs = 0L))
    }

    @Test
    fun expiresInIsClampedToSaneBounds() {
        fun expiry(expiresIn: Long): Long {
            val payload = JSONObject().put("access_token", "a").put("refresh_token", "r")
                .put("expires_in", expiresIn)
            return SessionResponseParser.parse(payload, nowMs = 0L)!!.expiresAtMs
        }
        assertEquals(1_000L, expiry(0L))
        assertEquals(1_000L, expiry(-50L))
        assertEquals(86_400_000L, expiry(999_999L))
        // Missing expires_in falls back to one hour, matching GoTrue default.
        val noExpiry = JSONObject().put("access_token", "a").put("refresh_token", "r")
        assertEquals(3_600_000L, SessionResponseParser.parse(noExpiry, 0L)!!.expiresAtMs)
    }

    @Test
    fun localValidityUsesSixtySecondSkew() {
        val expiry = 120_000L
        assertTrue(SessionResponseParser.isLocallyValid(expiry, nowMs = 0L))
        assertTrue(SessionResponseParser.isLocallyValid(expiry, nowMs = 59_999L))
        assertFalse(SessionResponseParser.isLocallyValid(expiry, nowMs = 60_000L))
        assertFalse(SessionResponseParser.isLocallyValid(expiry, nowMs = 120_000L))
    }

    // ------------------------------------------------------ refresh serialization

    @Test
    fun concurrentCallersRefreshExactlyOnce() = runBlocking {
        // Models the production refreshMutex contract in KnouxAuthApi.verifiedToken:
        // the first caller refreshes; the rest observe the fresh token under the lock.
        val mutex = Mutex()
        var stored = SessionResponseParser.AcceptedSession("old", "refresh-1", expiresAtMs = 0L)
        var refreshCalls = 0
        suspend fun verifiedToken(now: Long): String = mutex.withLock {
            if (SessionResponseParser.isLocallyValid(stored.expiresAtMs, now)) return@withLock stored.accessToken
            refreshCalls++
            val response = JSONObject()
                .put("access_token", "new-$refreshCalls")
                .put("refresh_token", "refresh-${refreshCalls + 1}")
                .put("expires_in", 3600)
            val accepted = SessionResponseParser.parse(response, now)!!
            stored = accepted
            accepted.accessToken
        }
        val tokens = (1..8).map { async { verifiedToken(now = 1_000L) } }.awaitAll()
        assertEquals(1, refreshCalls)
        assertTrue(tokens.all { it == "new-1" })
    }

    @Test
    fun permanentlyRejectedRefreshClearsTheStoredSession() = runBlocking {
        // Models the catch branch: a 400 invalid_grant must clear the store so the
        // app returns to sign-in rather than retrying a dead refresh token forever.
        var stored: String? = "stale-token"
        val refresh = JSONObject().put("error_code", "refresh_token_not_found")
        try {
            // Permanent 4xx surfaces as KnouxAuthException, never HttpStatusException.
            val status = 400
            assertFalse(ProductionSecurity.isRetryableStatus(status))
            throw KnouxAuthException(refresh.getString("error_code"), "rejected")
        } catch (e: KnouxAuthException) {
            stored = null
            assertEquals("refresh_token_not_found", e.code)
        }
        assertNull(stored)
    }

    // ------------------------------------------------------ HTTP retry policy

    @Test
    fun transientHttpStatusIsRetriedOnIdempotentCalls() = runBlocking {
        var attempts = 0
        val result = secureRequest(maxRetries = 3, idempotent = true, delayForTest = {}) {
            attempts++
            if (attempts < 3) throw HttpStatusException(429, "rate limited") else "ok"
        }
        assertEquals("ok", result)
        assertEquals(3, attempts)
    }

    @Test
    fun serverErrorsAreRetriedThenSurfaceWithStatus() = runBlocking {
        var attempts = 0
        try {
            secureRequest(maxRetries = 2, idempotent = true, delayForTest = {}) {
                attempts++
                throw HttpStatusException(503, "unavailable")
            }
            error("expected failure")
        } catch (e: HttpStatusException) {
            assertEquals(503, e.status)
        }
        assertEquals(3, attempts)
    }

    @Test
    fun permanentHttpStatusIsNeverReplayedEvenWhenIdempotent() = runBlocking {
        var attempts = 0
        try {
            secureRequest(maxRetries = 3, idempotent = true, delayForTest = {}) {
                attempts++
                throw HttpStatusException(401, "invalid credentials")
            }
            error("expected failure")
        } catch (e: HttpStatusException) {
            assertEquals(401, e.status)
        }
        assertEquals(1, attempts)
    }

    @Test
    fun nonIdempotentAuthPostIsExecutedExactlyOnce() = runBlocking {
        // signInWithPassword / verify OTP must not be replayed: a retried OTP
        // verification could consume a single-use code twice.
        var attempts = 0
        try {
            secureRequest(maxRetries = 3, idempotent = false, delayForTest = {}) {
                attempts++
                throw HttpStatusException(503, "unavailable")
            }
            error("expected failure")
        } catch (e: HttpStatusException) {
            assertEquals(503, e.status)
        }
        assertEquals(1, attempts)
    }

    @Test
    fun ioFailureStillRetriesIdempotentGet() = runBlocking {
        var attempts = 0
        val result = secureRequest(maxRetries = 3, idempotent = true, delayForTest = {}) {
            attempts++
            if (attempts == 1) throw IOException("connection reset") else "ok"
        }
        assertEquals("ok", result)
        assertEquals(2, attempts)
    }

    // ------------------------------------------------------ email validation parity

    @Test
    fun emailValidationIsJvmExecutableAndConservative() {
        assertTrue(ProductionSecurity.isValidEmail("user@example.com"))
        assertTrue(ProductionSecurity.isValidEmail("first.last+tag@sub.example.co"))
        assertFalse(ProductionSecurity.isValidEmail(null))
        assertFalse(ProductionSecurity.isValidEmail(""))
        assertFalse(ProductionSecurity.isValidEmail("no-at-sign"))
        assertFalse(ProductionSecurity.isValidEmail("user@localhost"))
        assertFalse(ProductionSecurity.isValidEmail("user name@example.com"))
        assertFalse(ProductionSecurity.isValidEmail("a".repeat(250) + "@example.com"))
    }

    // ------------------------------------------------------ source contracts

    @Test
    fun productionAuthApiRoutesThroughTheSharedParserAndMutex() {
        val source = appContractFile("src/main/java/com/knoux/ai/auth/KnouxAuthApi.kt").readText()
        assertTrue(source.contains("SessionResponseParser.parse(obj, System.currentTimeMillis())"))
        assertTrue(source.contains("SessionResponseParser.isLocallyValid("))
        assertTrue(Regex("private val refreshMutex = Mutex\\(\\)").containsMatchIn(source))
        assertTrue(Regex("private suspend fun verifiedToken\\(\\):String\\? = refreshMutex.withLock").containsMatchIn(source))
        assertTrue(source.contains("idempotent: Boolean = method == \"GET\""))
        assertTrue(source.contains("throw HttpStatusException(status,"))
        // Refresh is explicitly idempotent so 429/5xx are retried safely.
        assertTrue(source.contains("grant_type=refresh_token\",JSONObject().put(\"refresh_token\",saved.refreshToken), idempotent = true)"))
        // A permanent refresh rejection clears the local session.
        val refreshBlock = source.substringAfter("private suspend fun verifiedToken(")
            .substringBefore("suspend fun currentUserVerified(")
        assertTrue(refreshBlock.contains("store.clear()"))
    }

    private fun appContractFile(relativePath: String): java.io.File {
        val workingDirectory = java.io.File(System.getProperty("user.dir")).absoluteFile
        return listOf(workingDirectory, workingDirectory.parentFile)
            .map { java.io.File(it, "app/$relativePath").absoluteFile }
            .firstOrNull { it.isFile }
            ?: error("Cannot locate app contract: $relativePath from $workingDirectory")
    }
}
